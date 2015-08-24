/*
 * $Revision$
 * $Date$
 *
 * Copyright (c) 2011 Jason Brittain.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * Written by Jason Brittain for Tomcat: The Definitive Guide, 2nd Edition
 */

package gov.nih.nci.cabig.caaers.web.filters;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Filters out bad user input from HTTP requests to avoid malicious
 * attacks including Cross Site Scripting (XSS), SQL Injection, and
 * HTML Injection vulnerabilities, among others.
 *
 * @author Jason Brittain
 * @author John Moore (filterable request)
 */
public class BadInputFilter implements Filter {

    protected static final Log logger = LogFactory.getLog(BadInputFilter.class);

    // --------------------------------------------- Static Variables

    /**
     * Descriptive information about this implementation.
     */
    protected static String info =
            "com.oreilly.tomcat.filter.BadInputFilter/2.0";

    /**
     * An empty String array to re-use as a type indicator for toArray().
     */
    private static final String[] STRING_ARRAY = new String[0];

    // ------------------------------------------- Instance Variables

    /**
     * The attribute that determines whether or not to skip a url for filtering.
     */
    private List<String> allowURIs = new ArrayList<String>();

    /**
     * The attribute that determines whether or not to skip a parameter for filtering.
     */
    private List<String> allowParams = new ArrayList<String>();

    /**
     * The flag that determines whether or not to escape quotes that are
     * part of the request.
     */
    protected boolean escapeQuotes = false;

    /**
     * The flag that determines whether or not to escape angle brackets
     * that are part of the request.
     */
    protected boolean escapeAngleBrackets = false;

    /**
     * The flag that determines whether or not to escape html form elements
     * that are part of the request.
     */
    protected boolean escapeHtmlFormTags = false;

    /**
     * The flag that determines whether or not to escape JavaScript
     * function and object names that are part of the request.
     */
    protected boolean escapeJavaScript = false;

    /**
     * A substitution mapping (regular expression to match, replacement)
     * that is used to replace single quotes (') and double quotes (")
     * with escaped equivalents that can't be used for malicious purposes.
     */
    protected LinkedHashMap<Pattern, String> quotesLinkedHashMap = new LinkedHashMap<Pattern, String>();

    /**
     * A substitution mapping (regular expression to match, replacement)
     * that is used to replace angle brackets (<>) with escaped
     * equivalents that can't be used for malicious purposes.
     */
    protected LinkedHashMap<Pattern, String> angleBracketsLinkedHashMap = new LinkedHashMap<Pattern, String>();

    /**
     * A substitution mapping (regular expression to match, replacement)
     * that is used to replace potentially dangerous JavaScript function
     * calls with escaped equivalents that can't be used for malicious
     * purposes.
     */
    protected LinkedHashMap<Pattern, String> javaScriptLinkedHashMap = new LinkedHashMap<Pattern, String>();

    /**
     * Will store the HTML form tags that we need to escape in parameter names and values
     */
    protected LinkedHashMap<Pattern, String> newlinesLinkedHashMap = new LinkedHashMap<Pattern, String>();


    /**
     * Will store the patterns for new line related changes
     */
    protected LinkedHashMap<Pattern, String> htmlLinkedHashMap = new LinkedHashMap<Pattern, String>();


    /**
     * The flag that determines whether or not to escape newlines that are part
     * of the request.
     */
    protected boolean escapeNewlines = false;

    /**
     * The comma-delimited set of <code>allow</code> expressions.
     */
    protected String allow = null;

    /**
     * The set of <code>allow</code> regular expressions we will evaluate.
     */
    protected Pattern allows[] = new Pattern[0];

    /**
     * The set of <code>deny</code> regular expressions we will evaluate.
     */
    protected Pattern denies[] = new Pattern[0];

    /**
     * The comma-delimited set of <code>deny</code> expressions.
     */
    protected String deny = null;
    /**
     * A Map of regular expressions used to filter the parameters. The key is
     * the regular expression String to search for, and the value is the regular
     * expression String used to modify the parameter if the search String is found.
     */
    protected Map<Pattern, String> parameterEscapes = new LinkedHashMap<Pattern, String>();


