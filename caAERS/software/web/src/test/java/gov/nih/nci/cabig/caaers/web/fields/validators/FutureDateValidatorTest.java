/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.fields.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;


public class FutureDateValidatorTest extends TestCase {

	public void testIsValid() {
		Calendar gcNow = GregorianCalendar.getInstance();
		gcNow.add(Calendar.MINUTE,-5);
		Date now = gcNow.getTime(); //correct 5 mts
		boolean valid;
		FutureDateValidator futureDateValidator = new FutureDateValidator();
		valid = futureDateValidator.isValid(now);
		assertFalse(valid);
		
		Date futureDate = new GregorianCalendar(2019, 07, 14, 14, 00).getTime();
		valid = futureDateValidator.isValid(futureDate);
		assertTrue(valid);
		
		Date pastDate = new GregorianCalendar(2009, 01, 14, 14, 00).getTime();
		valid = futureDateValidator.isValid(pastDate);
		assertFalse(valid);
		
		
	}

}
