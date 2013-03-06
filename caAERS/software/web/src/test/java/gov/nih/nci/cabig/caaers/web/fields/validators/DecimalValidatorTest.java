/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.fields.validators;

import java.math.BigDecimal;

import junit.framework.TestCase;

/**
 * 
 * @author Biju Joseph
 *
 */
public class DecimalValidatorTest extends TestCase {
	DecimalValidator dv;

	protected void setUp() throws Exception {
		super.setUp();
		dv = new DecimalValidator(6, 2);

	}

	public void testIsValid() {
		assertTrue(dv.isValid("-453422.93"));
		assertTrue(dv.isValid("-99.99"));
		assertTrue(dv.isValid("99"));
		assertTrue(dv.isValid(new BigDecimal(99.)));
		assertTrue(dv.isValid("-123456.12"));
		assertTrue(dv.isValid("123456"));

        assertFalse(dv.isValid("-9x9.99"));
        assertFalse(dv.isValid("99.992"));
        assertFalse(dv.isValid("1234567.92"));
        assertFalse(dv.isValid("123456.123"));



        dv = new DecimalValidator(14, 6);
        assertTrue(dv.isValid("999999123.012345"));
        assertTrue(dv.isValid("12345678901.012345"));
        assertFalse(dv.isValid("123456789012345.012345"));
        assertTrue(dv.isValid("000"));
        assertTrue(dv.isValid("000.000"));
        assertTrue(dv.isValid("8.000"));
		assertTrue(dv.isValid("999999123.012345"));

        assertTrue(dv.isValid("-3.00"));
        assertTrue(dv.isValid("8."));
		assertFalse(dv.isValid("8..."));
        assertFalse(dv.isValid("bba12"));
		assertFalse(dv.isValid(".14"));
		assertFalse(dv.isValid("3.1234567"));


        dv = new DecimalValidator(5, 2);
		assertTrue(dv.isValid("99923.01"));
		assertTrue(dv.isValid("0.0"));
		assertTrue(dv.isValid("0."));
		assertTrue(dv.isValid("0.4"));

		assertFalse(dv.isValid(".14"));
		assertFalse(dv.isValid("3.123"));
		assertFalse(dv.isValid("123456.91"));
        assertFalse(dv.isValid("bba12"));
	}

	public void testGetMessagePrefix() {
		assertEquals("Invalid", dv.getMessagePrefix());
	}

	public void testGetValidatorCSSClassName() {
		assertEquals("DECIMAL", dv.getValidatorCSSClassName());
	}

    public void testOK() {
        dv = new DecimalValidator(14, 6);
        dv.isValid("99.99");
    }
}
