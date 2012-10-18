package gov.nih.nci.cabig.caaers.web.utils;


import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.drools.util.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.nih.nci.cabig.caaers.validation.ValidationError;
import gov.nih.nci.cabig.caaers.validation.fields.validators.FieldValidator;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.ctms.domain.CodedEnum;
import java.io.File;

public class WebUtils {

    private static final Log logger = LogFactory.getLog(WebUtils.class);
    
    /**
     * This method will create, options from an enumeration.
     * @param codedEnum
     * @param blankValueLabel
     * @return
     */
    public static Map<Object, Object> collectOptions(CodedEnum<? extends Object>[] codedEnumValues, String blankValueLabel){
    	 Map<Object, Object> options = new LinkedHashMap<Object, Object>();
    	 if (blankValueLabel != null) options.put("", blankValueLabel);
    	 for (int i = 0; i < codedEnumValues.length; i++){
    		 options.put(((Enum)codedEnumValues[i]).name(), codedEnumValues[i].getDisplayName());
    	 }
    	 return options;
    }

    /**
     * Creates and options map using the same principles as spring's <code>form:options</code>
     * tag.
     * 
     * @param items
     *                A collection of items that should make up the options. The options will be in
     *                the same iteration order as this collection.
     * @param itemValueProperty
     *                The property of the collection's elements which should be used as as the
     *                submitted value for each item. If null, the result of
     *                <code>item.toString()</code> will be used instead.
     * @param itemLabelProperty
     *                The property of the collection's elements which should be used as as the
     *                displayed label for each item. If null, the result of
     *                <code>item.toString()</code> will be used instead.
     * @return an options map suitable for use as the {@link gov.nih.nci.cabig.caaers.web.fields.InputField#OPTIONS} attribute
     */
    public static Map<Object, Object> collectOptions(Collection<?> items, String itemValueProperty, String itemLabelProperty, String blankValue) {
        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        if (blankValue != null) options.put("", blankValue);
        for (Object item : items) {
            BeanWrapper wrappedItem = new BeanWrapperImpl(item);
            Object value = extractProperty(wrappedItem, itemValueProperty);
            Object label = extractProperty(wrappedItem, itemLabelProperty);
            options.put(String.valueOf(value), label);
        }
        return options;
    }
    
    /**
     * Creates and options map using the same principles as spring's <code>form:options</code>
     * tag.
     * 
     * @param items
     *                A collection of items that should make up the options. The options will be in
     *                the same iteration order as this collection.
     * @param itemValueProperty
     *                The property of the collection's elements which should be used as as the
     *                submitted value for each item. If null, the result of
     *                <code>item.toString()</code> will be used instead.
     * @param itemLabel1Property
     *                The property of the collection's elements which should be used as as the
     *                displayed label for each item before the separator. If null, the result of
     *                <code>item.toString()</code> will be used instead.
     * @param itemLabel2Property
     *                The property of the collection's elements which should be used as as the
     *                displayed label for each item after the separator. If null, the result of
     *                <code>item.toString()</code> will be used instead.
     * @param separator
     * 				  The string that separates label1 and label2 property.                                             
     * @param items
     * @param itemValueProperty
     * @param itemLabel1Property
     * @param itemLabel2Property
     * @param separator
     * @return
     */
    public static Map<Object, Object> collectCustomOptions(Collection<?> items, String itemValueProperty,
    			String itemLabel1Property, String itemLabel2Property, String separator) {
    	Map<Object, Object> options = new LinkedHashMap<Object, Object>();
    	for (Object item : items) {
    			BeanWrapper wrappedItem = new BeanWrapperImpl(item);
    			Object value = extractProperty(wrappedItem, itemValueProperty);
    			Object label1 = extractProperty(wrappedItem, itemLabel1Property);
    			Object label2 = extractProperty(wrappedItem, itemLabel2Property);
    			if(separator == null)
    				separator = "-";
    			options.put(String.valueOf(value), label1.toString() + separator + label2.toString());
    	}
    	return options;
    }
    
    
    
    public static Map<Object, Object> collectOptions(Collection<?> items, String itemValueProperty, String itemLabelProperty) {
        return collectOptions(items, itemValueProperty, itemLabelProperty, null);
    }

    private static Object extractProperty(BeanWrapper wrappedItem, String propertyName) {
        if (wrappedItem.getWrappedInstance() == null) {
            return null;
        } else if (propertyName == null) {
            return wrappedItem.getWrappedInstance().toString();
        } else {
            return wrappedItem.getPropertyValue(propertyName);
        }
    }
    
    /**
     * Returns the previous page, based on the request parameter _page
     * @param request
     * @return
     */
    public static int getPreviousPage(HttpServletRequest request){
    	String pg = request.getParameter("_page");
    	if(StringUtils.isEmpty(pg)) return -1;
    	return Integer.parseInt(pg);
    }
    
