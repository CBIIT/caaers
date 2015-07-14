/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.utils;

import gov.nih.nci.cabig.caaers.domain.DateValue;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * @author Ion C. Olaru
 * */
public class DateUtils {

    public static final String DATE_PATTERN= "MM/dd/yyyy";
    public static final String WS_DATE_PATTERN= "yyyy-MM-dd'T'HH:mm:ss";
    public static final String WS_DATE_PATTERN_WITH_TZ= "yyyy-MM-dd'T'HH:mm:ssX";
    public static final String DATE_PATTERN_WITH_TZ= "EEE MMM dd yyyy h:mm:ss a z"; 
    public static final String DATE_WITH_HYPHENS= "MM-dd-yyyy";
    public static final String DATE_WITH_DATETIME= "MM/dd/yyyy HH:mm";
    
    /**
     * Will return the difference in days between the two dates
     * @param d1
     * @param d2
     * @return
     */
    public static long differenceInDays(Date d1, Date d2){
         if(d1 == null  || d2 == null) return Long.MAX_VALUE;
         long  l1 = d1.getTime();
         long l2 = d2.getTime();
         return ( l1 - l2) / (1000 * 60 * 60 * 24);
    }
    
    /**
     * Will return the difference in minutes between the two dates
     * @param d1
     * @param d2
     * @return
     */
    public static long differenceInMinutes(Date d1, Date d2){
         if(d1 == null  || d2 == null) return Long.MAX_VALUE;
         long  l1 = d1.getTime();
         long l2 = d2.getTime();
         return ( l1 - l2) / (1000 * 60);
    }
	/**
	 * Checks whether the given d, is greater than or equal to startDate and less than or equal to endDate.
	 * @param d, cannot be null
	 * @param startDate, cannot be null
	 * @param endDate, if null ignored
	 * @return
	 * {@link NullPointerException} if d or startDate is null
	 */
	public static boolean between(Date d, Date startDate, Date endDate){
		if(endDate == null){
			return compareDate(d, startDate) >=0;
		}else if (compareDate(endDate, d) ==  0){
			return false;
		}else{
			return compareDate(d, startDate) >= 0 && compareDate(d , endDate) <=0;
		}
	}
	
	/**
	 * This is a convenient method to get yesterday date
	 * @return
	 */
	public static Date yesterday(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		Date d = c.getTime();
		d.setHours(0);
		d.setMinutes(0);
		return d;
	}
	
	/**
	 * This is a convenient method to get tomorrow
	 * @return
	 */
	public static Date tomorrow(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		
		Date d = c.getTime();
		d.setHours(0);
		d.setMinutes(0);
		
		return d;
	}
	
	public static Date today(){
		Calendar c = Calendar.getInstance();
		
		Date d = c.getTime();
		d.setHours(0);
		d.setMinutes(0);
		
		return d;
	}

    public static Date firstDayOfThisMonth(){
        return firstDayOfThisMonth(today());
    }

