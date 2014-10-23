/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.migrator;

import gov.nih.nci.cabig.caaers.dao.LabTermDao;
import gov.nih.nci.cabig.caaers.dao.LabVersionDao;
import gov.nih.nci.cabig.caaers.domain.LabCategory;
import gov.nih.nci.cabig.caaers.domain.LabTerm;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Message;

import java.util.List;

/**
 * @author Ramakrishna Gundala
 */

public class LabMigrator implements Migrator<LabCategory>{
	
	private LabVersionDao labVersionDao;
	private LabTermDao labTermDao;

	public void setLabTermDao(LabTermDao labTermDao) {
		this.labTermDao = labTermDao;
	}

	public void setLabVersionDao(LabVersionDao labVersionDao) {
		this.labVersionDao = labVersionDao;
	}

	public void migrate(LabCategory src, LabCategory dest, DomainObjectImportOutcome<LabCategory> outcome) {
		dest.setName(src.getName());
		dest.setRetiredIndicator(src.getRetiredIndicator());
		// if lab category exists in the db, loop through lab terms and check if each lab term exists. If it exists, update it else create new one
		if(dest.getId() != null) {

			// case for existing lab category
			for(LabTerm labTerm:src.getTerms()){
                List<LabTerm> matchedTerms = dest.findAllMatchingTerms(labTerm.getTerm());
                //retire existing
                for(LabTerm t : matchedTerms) t.retire();
                if(matchedTerms.isEmpty()) matchedTerms.add(new LabTerm());

                LabTerm dbTerm  = matchedTerms.get(0);
				// migrate lab term, set retired indicator etc
				migrate(labTerm,dbTerm,null);
				outcome.getMessages().add(new Message("Created/updated lab term :" + labTerm.getTerm(),null));
				// if term doesn't exist add it
				if(dbTerm.getId() == null){
					dest.addTerm(dbTerm);
				}
			}
		} else {
			// case for new lab category, set lab version to Version 1
			dest.setLabVersion(labVersionDao.getByName("Version 1"));
			// create a new lab term for each migrated one.
			for(LabTerm labTerm:src.getTerms()){
				LabTerm newLabTerm = new LabTerm();
				migrate(labTerm,newLabTerm,null);
				dest.addTerm(newLabTerm);
				outcome.getMessages().add(new Message("Created/updated lab term :" + labTerm.getTerm(),null));
			}
		}
	}
	
	public void migrate(LabTerm src, LabTerm dest, DomainObjectImportOutcome<LabCategory> outcome) {
		dest.setTerm(src.getTerm());
		dest.setRetiredIndicator(src.getRetiredIndicator());
	}
}
