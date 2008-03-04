package gov.nih.nci.cabig.caaers.web.ae;

import static org.easymock.EasyMock.expect;
import gov.nih.nci.cabig.caaers.CaaersTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;

/**
 * @author Rhett Sutphin
 */
public class ExpeditedFlowFactoryTest extends CaaersTestCase {
    private ExpeditedFlowFactory factory = new ExpeditedFlowFactory("Test flow");

    private ExpeditedAdverseEventInputCommand command;

    private Study study;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        study = Fixtures.createStudy("Kilo");
        study.getAeTerminology().setTerm(Term.CTC);
        study.setAdeersReporting(Boolean.TRUE);

        command = registerMockFor(ExpeditedAdverseEventInputCommand.class);
        expect(command.getStudy()).andReturn(study).anyTimes();
        replayMocks();
    }

    public void testTabCount() throws Exception {
        assertEquals(17, factory.createFlow(command).getTabCount());
    }

    public void testTabCountIfAdeersReportingNotRequiredCtc() throws Exception {
        study.setAdeersReporting(Boolean.FALSE);
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertEquals(17, flow.getTabCount());
        assertTrue("Wrong basics tab", flow.getTab(0) instanceof CtcBasicsOutcomeTab);
    }

    public void testTabCountIfAdeersReportingNotRequiredMeddra() throws Exception {
        study.getAeTerminology().setTerm(Term.MEDDRA);
        study.setAdeersReporting(Boolean.FALSE);
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertEquals(17, flow.getTabCount());
        assertTrue("Wrong basics tab", flow.getTab(0) instanceof MeddraBasicsOutcomeTab);
    }

    public void testMeddraBasicsTabUsedWhenAppropriate() throws Exception {
        study.getAeTerminology().setTerm(Term.MEDDRA);
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(0) instanceof MeddraBasicsTab);
    }

    public void testCtcTabUsedWhenExplicitlyRequired() throws Exception {
        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(0) instanceof CtcBasicsTab);
    }

    public void testCtcTabUsedByDefault() throws Exception {
        resetMocks();
        expect(command.getStudy()).andReturn(null).anyTimes();
        replayMocks();

        Flow<ExpeditedAdverseEventInputCommand> flow = factory.createFlow(command);
        assertTrue("Wrong basics tab", flow.getTab(0) instanceof CtcBasicsTab);
    }
}
