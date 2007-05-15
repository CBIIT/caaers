package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.CaaersTestCase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Rhett Sutphin
 */
public class CtcTermTest extends CaaersTestCase {
    private CtcTerm term = new CtcTerm();

    public void testFullNameWithoutSelect() throws Exception {
        term.setTerm("This is the long term name");
        assertEquals("This is the long term name", term.getFullName());
    }

    public void testFullNameWithSelect() throws Exception {
        term.setTerm("This is the long term name");
        term.setSelect("exactly");
        assertEquals("This is the long term name - exactly", term.getFullName());
    }

    public void testGetGradesUsesContextualIfPresent() throws Exception {
        CtcGrade grade = new CtcGrade();
        grade.setGrade(Grade.MILD);
        grade.setText("Complains of hovering");
        term.setContextualGrades(new ArrayList<CtcGrade>());
        term.getContextualGrades().add(grade);

        assertSame(term.getContextualGrades(), term.getGrades());
    }

    public void testGetGradesUsesDefaultsIfZeroContextualGrades() throws Exception {
        term.setContextualGrades(new ArrayList<CtcGrade>());
        assertEquals(Arrays.asList(Grade.values()), term.getGrades());
    }

    public void testGetGradesUsesDefaultsIfNullContextualGrades() throws Exception {
        term.setContextualGrades(null);
        assertEquals(Arrays.asList(Grade.values()), term.getGrades());
    }
}
