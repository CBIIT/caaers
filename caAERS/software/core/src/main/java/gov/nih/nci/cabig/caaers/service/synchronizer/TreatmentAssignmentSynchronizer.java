/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.AbstractMutableRetireableDomainObject;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * This Class synchronizes all TreatmentAssignments 
 * @author Monish Dombla
 * @author Biju Joseph (refactored)
 * @author Ion C. Olaru
 *
 */
public class TreatmentAssignmentSynchronizer implements Synchronizer<gov.nih.nci.cabig.caaers.domain.Study>  {

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	public void migrate(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {
        //create an Index of existing ones (available in DB)
        Hashtable<String, TreatmentAssignment> dbTacIndexMap = new Hashtable<String, TreatmentAssignment>();
        Hashtable<String, TreatmentAssignment> dbCtepIndexMap = new Hashtable<String, TreatmentAssignment>();
        for (TreatmentAssignment ta : dbStudy.getActiveTreatmentAssignments()) {
            String ctepDbId = StringUtils.upperCase(ta.getCtepDbIdentifier());
            String tac = StringUtils.upperCase(ta.getCode());
            dbTacIndexMap.put(tac, ta);
            if(StringUtils.isNotEmpty(ctepDbId)) dbCtepIndexMap.put(ctepDbId, ta);
        }

        //Identify New TreatmentAssignments and also update existing ones.
        for (TreatmentAssignment xmlTreatmentAssignment : xmlStudy.getTreatmentAssignments()) {

            // //CAAERS-7367 - /REFACTORED - always prefer the tac that is available.
            String ctepDbId = StringUtils.upperCase(xmlTreatmentAssignment.getCtepDbIdentifier());
            String tac = StringUtils.upperCase(xmlTreatmentAssignment.getCode());
            if(StringUtils.isEmpty(tac) && StringUtils.isEmpty(ctepDbId)) continue; //no I cannot process this record
            TreatmentAssignment ta = null;

            //try to identify the TA by ctep-id
            if(StringUtils.isNotEmpty(ctepDbId)) {
                ta = dbCtepIndexMap.get(ctepDbId) ;
            }
            //TA not found : try to find by tac
            if(ta ==  null) ta = dbTacIndexMap.get(tac);

            //still tac null -- create a new one.
            if(ta == null) {
                ta = xmlTreatmentAssignment;
                dbStudy.addTreatmentAssignment(xmlTreatmentAssignment);
                continue;
            }

            //it is an existing TA, so lets sync up the attributes
            ta.setCtepDbIdentifier(xmlTreatmentAssignment.getCtepDbIdentifier());
            ta.setCode(xmlTreatmentAssignment.getCode());
            ta.setDescription(xmlTreatmentAssignment.getDescription());
            ta.setComments(xmlTreatmentAssignment.getComments());
            ta.setDoseLevelOrder(xmlTreatmentAssignment.getDoseLevelOrder());

            //marking the TA as processed by removing it from index
            dbTacIndexMap.remove(tac);

        }

        //soft delete - all the TAs that were not present in XML Study
		AbstractMutableRetireableDomainObject.retire(dbTacIndexMap.values());

	}

}
