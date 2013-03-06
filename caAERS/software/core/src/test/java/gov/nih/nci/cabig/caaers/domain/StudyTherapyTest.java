/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import edu.nwu.bioinformatics.commons.testing.CoreTestCase;

/**
 * @author Biju Joseph
 */
public class StudyTherapyTest extends CoreTestCase {

    private StudyTherapy studyTherapy1, studyTherapy2;
    private Study study;

    @Override
    protected void setUp() throws Exception {
        studyTherapy1 = new StudyTherapy();
        studyTherapy2 = new StudyTherapy();
        study = new LocalStudy();
    }

    public void testStudyTherapy() {
        assertEquals(studyTherapy1, studyTherapy2);

        studyTherapy1.setStudyTherapyType(StudyTherapyType.BEHAVIORAL);
        assertNotEquals(studyTherapy1, studyTherapy2);
        studyTherapy2.setStudyTherapyType(StudyTherapyType.BEHAVIORAL);
        assertEquals(studyTherapy1, studyTherapy2);


        studyTherapy1.setStudy(study);
        assertNotEquals(studyTherapy1, studyTherapy2);
        studyTherapy2.setStudy(study);
        assertEquals(studyTherapy1, studyTherapy2);

        study.setId(1);
        studyTherapy1.setStudy(study);
        studyTherapy2.setStudy(new LocalStudy());
        assertNotEquals(studyTherapy1, studyTherapy2);
        studyTherapy2.setStudy(study);
        assertEquals(studyTherapy1, studyTherapy2);


    }
}