    /**
     * The ServletContext under which this Filter runs.  Used for logging.
     */
    protected ServletContext servletContext;


    // ------------------------------------------------- Constructors

    /**
     * Construct a new instance of this class with default property values.
     */
    public BadInputFilter() {

        // Populate the regex escape maps.

        //commented follwing 2 lines to fix  -
        // CAAERS-6005 - Apostrophe (') in course description is being rendered html character codes in it
        quotesLinkedHashMap.put(Pattern.compile("\""), "&quot;");
        quotesLinkedHashMap.put(Pattern.compile("\'"), "&#39;");
        quotesLinkedHashMap.put(Pattern.compile("`"), "&#96;");

        angleBracketsLinkedHashMap.put(Pattern.compile("<"), "&lt;");
        angleBracketsLinkedHashMap.put(Pattern.compile(">"), "&gt;");

        String[] formTags = "form,textarea,input,button,select,optgroup,label,fieldset".split(",");
        for (String tag : formTags) {
            htmlLinkedHashMap.put(Pattern.compile("<" + tag + "(\\s*)", Pattern.CASE_INSENSITIVE), tag + "$1");
        }

        javaScriptLinkedHashMap.put(Pattern.compile("<(\\s*)(/\\s*)?script(\\s*)>"), "<$2script-disabled>");
        javaScriptLinkedHashMap.put(Pattern.compile("%3Cscript%3E"), "%3Cscript-disabled%3E");
        javaScriptLinkedHashMap.put(Pattern.compile("<(\\s*)(/\\s*)?iframe(\\s*)>"), "<$2iframe-disabled>");
        javaScriptLinkedHashMap.put(Pattern.compile("%3Ciframe%3E"), "%3Ciframe-disabled%3E");
        javaScriptLinkedHashMap.put(Pattern.compile("alert(\\s*)\\("), "alert[");
        javaScriptLinkedHashMap.put(Pattern.compile("alert%28"), "alert%5B");
        javaScriptLinkedHashMap.put(Pattern.compile("document(.*)\\.(.*)cookie"), "document cookie");
        javaScriptLinkedHashMap.put(Pattern.compile("eval(\\s*)\\("), "eval[");
        javaScriptLinkedHashMap.put(Pattern.compile("setTimeout(\\s*)\\("), "setTimeout$1[");
        javaScriptLinkedHashMap.put(Pattern.compile("setInterval(\\s*)\\("), "setInterval$1[");
        javaScriptLinkedHashMap.put(Pattern.compile("execScript(\\s*)\\("), "execScript$1[");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)javascript(?-i):"), "javascript ");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onclick(?-i)"), "oncl1ck");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)ondblclick(?-i)"), "ondblcl1ck");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onmouseover(?-i)"), "onm0useover");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onmousedown(?-i)"), "onm0usedown");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onmouseup(?-i)"), "onm0useup");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onmousemove(?-i)"), "onm0usemove");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onmouseout(?-i)"), "onm0useout");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onchange(?-i)"), "onchahge");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onfocus(?-i)"), "onf0cus");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)autofocus(?-i)"), "aut0f0cus");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onblur(?-i)"), "onb1ur");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onkeypress(?-i)"), "onkeyqress");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onkeyup(?-i)"), "onkeyuq");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onkeydown(?-i)"), "onkeyd0wn");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onload(?-i)"), "onl0ad");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onreset(?-i)"), "onrezet");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onselect(?-i)"), "onzelect");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onsubmit(?-i)"), "onsubm1t");
        javaScriptLinkedHashMap.put(Pattern.compile("(?i)onunload(?-i)"), "onunl0ad");
        javaScriptLinkedHashMap.put(Pattern.compile("&#x61;&#x6C;&#x65;&#x72;&#x74;"), "a1ert");

        newlinesLinkedHashMap.put(Pattern.compile("\r"), " ");
        newlinesLinkedHashMap.put(Pattern.compile("\n"), "<br/>");
    }

    // --------------------------------------------------- Properties

    public boolean getEscapeHtmlFormTags() {
        return escapeHtmlFormTags;
    }

    public void setEscapeHtmlFormTags(boolean escapeHtmlFormTags) {
        if(escapeHtmlFormTags) parameterEscapes.putAll(htmlLinkedHashMap);
        this.escapeHtmlFormTags = escapeHtmlFormTags;
    }

    /**
     * Gets the flag which determines whether this Filter will escape
     * any quotes (both double and single quotes) that are part of the
     * request, before the request is performed.
     */
    public boolean getEscapeQuotes() {

        return escapeQuotes;

    }

    /**
     * Sets the flag which determines whether this Filter will escape
     * any quotes (both double and single quotes) that are part of the
     * request, before the request is performed.
     *
     * @param escapeQuotes
     */
    public void setEscapeQuotes(boolean escapeQuotes) {
        if(escapeQuotes) parameterEscapes.putAll(quotesLinkedHashMap);
        this.escapeQuotes = escapeQuotes;
    }

    /**
     * Gets the flag which determines whether this Filter will escape
     * any angle brackets that are part of the request, before the
     * request is performed.
     */
    public boolean getEscapeAngleBrackets() {

        return escapeAngleBrackets;

    }

    /**
     * Sets the flag which determines whether this Filter will escape
     * any angle brackets that are part of the request, before the
     * request is performed.
     *
     * @param escapeAngleBrackets
     */
    public void setEscapeAngleBrackets(boolean escapeAngleBrackets) {
        if(escapeAngleBrackets) parameterEscapes.putAll(angleBracketsLinkedHashMap);
        this.escapeAngleBrackets = escapeAngleBrackets;
    }


    /**
     * Gets the flag which determines whether this Filter will escape newlines
     * that are part of the request, before the request is performed.
     */
    public boolean getEscapeNewlines() {
        return escapeNewlines;
    }


    /**
     * Sets the flag which determines whether this Filter will escape newlines
     * that are part of the request, before the request is performed.
     *
     * @param escapeNewlines
     */
    public void setEscapeNewlines(boolean escapeNewlines) {
        if(escapeNewlines) parameterEscapes.putAll(newlinesLinkedHashMap);
        this.escapeNewlines = escapeNewlines;

    }


    /**
     * Gets the flag which determines whether this Filter will escape
     * any potentially dangerous references to JavaScript functions
     * and objects that are part of the request, before the request is
     * performed.
     */
    public boolean getEscapeJavaScript() {
        return escapeJavaScript;
    }

    /**
     * Sets the flag which determines whether this Filter will escape
     * any potentially dangerous references to JavaScript functions
     * and objects that are part of the request, before the request is
     * performed.
     *
     * @param escapeJavaScript
     */
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        if(escapeJavaScript) parameterEscapes.putAll(javaScriptLinkedHashMap);
        this.escapeJavaScript = escapeJavaScript;
    }

    /**
     * Return a comma-delimited set of the <code>allow</code> expressions
     * configured for this Filter, if any; otherwise, return <code>null</code>.
     */
    public String getAllow() {

        return (this.allow);

    }

    /**
     * Set the comma-delimited set of the <code>allow</code> expressions
     * configured for this Filter, if any.
     *
     * @param allow The new set of allow expressions
     */
    public void setAllow(String allow) {

        this.allow = allow;
        allows = precalculate(allow);
        logger.debug("BadInputFilter: allow = " + allow);

    }

    /**
     * Return a comma-delimited set of the <code>deny</code> expressions
     * configured for this Filter, if any; otherwise, return
     * <code>null</code>.
     */
    public String getDeny() {

        return (this.deny);

    }

    /**
     * Set the comma-delimited set of the <code>deny</code> expressions
     * configured for this Filter, if any.
     *
     * @param deny The new set of deny expressions
     */
    public void setDeny(String deny) {

        this.deny = deny;
        denies = precalculate(deny);
        logger.debug("BadInputFilter: deny = " + deny);

    }

    // ----------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig filterConfig) throws ServletException {

        servletContext = filterConfig.getServletContext();

        // Parse the Filter's init parameters.
        String allowString = filterConfig.getInitParameter("allowURIs");
        if (!StringUtils.isBlank(allowString)) {
            this.allowURIs = Arrays.asList(allowString.split(",\\s*"));
        }
        String allowParams = filterConfig.getInitParameter("allowParams");
        if (!StringUtils.isBlank(allowParams)) {
            this.allowParams = Arrays.asList(allowParams.split(",\\s*"));
        }
        setAllow(filterConfig.getInitParameter("allow"));
        setDeny(filterConfig.getInitParameter("deny"));
        String initParam = filterConfig.getInitParameter("escapeQuotes");
        if (initParam != null) {
            boolean flag = Boolean.parseBoolean(initParam);
            setEscapeQuotes(flag);
        }
        initParam = filterConfig.getInitParameter("escapeAngleBrackets");
        if (initParam != null) {
            boolean flag = Boolean.parseBoolean(initParam);
            setEscapeAngleBrackets(flag);
        }
        initParam = filterConfig.getInitParameter("escapeHtmlFormTags");
        if (initParam != null) {
            boolean flag = Boolean.parseBoolean(initParam);
            setEscapeHtmlFormTags(flag);
        }
        initParam = filterConfig.getInitParameter("escapeNewlines");
        if (initParam != null) {
            boolean flag = Boolean.parseBoolean(initParam);
            setEscapeNewlines(flag);
        }

        initParam = filterConfig.getInitParameter("escapeJavaScript");
        if (initParam != null) {
            boolean flag = Boolean.parseBoolean(initParam);
            setEscapeJavaScript(flag);
        }

