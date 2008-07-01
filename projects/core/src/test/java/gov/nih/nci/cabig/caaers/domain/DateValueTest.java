package gov.nih.nci.cabig.caaers.domain;

import java.util.Calendar;
import java.util.Date;

import edu.nwu.bioinformatics.commons.DateUtils;
import junit.framework.TestCase;

public class DateValueTest extends TestCase {

	

	public void testDateValueIntegerIntegerInteger() {
		DateValue dv = new DateValue(19,9,2002);
		assertEquals(new Integer(2002), dv.getYear());
		assertEquals(new Integer(9), dv.getMonth());
		assertEquals(new Integer(19), dv.getDay());
		assertEquals("09/19/2002", dv.toString());
	}

	public void testDateValueDate() {
		Date d = DateUtils.createDate(2002, 8, 19);
		DateValue dv = new DateValue(d);
		assertEquals("09/19/2002", dv.toString());
	}

	public void testCompareTo() {
		DateValue dv = new DateValue(19,12,2002);
		Date d = DateUtils.createDate(2002, 11, 19);
		DateValue dv2 = new DateValue(d);
		assertEquals(0, dv.compareTo(dv2));
		
		DateValue dv3 = new DateValue(18,9,2002);
		assertEquals(1,dv2.compareTo(dv3));
		assertEquals(-1,dv3.compareTo(dv2));
		
		assertEquals(1, new DateValue(2002).compareTo(new DateValue(2001)));
		assertEquals(-1, new DateValue(2000).compareTo(new DateValue(2001)));
		assertEquals(-1, new DateValue(2002,01).compareTo(new DateValue(2002,02)));
		assertEquals(-1, new DateValue(2002,01).compareTo(new DateValue(2002,01, 02)));
		
		assertEquals(0, new DateValue(2002, 02,02).compareTo(new DateValue(2002,02,02)));
		assertEquals(0, new DateValue(2002, 02).compareTo(new DateValue(2002,02)));
		assertEquals(0,new DateValue().compareTo(new DateValue()));
		assertEquals(-1, new DateValue().compareTo(new DateValue()));
	}

	public void testToString() {
		DateValue dv = new DateValue(19,9,2002);
		assertEquals("09/19/2002", dv.toString());
	}

	

}
