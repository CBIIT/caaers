/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.dto;

import junit.framework.TestCase;

/**
 * @author: Biju Joseph
 */
public class EvaluationResultDTOTest extends TestCase {
    public void testAddProcessingStep() throws Exception {
       EvaluationResultDTO dto = new EvaluationResultDTO();
       
        dto.addProcessingStep(1, "x", "y");
        assertEquals("x y", dto.getProcessingSteps().get(1).get(0));
        assertNull(dto.getProcessingSteps().get(0));
    }
}
