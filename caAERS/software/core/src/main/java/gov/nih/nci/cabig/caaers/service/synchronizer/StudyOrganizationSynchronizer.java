/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudyOrganizationSynchronizer implements Synchronizer<gov.nih.nci.cabig.caaers.domain.Study>{

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	public void migrate(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {
		
		
		//migrate funding sponsor
		syncFundingSponsor(dbStudy, xmlStudy, outcome);
        
        //migrate coordinating center
		syncCoordinatingCenter(dbStudy, xmlStudy, outcome);
		
        //migrate studyOrganization.
		syncStudySite(dbStudy, xmlStudy, outcome);
		
	}
	
	/**
	 * This method will synchronize the study site
	 * @param dbStudy
	 * @param xmlStudy
	 * @param outcome
	 */
	private void syncStudySite(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {
		
		//do nothing if study sites section is empty in xmlStudy
		if(CollectionUtils.isEmpty(xmlStudy.getStudySites())){
			return;
		}
		
		//create an index consisting of sites, in dbStudy
		HashMap<String, StudySite> siteIndexMap = new HashMap<String, StudySite>();
		for(StudySite ss : dbStudy.getStudySites()){
			siteIndexMap.put(generateIndexKey(ss), ss);
		}
		
		//loop through xmlStudy sites, and sync the personnel and investigators
		for(StudySite xmlStudySite : xmlStudy.getStudySites()){
			StudySite ss = siteIndexMap.remove(generateIndexKey(xmlStudySite));
			if(ss == null){
				//new so add it to dbStudy
				dbStudy.addStudySite(xmlStudySite);
				continue;
			}
			
			//sync the staff & investigators
			syncStudyInvestigators(ss, xmlStudySite, ss.getOrganization(), outcome);
			syncStudyPersonnels(ss, xmlStudySite, ss.getOrganization(), outcome);
			
		}
		
		//de-activate, all the other sites
		for(StudySite ss : siteIndexMap.values()){
			ss.deactivate();
		}
		
		
	}
	
	private void syncFundingSponsor(Study dbStudy, Study xmlStudy, DomainObjectImportOutcome<Study> outcome ){
		
		StudyFundingSponsor dbStudySponsor = dbStudy.getPrimaryFundingSponsor();
		StudyFundingSponsor xmlStudySponsor = xmlStudy.getPrimaryFundingSponsor();
		if(dbStudySponsor!=null && xmlStudySponsor!=null){
			//update funding sponsor if changed
			if(!dbStudySponsor.getOrganization().equals(xmlStudySponsor.getOrganization())){
				//clear study personnel
				dbStudySponsor.getStudyPersonnels().clear();
				//clear study investigator
				dbStudySponsor.getStudyInvestigators().clear();
				//update funding sponsor
				dbStudy.setPrimaryFundingSponsorOrganization(xmlStudy.getPrimaryFundingSponsorOrganization());
			}
			//Synchronize investigators and personnel
			syncStudyInvestigators(dbStudySponsor, xmlStudySponsor, dbStudySponsor.getOrganization(), outcome);
			syncStudyPersonnels(dbStudySponsor, xmlStudySponsor, dbStudySponsor.getOrganization(), outcome);
		}
	}
	
	private void syncCoordinatingCenter(Study dbStudy, Study xmlStudy, DomainObjectImportOutcome<Study> outcome ){
		
		StudyCoordinatingCenter dbStudyCoordinatingCenter = dbStudy.getStudyCoordinatingCenter();
		StudyCoordinatingCenter xmlStudyCoordinatingCenter = xmlStudy.getStudyCoordinatingCenter();
		
		if(dbStudyCoordinatingCenter != null && xmlStudyCoordinatingCenter != null){
			//update coordinating center if changed
			if(!dbStudyCoordinatingCenter.getOrganization().equals(xmlStudyCoordinatingCenter.getOrganization())){
				//clear study personnel
				dbStudyCoordinatingCenter.getStudyPersonnels().clear();
				//clear study investigator
				dbStudyCoordinatingCenter.getStudyInvestigators().clear();
				//update coordinating center
				dbStudyCoordinatingCenter.setOrganization(xmlStudyCoordinatingCenter.getOrganization());
			}
			//Synchronize investigators and personnel
			syncStudyInvestigators(dbStudyCoordinatingCenter, xmlStudyCoordinatingCenter, dbStudyCoordinatingCenter.getOrganization(), outcome);
			syncStudyPersonnels(dbStudyCoordinatingCenter, xmlStudyCoordinatingCenter, dbStudyCoordinatingCenter.getOrganization(), outcome);
		}
	}
	
	private void syncStudyInvestigators(StudyOrganization dbStudyOrganization,
										StudyOrganization xmlStudyOrganization,
										Organization organization, 
										DomainObjectImportOutcome<Study> studyImportOutcome) {
		
		//do nothing if there is no investigator in the xmlStudy Organization
		if(CollectionUtils.isEmpty(xmlStudyOrganization.getStudyInvestigators())){
			return;
		}
		
		//generate and index of existing study investigators
		HashMap<String, StudyInvestigator> dbStudyInvIndexMap = new HashMap<String, StudyInvestigator>();
		for(StudyInvestigator si : dbStudyOrganization.getStudyInvestigators()){
			dbStudyInvIndexMap.put(generateIndexKey(si), si);
		}
		
		//loop through xmlStudy Organization StudyInvestigators, then add and modify details
		for(StudyInvestigator xmlSi : xmlStudyOrganization.getStudyInvestigators()){
			StudyInvestigator si = dbStudyInvIndexMap.remove(generateIndexKey(xmlSi));
			if(si == null){
				//new one so add it to Study
				dbStudyOrganization.addStudyInvestigators(xmlSi);
				continue;
			}
			//update existing investigator
			si.setEndDate(xmlSi.getEndDate());
			si.setStartDate(xmlSi.getStartDate());
			si.setRoleCode(xmlSi.getRoleCode());
		}
		
		//deactivate the study investigators which are not present in xmlStudy Organization
		for(StudyInvestigator si : dbStudyInvIndexMap.values()){
			si.deactivate();
		}
		
		
	}//end method
	
	private void syncStudyPersonnels(StudyOrganization dbStudyOrganization,
										StudyOrganization xmlStudyOrganization,
										Organization organization, 
										DomainObjectImportOutcome<Study> studyImportOutcome) {
		
		

		//do nothing if there is no personnel in the xmlStudy Organization
		if(CollectionUtils.isEmpty(xmlStudyOrganization.getStudyPersonnels())){
			return;
		}
		
		//generate and index of existing study StudyPersonnel
		HashMap<String, StudyPersonnel> dbStudyPersonnelIndexMap = new HashMap<String, StudyPersonnel>();
		for(StudyPersonnel sp : dbStudyOrganization.getStudyPersonnels()){
			dbStudyPersonnelIndexMap.put(generateIndexKey(sp), sp);
		}
		
		//loop through xmlStudy Organization StudyPersonnel, then add and modify details
		for(StudyPersonnel xmlSp : xmlStudyOrganization.getStudyPersonnels()){
			StudyPersonnel sp = dbStudyPersonnelIndexMap.remove(generateIndexKey(xmlSp));
			if(sp == null){
				//new one so add it to Study
				dbStudyOrganization.addStudyPersonnel(xmlSp);
				continue;
			}
			//update existing study personnel
			sp.setEndDate(xmlSp.getEndDate());
			sp.setStartDate(xmlSp.getStartDate());
			sp.setRoleCode(xmlSp.getRoleCode());
		}
		
		//deactivate the study staff which are not present in xmlStudy Organization
		for(StudyPersonnel sp : dbStudyPersonnelIndexMap.values()){
			sp.deactivate();
		}
		
	}//end method
	
	//generate a string key based on the values of site
	private String generateIndexKey(StudySite so){
		Organization o = so.getOrganization();
		String nciCode = o.getNciInstituteCode();
		String name = o.getName();
		assert nciCode != null || name != null : " Organization Name and NCICode, atleast one should be present";
		return ((nciCode == null) ? "" : nciCode + "%" ) + ( (name == null) ? "" : name); 
	}
	
	//generate a string key based on the values of study investigator
	private String generateIndexKey(StudyInvestigator si){
		Investigator inv = si.getSiteInvestigator().getInvestigator();
		String nciCode = inv.getNciIdentifier();
		String firstName = inv.getFirstName();
		String lastName = inv.getLastName();
		String roleCode = si.getRoleCode();
		
		assert (nciCode != null || firstName != null || lastName != null || roleCode != null) : "Investigator firstname, lastname , nciCode or roleCode should be present";
		StringBuffer sb = new StringBuffer();
		sb.append(nciCode != null ? nciCode : "").append("%")
		.append(firstName != null ? firstName : "").append("%")
		.append(lastName != null ? lastName : "").append("%")
		.append(roleCode != null ? roleCode : "");
		return sb.toString();
		
	}
	
	//generate a string key based on the values of study personnel
	private String generateIndexKey(StudyPersonnel sp){
		ResearchStaff staff = sp.getSiteResearchStaff().getResearchStaff();
		String nciCode = staff.getNciIdentifier();
		String firstName = staff.getFirstName();
		String lastName = staff.getLastName();
		String roleCode = sp.getRoleCode();
		
		assert (nciCode != null || firstName != null || lastName != null || roleCode != null) : "ResearchStaff firstname, lastname , nciCode or roleCode should be present";
		StringBuffer sb = new StringBuffer();
		sb.append(nciCode != null ? nciCode : "").append("%")
		.append(firstName != null ? firstName : "").append("%")
		.append(lastName != null ? lastName : "").append("%")
		.append(roleCode != null ? roleCode : "");
		return sb.toString();
		
	}
}
