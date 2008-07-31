package gov.nih.nci.cabig.caaers.service.migrator;

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.SiteInvestigatorDao;
import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.domain.CoordinatingCenter;
import gov.nih.nci.cabig.caaers.domain.FundingSponsor;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteInvestigator;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyCoordinatingCenter;
import gov.nih.nci.cabig.caaers.domain.StudyFundingSponsor;
import gov.nih.nci.cabig.caaers.domain.StudyInvestigator;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class StudyOrganizationMigrator implements Migrator<Study>{
	
	  private OrganizationDao organizationDao;
	  private SiteInvestigatorDao siteInvestigatorDao;
	  private ResearchStaffDao researchStaffDao;
	  
	/**
	 * This method will copy the {@link StudyOrganization}s from source to destination
	 */
	public void migrate(Study source, Study destination,DomainObjectImportOutcome<Study> outcome) {
		
		//migrate funding sponsor
		migrateFundingSponsor(source, destination, outcome);
        
        //migrate coordinating center
		migrateCoordinatingCenter(source, destination, outcome);
		
        //migrate studyOrganization.
		migrateStudySite(source, destination, outcome);
	}
	
	private void migrateStudySite(Study source, Study destination,DomainObjectImportOutcome<Study> outcome) {
		if(source.getStudyOrganizations() != null && source.getStudyOrganizations().size() > 0){
			for (StudyOrganization studyOrganization : source.getStudyOrganizations()) {
				if(studyOrganization instanceof StudySite){
					Organization organization = null;
					if(studyOrganization.getOrganization().getNciInstituteCode() != null && studyOrganization.getOrganization().getNciInstituteCode().length() > 0){
						String nciInstituteCode = studyOrganization.getOrganization().getNciInstituteCode();
				        organization = fetchOrganization(nciInstituteCode);
					}else{
						String orgName = studyOrganization.getOrganization().getName();
				        organization = organizationDao.getByName(orgName);
					}
			        outcome.ifNullObject(organization, DomainObjectImportOutcome.Severity.ERROR, 
					"The organization specified in studySite is invalid");
			        studyOrganization.setOrganization(organization);
	
			        // Migrate Study investigators and Study Personnels
			        if(organization != null){
			        	migrateStudyInvestigators(studyOrganization, organization, outcome);
				        migrateStudyPersonnels(studyOrganization, organization, outcome);
			        }
			        destination.addStudySite((StudySite) studyOrganization);
		       }
		   }
	
		}
		
	}
	
	private void migrateFundingSponsor(Study source, Study destination, DomainObjectImportOutcome<Study> outcome ){
		FundingSponsor sponsor = source.getFundingSponsor();
		if(sponsor == null) return;
		
		
		StudyFundingSponsor studySponsor = sponsor.getStudyFundingSponsor();
		Organization organization = null;
		if(studySponsor.getOrganization().getNciInstituteCode() != null && studySponsor.getOrganization().getNciInstituteCode().length() > 0){
			String nciInstituteCode = studySponsor.getOrganization().getNciInstituteCode();
	        organization = fetchOrganization(nciInstituteCode);
		}else{
			String orgName = studySponsor.getOrganization().getName();
			organization = organizationDao.getByName(orgName);
		}
		outcome.ifNullObject(organization, DomainObjectImportOutcome.Severity.ERROR, 
				"The organization specified in fundingSponsor is invalid");
		studySponsor.setOrganization(organization);
		
		OrganizationAssignedIdentifier orgIdentifier = sponsor.getOrganizationAssignedIdentifier();
		orgIdentifier.setType(OrganizationAssignedIdentifier.SPONSOR_IDENTIFIER_TYPE);
		orgIdentifier.setOrganization(organization);
		orgIdentifier.setPrimaryIndicator(Boolean.FALSE);
		
        //	Migrate Study investigators and Study Personnels
		if(organization != null){
			migrateStudyInvestigators(studySponsor, organization, outcome);
			migrateStudyPersonnels(studySponsor, organization, outcome);
		}
        
        destination.getIdentifiers().add(orgIdentifier);
        destination.addStudyFundingSponsor(studySponsor);
		
	}
	
	private void migrateCoordinatingCenter(Study source, Study destination, DomainObjectImportOutcome<Study> outcome ){
		CoordinatingCenter coCenter = source.getCoordinatingCenter();
		if(coCenter == null) return;
		
		StudyCoordinatingCenter studyCoordinatingCenter = coCenter.getStudyCoordinatingCenter();
		Organization organization = null;
		if(studyCoordinatingCenter.getOrganization().getNciInstituteCode() != null && studyCoordinatingCenter.getOrganization().getNciInstituteCode().length() > 0){
			String nciInstituteCode = studyCoordinatingCenter.getOrganization().getNciInstituteCode();
	        organization = fetchOrganization(nciInstituteCode);
		}else{
			String orgName = studyCoordinatingCenter.getOrganization().getName();
			organization = organizationDao.getByName(orgName);
		}
		outcome.ifNullObject(organization, DomainObjectImportOutcome.Severity.ERROR, "The organization specified in coordinatingCenter is invalid");
		studyCoordinatingCenter.setOrganization(organization);
		
		OrganizationAssignedIdentifier orgIdentifier = coCenter.getOrganizationAssignedIdentifier();
		orgIdentifier.setType(OrganizationAssignedIdentifier.COORDINATING_CENTER_IDENTIFIER_TYPE);
		orgIdentifier.setOrganization(organization);
		orgIdentifier.setPrimaryIndicator(Boolean.TRUE);
		
        //	Migrate Study investigators and Study Personnels
		if(organization != null){
			migrateStudyInvestigators(studyCoordinatingCenter, organization, outcome);
			migrateStudyPersonnels(studyCoordinatingCenter, organization, outcome);
		}
        
        destination.getIdentifiers().add(orgIdentifier);
        destination.addStudyOrganization(studyCoordinatingCenter);
		
	}	
 

    private void migrateStudyInvestigators(StudyOrganization studyOrganization, Organization organization, DomainObjectImportOutcome studyImportOutcome) {

        for (StudyInvestigator studyInvestigator : studyOrganization.getStudyInvestigators()) {

            Investigator investigator = studyInvestigator.getSiteInvestigator().getInvestigator();
            List<SiteInvestigator> siteInvestigators = null;
            if(investigator.getNciIdentifier() != null && investigator.getNciIdentifier().length() > 0){
            	String[] nciIdentifier = {investigator.getNciIdentifier()};
                siteInvestigators = siteInvestigatorDao.getByNciIdentifier(nciIdentifier, organization.getId());
            }else{
            	String[] investigatorFirstAndLast = {investigator.getFirstName(), investigator.getLastName()};
                siteInvestigators = siteInvestigatorDao.getBySubnames(investigatorFirstAndLast, organization.getId());
            }
            if (siteInvestigators.size() > 0) {
                studyInvestigator.setSiteInvestigator(siteInvestigators.get(0));
                studyInvestigator.setStudyOrganization(studyOrganization);
            } else {
                studyImportOutcome.ifNullObject(null, DomainObjectImportOutcome.Severity.ERROR, "The selected investigator " +
                        investigator.getFirstName() + " " + investigator.getLastName() + " is not Valid ");
            }
        }
    }


    private void migrateStudyPersonnels(StudyOrganization studyOrganization,
                                        Organization organization, DomainObjectImportOutcome<Study> studyImportOutcome) {

        for (StudyPersonnel studyPersonnel : studyOrganization.getStudyPersonnels()) {
            ResearchStaff researchStaffer = studyPersonnel.getResearchStaff();
            List<ResearchStaff> researchStaffs = null;
            
            if(researchStaffer.getNciIdentifier() != null && researchStaffer.getNciIdentifier().length() > 0){
            	String[] nciIdentifier = {researchStaffer.getNciIdentifier()};
            	researchStaffs = researchStaffDao.getByNciIdentifier(nciIdentifier, organization.getId());
            }else{
            	String[] investigatorFirstAndLast = {researchStaffer.getFirstName(), researchStaffer.getLastName()};
                researchStaffs = researchStaffDao.getBySubnames(investigatorFirstAndLast, organization.getId());
            }
            if (researchStaffs.size() > 0) {
                ResearchStaff researchStaff = researchStaffs.get(0);
                studyPersonnel.setResearchStaff(researchStaff);
                studyPersonnel.setStudyOrganization(studyOrganization);
            } else {
                studyImportOutcome.ifNullObject(null, DomainObjectImportOutcome.Severity.ERROR, "The selected personnel " +
                        researchStaffer.getFirstName() + " " + researchStaffer.getLastName() + " is not Valid ");
            }
        }
    } 
    
    /**
     * Fetches the organization from the DB
     * 
     * @param nciCode
     * @return
     */
    private Organization fetchOrganization(String nciInstituteCode) {
        OrganizationQuery orgQuery = new OrganizationQuery();

        if (StringUtils.isNotEmpty(nciInstituteCode)) {
            orgQuery.filterByNciCodeExactMatch(nciInstituteCode);
        }

        List<Organization> orgList = organizationDao.searchOrganization(orgQuery);

        if (orgList == null || orgList.isEmpty()) {
            return null;
        }
        if (orgList.size() > 1) {
            //("Multiple organizations exist with same NCI Institute Code :" + nciInstituteCode);
        }

        return orgList.get(0);
    }
    
    
    
    @Required
    public void setSiteInvestigatorDao(final SiteInvestigatorDao siteInvestigatorDao) {
        this.siteInvestigatorDao = siteInvestigatorDao;
    }
    
    @Required
    public void setResearchStaffDao(final ResearchStaffDao researchStaffDao) {
        this.researchStaffDao = researchStaffDao;
    }
    
    @Required
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }
}