    /**
     * Will return the date representing the first day of this month
     * @param currDate
     * @return
     */
    public static Date firstDayOfThisMonth(Date currDate){
        Calendar c = Calendar.getInstance();
        c.setTime(currDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    public static Date lastDayOfThisMonth(){
        return lastDayOfThisMonth(today());
    }
    /**
     * Will return the date representing the last day of this month
     * @param currDate
     * @return
     */
    public static Date lastDayOfThisMonth(Date currDate){
        Calendar c = Calendar.getInstance();
        c.setTime(currDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * Compares two dates. The time fields are ignored.
     * 
     * @param d1 -
     *                Date 1
     * @param d2 -
     *                Date 2
     * @return 0 if same, -1 if d1 is less than d2.
     */
    public static int compareDate(Date d1, Date d2) {

        if(d1 == null && d2 == null) return 0;
        if (d1 == null && d2 != null) return -1;
        if (d1 != null && d2 == null) return 1;

        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        int x = c1.get(Calendar.YEAR);
        int y = c2.get(Calendar.YEAR);
        if (x != y) return x - y;

        x = c1.get(Calendar.MONTH);
        y = c2.get(Calendar.MONTH);
        if (x != y) return x - y;

        x = c1.get(Calendar.DATE);
        y = c2.get(Calendar.DATE);
        return x - y;
    }
    /**
     *Compares two date objects along with time,
     * @param d1
     * @param d2
     * @return 0 if same, -1 if d1 is less and 1 if d1 is higher. 
     */
    public static int compateDateAndTime(Date d1, Date d2){
    	long t1 = d1.getTime();
    	long t2 = d2.getTime();
    	if(t1 > t2) return 1;
    	if(t1 < t2) return -1;
    	return 0;
    }
    public static String formatDate(Date d){
        if ( d.getHours() > 0 || d.getMinutes() > 0)
            return formatDate(d, DATE_WITH_DATETIME);
        return formatDate(d, DATE_PATTERN);
    }
    public static String formatDateForWS(Date d){
        return formatDate(d, WS_DATE_PATTERN);
    }
    public static String formatDate(Date d, String pattern){
    	return formatDate(d, pattern, TimeZone.getDefault());
    }

    public static String formatDate(Date d, String pattern , TimeZone tz){
        //BJ: date formats are not thread safe.
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(tz);
        return df.format(d);
    }

    public static Date parseDate(String strDate) throws ParseException{
        return parseDate(strDate, "MM/dd/yyyy","MM/dd/yy","M/dd/yyyy", "M/dd/yy","M/d/yyyy","M/d/yy","MM/d/yy","MM/d/yyyy",DATE_WITH_DATETIME);
    }

    public static Date parseDate(String dateStr, String... parsePatterns) throws ParseException{
        
        if (dateStr == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        String strDate = dateStr;
       //do year correction. (partial year >=50 will be 1999 and <50 will be 2000)
       String[] parts = StringUtils.split(dateStr, '/');
       int len = parts.length;

       if(len != 3 || parts[0].length() > 2 || parts[1].length() > 2) throw new ParseException("Unable to parse the date "+strDate, -1);

       String yStr = parts[2];

       if(!(yStr.length() == 4 || yStr.length() == 2 || yStr.length() == 10)) throw new ParseException("Unable to parse the date "+ strDate , -1);
       if(yStr.length() == 2 && StringUtils.isNumeric(yStr)){

           if(Integer.parseInt(yStr) < 50)
               yStr = "20"+yStr;
           else
               yStr = "19" + yStr;

           parts[2] = yStr;
           strDate = StringUtils.join(parts,'/');
       }

        //BJ: date formats are not thread save, so we need to create one each time.
        SimpleDateFormat parser = null;
        ParsePosition pos = new ParsePosition(0);
        for (int i = 0; i < parsePatterns.length; i++) {
            if (i == 0) {
                parser = new SimpleDateFormat(parsePatterns[0]);
            } else {
                parser.applyPattern(parsePatterns[i]);
            }
            pos.setIndex(0);

            Date date = parser.parse(strDate, pos);
            if (date != null && pos.getIndex() == strDate.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + strDate, -1);
    }
    
    public static DateValue parseDateString(String dateString){
    	if(dateString == null) return null;
    	DateValue dv = new DateValue();
    	
    	if(dateString.length() == 4){
    		dv.setYearString(dateString);
    		return dv;
    	}
    	
    	if(dateString.indexOf('/') < 0)  
    		throw new RuntimeException("Unknown format, expected format is 'mm/dd/yyyy' or 'mm/yyyy' or 'yyyy'");
    	 
    	String[] dateParts = dateString.split("/");
    	int size = dateParts.length;
    	//validate year
    	if(dateParts[size - 1].length() != 4) 
    		throw new RuntimeException("Unknown format, expected format is 'mm/dd/yyyy' or 'mm/yyyy' or 'yyyy'");
    	
    	if(size == 2){
    		 if(dateParts[0].length() != 2)
    			 throw new RuntimeException("Unknown format, expected format is 'mm/dd/yyyy' or 'mm/yyyy' or 'yyyy'");
    		
    		 dv.setMonthString(dateParts[0]);
    		 dv.setYearString(dateParts[1]);
    		 
    	}else if (size == 3){
    		if(dateParts[0].length() != 2)
   			 	throw new RuntimeException("Unknown format, expected format is 'mm/dd/yyyy' or 'mm/yyyy' or 'yyyy'");
    		if(dateParts[1].length() != 2)
   			 	throw new RuntimeException("Unknown format, expected format is 'mm/dd/yyyy' or 'mm/yyyy' or 'yyyy'");
    		
    		dv.setMonthString(dateParts[0]);
    		dv.setDayString(dateParts[1]);
   		 	dv.setYearString(dateParts[2]);
    	} else {
    		return null;
    	}
    	
    	return dv;
    }

    /**
     * Validate a date to be a valid calendar date.
     * Input may come with date "00" which means the date was not indicated,
     * in which case we are going to use 01 just for validation purposes.
     *
     * @param date - the date to be validates
     * @return boolean - true if the date is valid, false otherwise
     *
     * */
    public static boolean isValidDate(String date) {
        String cloneDate = null;

        if (date.substring(3, 5).equals("00")) {
            cloneDate = date.replaceFirst("/00/", "/01/");
        } else cloneDate = new String(date);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

        Date testDate;
        try {
            testDate = sdf.parse(cloneDate);
        }
        catch (ParseException e) {
            return false;
        }

        if (!sdf.format(testDate).equals(cloneDate)) {
            return false;
        }

        return true;
    }

    public static boolean isValidDate(DateValue d) {
        if (d.isNull() || d.isEmpty()) return true;
        return isValidDate(d.toString());
    }
    
    public static String formatToWSResponseDateWithTimeZone(Date date){
        return formatDate(date, WS_DATE_PATTERN_WITH_TZ, TimeZone.getTimeZone("UTC"));
    }

}
