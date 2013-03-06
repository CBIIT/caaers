/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository.ajax;

import gov.nih.nci.cabig.caaers.dao.query.ajax.ParticipantAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.ajax.ParticipantAjaxableDomainObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;


 
/**
 * The Class ParticipantAjaxableDomainObjectRepository.
 *
 * @param <T> the generic type
 * @author Biju Joseph
 */

@Transactional(readOnly = true)
public class ParticipantAjaxableDomainObjectRepository<T extends ParticipantAjaxableDomainObject> extends AbstractAjaxableDomainObjectRepository {
    
	/**
	 * Gets the data for filtering.
	 *
	 * @param query the query
	 * @return the data for filtering
	 */
	public List<Object[]> getDataForFiltering(ParticipantAjaxableDomainObjectQuery query) {
    	return super.find(query);
    }
    
	/**
	 * Find participants.
	 *
	 * @param query the query
	 * @return the list
	 */
	public List<T> findParticipants(ParticipantAjaxableDomainObjectQuery query) {
		
		//System.out.println(query.getQueryString());
		
        List<Object[]> objects = super.find(query);
        Map<Integer,T> existingParticipantsMap = new LinkedHashMap<Integer, T>();
        for (Object[] o : objects) {
            T participantAjaxableDomainObject = existingParticipantsMap.get((Integer) o[0]);

            if (participantAjaxableDomainObject == null) {
                participantAjaxableDomainObject = (T) BeanUtils.instantiateClass(getObjectClass());
                participantAjaxableDomainObject.setId((Integer) o[0]);
                participantAjaxableDomainObject.setFirstName((String) o[1]);
                participantAjaxableDomainObject.setLastName((String) o[2]);
                participantAjaxableDomainObject.setGender((String) o[3]);
                participantAjaxableDomainObject.setRace((String) o[4]);
                participantAjaxableDomainObject.setEthnicity((String) o[5]);
                if (o[7] != null && (Boolean) o[7]) {
                    participantAjaxableDomainObject.setPrimaryIdentifierValue((String) o[6]);
                }

                participantAjaxableDomainObject.collectStudySubjectIdentifier((String) o[8]);
               // addAdditionalProperties(participantAjaxableDomainObject, o);
                existingParticipantsMap.put(participantAjaxableDomainObject.getId(),participantAjaxableDomainObject);
                

            } else  {
            	updateAdditionalProperties(participantAjaxableDomainObject, o);
            }
        }

        return new ArrayList<T>(existingParticipantsMap.values());

    }
    
    /**
     * Update additional properties.
     *
     * @param participantAjaxableDomainObject the participant ajaxable domain object
     * @param o the o
     */
    protected void updateAdditionalProperties(T participantAjaxableDomainObject, Object[] o) {
    	if (o[7] != null && (Boolean) o[7]) {
            participantAjaxableDomainObject.setPrimaryIdentifierValue((String) o[6]);
        }
        participantAjaxableDomainObject.collectStudySubjectIdentifier((String) o[8]);
    	//updateParticipantStudySite(participantAjaxableDomainObject, o);
    	//updateStudy(participantAjaxableDomainObject, o);


    }
/*
    protected void addAdditionalProperties(T participantAjaxableDomainObject, Object[] o) {
    	//updateParticipantStudySite(participantAjaxableDomainObject, o);
    	updateStudy(participantAjaxableDomainObject, o);
    }
    
    private void updateAssignedStudySite(StudySearchableAjaxableDomainObject studySearchableAjaxableDomainObject, Object[] o) {
        if (!StringUtils.isBlank((String) o[18])) {
            StudySiteAjaxableDomainObject studySiteAjaxableDomainObject = new StudySiteAjaxableDomainObject();
            studySiteAjaxableDomainObject.setId((Integer) o[17]);
            studySiteAjaxableDomainObject.setName((String) o[18]);
            studySiteAjaxableDomainObject.setNciInstituteCode((String) o[19]);
            studySiteAjaxableDomainObject.setStudyId((Integer) o[9]);
            studySearchableAjaxableDomainObject.addAssignedStudySite(studySiteAjaxableDomainObject);
        }
    }
    
    private void updateStudySite(StudySearchableAjaxableDomainObject studySearchableAjaxableDomainObject, Object[] o) {
        
    	//CAAERS-2441
        //if (!StringUtils.isBlank((String) o[12]) && (StringUtils.equals((String) o[14], "SST") || StringUtils.equals((String) o[14], "SCC"))) {
    	if (!StringUtils.isBlank((String) o[12]) && (StringUtils.equals((String) o[14], "SST") || StringUtils.equals((String) o[14], "SCC") || StringUtils.equals((String) o[14], "SFS"))) {
    		StudySiteAjaxableDomainObject studySiteAjaxableDomainObject = new StudySiteAjaxableDomainObject();
            studySiteAjaxableDomainObject.setId((Integer) o[13]);
            studySiteAjaxableDomainObject.setName((String) o[12]);
            studySiteAjaxableDomainObject.setNciInstituteCode((String) o[15]);
            studySiteAjaxableDomainObject.setStudyId((Integer) o[9]);
            studySiteAjaxableDomainObject.setType((String) o[14]);
            studySearchableAjaxableDomainObject.addStudySite(studySiteAjaxableDomainObject);
        }
    }
    
    private void updateStudy(T participantAjaxableDomainObject, Object[] o) {
        if (!StringUtils.isBlank((String) o[8])) {
        	//List <StudySearchableAjaxableDomainObject> studies = participantAjaxableDomainObject.getStudies();
        	StudySearchableAjaxableDomainObject studySearchableAjaxableDomainObject = (StudySearchableAjaxableDomainObject) getObjectById(participantAjaxableDomainObject.getStudies(), (Integer) o[9]);
        	if (studySearchableAjaxableDomainObject == null) {
        		StudySearchableAjaxableDomainObject study = new StudySearchableAjaxableDomainObject();
            	study.setId((Integer) o[9]);
            	study.setShortTitle((String) o[8]);
                if (o[11] != null && (Boolean) o[11]) {
                	study.setPrimaryIdentifierValue((String) o[10]);
                }
                if (o[16] != null) {
                	study.addStudyPersonnelId((Integer) o[16]);
                }
                updateStudySite(study, o);
                updateAssignedStudySite(study, o);
                participantAjaxableDomainObject.addStudy(study);
        	} else {
        		if (o[11] != null && (Boolean) o[11]) {
        			studySearchableAjaxableDomainObject.setPrimaryIdentifierValue((String) o[10]);
                }
                if (o[16] != null) {
                	studySearchableAjaxableDomainObject.addStudyPersonnelId((Integer) o[16]);
                }
        		updateStudySite(studySearchableAjaxableDomainObject,o);
        		updateAssignedStudySite(studySearchableAjaxableDomainObject, o);
        		participantAjaxableDomainObject.addStudy(studySearchableAjaxableDomainObject);
        	}
            
        }
    }  
    */
    /**
 * Gets the object class.
 *
 * @return the object class
 */
protected Class getObjectClass() {
        return ParticipantAjaxableDomainObject.class;
    }
    
   
}
