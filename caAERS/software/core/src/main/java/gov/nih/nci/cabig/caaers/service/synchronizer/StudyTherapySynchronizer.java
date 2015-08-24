/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyTherapy;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class StudyTherapySynchronizer implements Synchronizer<gov.nih.nci.cabig.caaers.domain.Study>{

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	/**
	 *	If none of the below given elements are provided in the XML, then StudyTherapies list will be null.
		In this case do not do anything.
		If one or more of the below given elements are provided in the XML, 
		In this case the existing StudyTherapies on the study will be replaced with the ones provided.
		
        <xs:element name="drugAdministrationTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="deviceTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="radiationTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="surgeryTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="behavioralTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="biologicalTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="geneticTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="dietarySupplementTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
        <xs:element name="otherTherapyType" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
	 */
	public void migrate(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {

		if(CollectionUtils.isEmpty(xmlStudy.getStudyTherapies())){
			return;
		}

		dbStudy.getStudyTherapies().clear();
		for(StudyTherapy therapy : xmlStudy.getStudyTherapies()){
			dbStudy.addStudyTherapy(therapy.getStudyTherapyType());
		}
	}
}
