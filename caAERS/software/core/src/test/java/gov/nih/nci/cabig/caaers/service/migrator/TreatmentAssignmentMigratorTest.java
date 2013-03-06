/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.migrator;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;

public class TreatmentAssignmentMigratorTest extends AbstractTestCase {

	Study xstreamStudy;
	Study dest;
	DomainObjectImportOutcome<Study> outcome;
	TreatmentAssignmentMigrator migrator;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xstreamStudy = Fixtures.createStudy("test");
		dest = new LocalStudy();
		outcome = new DomainObjectImportOutcome<Study>();
		migrator = new TreatmentAssignmentMigrator();
	}
	
	public void testMigrate() {
		TreatmentAssignment treatmentAssignment = Fixtures.createTreatmentAssignment();
        xstreamStudy.addTreatmentAssignment(treatmentAssignment);
        migrator.migrate(xstreamStudy,dest, outcome);
        
        assertTrue(outcome.getMessages().isEmpty());
        assertTrue(dest.getTreatmentAssignments().size() == 1);
	}
	
}
