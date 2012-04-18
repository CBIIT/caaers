package gov.nih.nci.cabig.caaers.api;

import gov.nih.nci.cabig.caaers.domain.EntityErrorMessage;
import gov.nih.nci.cabig.caaers.domain.PriorTherapy;

import java.util.List;

/**
 * Organization Creation and Updation.
 * @author Ramakrishna
 *
 */
public interface PriorTherapyLOVService {
	
	public List<EntityErrorMessage> importPriorTherapies(List<PriorTherapy> importedPriorTherapies);

}
