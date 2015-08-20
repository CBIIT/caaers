/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.ctms.domain.DomainObject;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @author Monish Domla
 * @author Biju Joseph (refactored)
 *
 */
public class StudyDiseasesSynchronizer  implements Synchronizer<gov.nih.nci.cabig.caaers.domain.Study>{

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	public void migrate(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {
		
		//ignore if disease section is empty in xmlstudy
		if(CollectionUtils.isEmpty(xmlStudy.getActiveStudyDiseases()) ){
			return;
		}
		
		//create an Index of existing study diseases
		HashMap<AbstractStudyDisease<? extends DomainObject>, AbstractStudyDisease<? extends DomainObject>> dbDiseasesIndexMap = 
			new HashMap<AbstractStudyDisease<? extends DomainObject>, AbstractStudyDisease<? extends DomainObject>>();
		
		for(AbstractStudyDisease<? extends DomainObject> studyDisease : dbStudy.getActiveStudyDiseases()){
			dbDiseasesIndexMap.put(studyDisease, studyDisease);
		}
		
		//loop through the xml study, then add/update existing diseases
		for(AbstractStudyDisease<? extends DomainObject> xmlDisease : xmlStudy.getActiveStudyDiseases()){
			AbstractStudyDisease<? extends DomainObject> disease = dbDiseasesIndexMap.remove(xmlDisease);
			if(disease == null){
				//new disease, so add to dbstudy
				if(xmlDisease instanceof CtepStudyDisease) dbStudy.addCtepStudyDisease((CtepStudyDisease)xmlDisease);
				if(xmlDisease instanceof MeddraStudyDisease) dbStudy.addMeddraStudyDisease((MeddraStudyDisease)xmlDisease);
				if(xmlDisease instanceof StudyCondition) dbStudy.addStudyCondition((StudyCondition)xmlDisease);
				continue;
			}
			
			//update the primary indicator (if CTEP Disease)
			if(disease instanceof CtepStudyDisease){
				((CtepStudyDisease)disease).setLeadDisease( ((CtepStudyDisease)xmlDisease).getLeadDisease() );
			}
		}
		
		//mark retired the diseases still in index
		AbstractMutableRetireableDomainObject.retire(dbDiseasesIndexMap.values());
		
	}
	
}