//        logger.debug(toString() + " initialized.");
        logger.debug(toString() + " initialized.");

    }

    /**
     * Sanitizes request parameters before bad user input gets into the
     * web application.
     *
     * @param request  The servlet request to be processed
     * @param response The servlet response to be created
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {

        // Skip filtering for non-HTTP requests and responses.
        if (!(request instanceof HttpServletRequest) ||
                !(response instanceof HttpServletResponse)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAllowedURI(((HttpServletRequest) request).getRequestURI())) {
            logger.debug("Skip filtering: '" + ((HttpServletRequest) request).getRequestURI() + "'");
            filterChain.doFilter(request, response);
            return;
        }

        // Only let requests through based on the allows and denies.
        if (processAllowsAndDenies(request, response)) {

            // Filter the input for potentially dangerous JavaScript
            // code so that bad user input is cleaned out of the request
            // by the time Tomcat begins to perform the request.
            FilterableRequest filterableRequest = new FilterableRequest((HttpServletRequest) request);
            filterParameters(filterableRequest);

            // Perform the request.
            filterChain.doFilter(filterableRequest, response);
        }

    }

    /**
     * Stops requests that contain forbidden string patterns in parameter
     * names and parameter values.
     *
     * @param request  The servlet request to be processed
     * @param response The servlet response to be created
     * @return false if the request is forbidden, true otherwise.
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public boolean processAllowsAndDenies(ServletRequest request,
                                          ServletResponse response)
            throws IOException, ServletException {

        @SuppressWarnings("unchecked")
        Map<String, String[]> paramMap = request.getParameterMap();
        // Loop through the list of parameters.
        Iterator<String> y = paramMap.keySet().iterator();
        while (y.hasNext()) {
            String name = y.next();
            String[] values = request.getParameterValues(name);

            // See if the name contains a forbidden pattern.
            if (!checkAllowsAndDenies(name, response)) {
                return false;
            }

            // Check the parameter's values for the pattern.
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!checkAllowsAndDenies(value, response)) {
                        return false;
                    }
                }
            }
        }

        // No parameter caused a deny.  The request should continue.
        return true;

    }

    /**
     * Perform the filtering that has been configured for this Filter,
     * matching against the specified request property. If the request
     * is allowed to proceed, this method returns true.  Otherwise,
     * this method sends a Forbidden error response page, and returns
     * false.
     * <p/>
     * <br><br>
     * <p/>
     * This method borrows heavily from RequestFilterValve.process().
     *
     * @param property The request property on which to filter
     * @param response The servlet response to be processed
     * @return true if the request is still allowed to proceed.
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public boolean checkAllowsAndDenies(String property,
                                        ServletResponse response)
            throws IOException, ServletException {

        // If there were no denies and no allows, process the request.
        if (denies.length == 0 && allows.length == 0) {
            return true;
        }

        // Check the deny patterns, if any
        for (int i = 0; i < denies.length; i++) {
            Matcher m = denies[i].matcher(property);
            if (m.find()) {
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse hres =
                            (HttpServletResponse) response;
                    hres.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }

        // Check the allow patterns, if any
        for (int i = 0; i < allows.length; i++) {
            Matcher m = allows[i].matcher(property);
            if (m.find()) {
                return true;
            }
        }

        // Allow if denies specified but not allows
        if (denies.length > 0 && allows.length == 0) {
            return true;
        }

        // Otherwise, deny the request.
        if (response instanceof HttpServletResponse) {
            HttpServletResponse hres = (HttpServletResponse) response;
            hres.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        return false;

    }

    /**
     * Filters all existing parameters for potentially dangerous content,
     * and escapes any if they are found.
     *
     * @param request The FilterableRequest that contains the parameters.
     */
    public void filterParameters(FilterableRequest request) {
        Map<String, String[]> paramMap = request.getModifiableParameterMap();

        // Loop through each of the substitution patterns.
        for (Pattern pattern : parameterEscapes.keySet()) {
            // Loop through the list of parameter names.
            for (String name : paramMap.keySet()) {
                String[] values = request.getParameterValues(name);

                // See if the name contains the pattern.
                Matcher matcher = pattern.matcher(name);
                if (matcher.find()) {
                    // The parameter's name matched a pattern, so we
                    // fix it by modifying the name, adding the parameter
                    // back as the new name, and removing the old one.
                    String newName = matcher.replaceAll((String) parameterEscapes.get(pattern));
                    paramMap.remove(name);
                    paramMap.put(newName, values);
                }
            }

            // Loop through the list of parameter values for each name.
            for (String name : paramMap.keySet()) {
                String[] values = request.getParameterValues(name);
                // Check the parameter's values for the pattern.
                if (values != null) {
                    for (int i = 0; i < values.length; ++i) {
                        String value = values[i];
                        Matcher matcher = pattern.matcher(value);
                        if (matcher.find()) {
                            // The value matched, so we modify the value
                            // and then set it back into the array.
                            String newValue;
                            newValue = matcher.replaceAll((String) parameterEscapes.get(pattern));
                            values[i] = newValue;
                        }
                    }
                }
            }
        }
    }


    /**
     * Return a text representation of this object.
     */
    @Override
    public String toString() {

        return "BadInputFilter";

    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {

    }

    // -------------------------------------------- Protected Methods

    /**
     * Return an array of regular expression objects initialized from the
     * specified argument, which must be <code>null</code> or a
     * comma-delimited list of regular expression patterns.
     *
     * @param list The comma-separated list of patterns
     * @throws IllegalArgumentException if one of the patterns has
     *                                  invalid syntax
     */
    protected Pattern[] precalculate(String list) {

        if (list == null)
            return (new Pattern[0]);
        list = list.trim();
        if (list.length() < 1)
            return (new Pattern[0]);
        list += ",";

        ArrayList<Pattern> reList = new ArrayList<Pattern>();
        while (list.length() > 0) {
            int comma = list.indexOf(',');
            if (comma < 0)
                break;
            String pattern = list.substring(0, comma).trim();
            try {
                reList.add(Pattern.compile(pattern));
            } catch (PatternSyntaxException e) {
                IllegalArgumentException iae = new IllegalArgumentException(
                        "Syntax error in request filter pattern" + pattern);
                iae.initCause(e);
                throw iae;
            }
            list = list.substring(comma + 1);
        }

        Pattern reArray[] = new Pattern[reList.size()];
        return reList.toArray(reArray);

    }

    private boolean isAllowedURI(String url) {
        for (String allowed : this.allowURIs) {
            //if(StringUtils.containsIgnoreCase(url, allowed)){
            if (url.endsWith(allowed)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedParam(String param) {
        for (String allowed : this.allowParams) {
            //if(StringUtils.containsIgnoreCase(url, allowed)){
            if (param.equals(allowed)) {
                return true;
            }
        }
        return false;
    }
}
