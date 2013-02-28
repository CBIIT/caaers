/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import static org.easymock.EasyMock.expect;
import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;

/**
 * @author Rhett Sutphin
 */
public class ExpeditedFlowFactoryTest extends AbstractTestCase {
    private ExpeditedFlowFactory factory = new ExpeditedFlowFactory("Test flow");

    private ExpeditedAdverseEventInputCommand command;

    private Study study;
    private Term term;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        study = Fixtures.createStudy("Kilo");
        study.getAeTerminology().setTerm(Term.CTC);
        term = Term.CTC;

        command = registerMockFor(ExpeditedAdverseEventInputCommand.class);
        
    }

    public void testTabCount() throws Exception {
    	expect(command.getStudyTerminologyTerm()).andReturn(term).anyTimes();
    	replayMocks();
        assertEquals(11, factory.createFlow(command).getTabCount());
    }

    public void testMeddraBasicsTabUsedWhenAppropriate() throws Exception {
    	term = Term.MEDDRA;
    	expect(command.getStudyTerminologyTerm()).andReturn(term).anyTimes();
    	replayMocks();
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(1) instanceof MeddraBasicsTab);
    }

    public void testCtcTabUsedWhenExplicitlyRequired() throws Exception {
    	expect(command.getStudyTerminologyTerm()).andReturn(term).anyTimes();
    	replayMocks();
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(1) instanceof CtcBasicsTab);
    }

    public void testCtcTabUsedByDefault() throws Exception {
        resetMocks();
        expect(command.getStudyTerminologyTerm()).andReturn(term).anyTimes();
        replayMocks();

        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(1) instanceof CtcBasicsTab);
    }
}
