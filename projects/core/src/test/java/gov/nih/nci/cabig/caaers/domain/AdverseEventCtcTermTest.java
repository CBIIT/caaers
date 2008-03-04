package gov.nih.nci.cabig.caaers.domain;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class AdverseEventCtcTermTest extends TestCase {
    private AdverseEventCtcTerm term;

    private AdverseEvent adverseEvent;

    private CtcTerm coagOther;

    private CtcTerm cd4;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        adverseEvent = new AdverseEvent();
        term = new AdverseEventCtcTerm();
        term.setAdverseEvent(adverseEvent);

        coagOther = new CtcTerm();
        coagOther.setTerm("Coagulation - Other (Specify, __)");
        coagOther.setOtherRequired(true);

        cd4 = new CtcTerm();
        cd4.setTerm("CD4 count");
    }

    public void testUniversalTermForSpecificTerm() throws Exception {
        term.setCtcTerm(cd4);
        assertEquals(cd4.getTerm(), term.getUniversalTerm());
    }

    public void testUniversalTermForOtherReqTermWithOtherDetails() throws Exception {
        adverseEvent.setDetailsForOther("Arbit");
        term.setCtcTerm(coagOther);
        assertEquals("Coagulation - Other: Arbit", term.getUniversalTerm());
    }

    public void testUniversalTermForOtherReqTermWithoutOtherDetails() throws Exception {
        adverseEvent.setDetailsForOther(null);
        term.setCtcTerm(coagOther);
        assertEquals("Coagulation - Other (Specify, __)", term.getUniversalTerm());
    }

    public void testUniversalTermWithNoTerm() throws Exception {
        term.setCtcTerm(null);
        assertNull(term.getUniversalTerm());
    }
}