    public static int getTargetPage(HttpServletRequest request){
    	Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (paramName.startsWith("_target")) {
				return Integer.parseInt(paramName.substring(7));
			}
		}
		return -1;
    }
    
    /*
    * Sort a map
    * 
    * */
    public static Map sortMapByKey(Map map, final boolean ignoreCase) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (ignoreCase)
                    return ((Comparable) ((Map.Entry) (o1)).getKey().toString().toLowerCase()).compareTo(((Map.Entry) (o2)).getKey().toString().toLowerCase());
                else
                    return ((Comparable) ((Map.Entry) (o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /*
    * Errors coming from validate() methods
    * */
    public static void populateErrorFieldNames(Map<String, Boolean> map, Errors errors) {
        logger.debug("F: populateErrorFieldNames(Map map, Errors errors)");
        if (map == null || errors == null || errors.getFieldErrors() == null|| errors.getFieldErrors().size() == 0) return;

        Iterator it = errors.getFieldErrors().iterator();
        while (it.hasNext()) {
            FieldError fe = (FieldError)it.next();
            String subName = fe.getField().substring(0, fe.getField().indexOf("].") + 1).toString();
            map.put(subName, Boolean.TRUE);
            logger.debug("FE: " + fe.getField());
        }
    }

    public static void rejectErrors(Errors errors, ValidationError ve) {
        logger.debug("F: rejectErrors(Errors errors, String...fieldNames)");
        if (errors == null || ve.getFieldNames() == null || ve.getFieldNames().length == 0) return;
        String subName = ve.getFieldNames()[0];
        errors.rejectValue(subName, ve.getCode(), ve.getMessage());
        logger.debug("F:rejectErrors: " + subName);
    }

    public static void populateErrorFieldNames(Map<String, Boolean> map, String... fieldNames) {
        logger.debug("F: populateErrorFieldNames(Map map, String... fieldNames)");
        for (byte i=0; i<fieldNames.length; i++) {
            String subName = fieldNames[i].substring(0, fieldNames[i].indexOf("].") + 1).toString();
            map.put(subName, Boolean.TRUE);
        }
    }

    public static void synchronzeErrorFields(Map<String, Boolean> m1, Map<String, Boolean> m2) {
        if (m1 == null || m2 == null) return;
        if (m2.size() == 0) return;
        
        logger.debug("F: synchronzeErrorFields");
        logger.debug("F: " + m1);
        logger.debug("F: " + m2);
        Iterator it = m2.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            m1.put(key, Boolean.TRUE);
            logger.debug("F: This field copied from XML Rules Files to the Mandatory Fields Hashmap: " + key);
        }
    }
    
    public static boolean hasParameter(HttpServletRequest request, String paramName){
    	for(Enumeration en = request.getParameterNames(); en.hasMoreElements(); ){
    		if(en.nextElement().equals(paramName)) return true;
    	}
    	return false;
    }
    
    public static String getStringParameter(HttpServletRequest request, String param){
    	String value = request.getParameter(param);
    	if(value == null) value = "";
    	return value;
    }
    
    public static int getIntParameter(HttpServletRequest request, String param){
    	return Integer.parseInt(getStringParameter(request, param));
    }

    public static int[] getIntParameters(HttpServletRequest request, String param){
    	ArrayList<Integer> values = new ArrayList<Integer>();
    	int ival =  Integer.parseInt(getStringParameter(request, param));
    	values.add(ival);
    	int[] ivals = new  int[values.size()];
    	for(int i = 0; i < ivals.length; i++){
    		ivals[i] = values.get(i);
    	}
    	return ivals;
    }

    /*
    *
    * Will keep only the studies Coordinated or Sponsored by one of the organizations in the Set
    *
    * */
    public static List<Study> filterStudiesByCoordinatorOrSponsor(List<Study> studies, Set<Organization> organizations, Organization subjectOrganization) {

        List<Study> _s = new ArrayList<Study>();

        for (Study s : studies) {
            boolean isGood = false;

            Organization scc = s.getStudyCoordinatingCenter().getOrganization();
            Organization sfs = s.getPrimaryFundingSponsorOrganization();

            // select studies coordinated by RS
            if (organizations.contains(scc) || organizations.contains(sfs)) {
                isGood = true;
            }

            // add studies where RS's organizaton is just a StudySite if it's the same as Subject's Site
            if (organizations.contains(subjectOrganization)) {
                for (StudySite ss : s.getStudySites()) {
                    if (ss.getOrganization().equals(subjectOrganization)) isGood = true;
                }
            }

            if (isGood) _s.add(s);

        }


        return _s;
    }


    public static String getFilePath(String aeReportId, String reportId, String pageNumber) {
		  String tempDir = System.getProperty("java.io.tmpdir");
		  String pngOutFile = "";
		 if(pageNumber.equalsIgnoreCase("1")) {
			  pngOutFile = tempDir+ File.separator + "expeditedAdverseEvent-"+ aeReportId + "-" + reportId + "report.png";
		 }else {
			  pngOutFile = tempDir+ File.separator + "expeditedAdverseEvent-"+ aeReportId + "-" + reportId+ "report" + pageNumber + ".png";
		 }
		  return pngOutFile;


	}

    public static String getBaseFileName(String aeReportId, String reportId) {

        return "expeditedAdverseEvent-" + aeReportId + "-" + reportId + "report.png";

    }
    /*
    *
    * Will keep only the studies Coordinated or Sponsored by Research Staff's organizations
    * 
    * */
    public static List<Study> filterStudiesForResearchStaff(List<Study> studies, ResearchStaff rs, Organization subjectOrganization) {
        Set<Organization> orgs = new HashSet<Organization>();
        for (SiteResearchStaff srs : rs.getSiteResearchStaffs()) {
            orgs.add(srs.getOrganization());
        }

        return filterStudiesByCoordinatorOrSponsor(studies, orgs, subjectOrganization);
    }

    public static Set<Organization> extractOrganizations(ResearchStaff rs) {
        Set<Organization> orgs = new HashSet<Organization>();
        for (SiteResearchStaff srs : rs.getSiteResearchStaffs()) {
            orgs.add(srs.getOrganization());
        }
        return orgs;
    }

    public static Set<Organization> extractOrganizations(Investigator i) {
        Set<Organization> orgs = new HashSet<Organization>();
        for (SiteInvestigator si : i.getSiteInvestigators()) {
            orgs.add(si.getOrganization());
        }
        return orgs;
    }


}
