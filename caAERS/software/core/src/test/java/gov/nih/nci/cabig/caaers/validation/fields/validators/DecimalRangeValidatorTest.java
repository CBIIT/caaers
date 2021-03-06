/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.validation.fields.validators;

import gov.nih.nci.cabig.caaers.validation.fields.validators.DecimalRangeValidator;
import junit.framework.TestCase;
/**
 * 
 * @author Biju Joseph
 *
 */
public class DecimalRangeValidatorTest extends TestCase {

	DecimalRangeValidator dv;
	protected void setUp() throws Exception {
		super.setUp();
		dv = new DecimalRangeValidator(1.0,100.5);
	}

	public void testIsValid() {
		assertTrue(dv.isValid("99.99"));
		assertFalse(dv.isValid("-99.99"));
		assertTrue(dv.isValid("99.99"));
		assertFalse(dv.isValid("-9x9.99"));
		assertTrue(dv.isValid(null));
	}

	public void testGetMessagePrefix() {
		assertEquals("Incorrect", dv.getMessagePrefix());
	}

	public void testGetValidatorCSSClassName() {
		assertEquals("DECIMAL", dv.getValidatorCSSClassName());
	}

}
