/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.tools.spring.tabbedflow;

import gov.nih.nci.cabig.caaers.web.utils.DefaultObjectPropertyReader;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Rhett Sutphin
 */
public class InPlaceEditableTab<C> extends WorkFlowTab<C> {

    private static final String IN_PLACE_PARAM_NAME = "_ajaxInPlaceEditParam";
    private static final String PATH_TO_GET = "_pathToGet";

    public InPlaceEditableTab() {

    }

    public InPlaceEditableTab(String longTitle, String shortTitle, String viewName) {
        super(longTitle, shortTitle, viewName);
    }

    public InPlaceEditableTab(String longTitle, String shortTitle) {
        super(longTitle, shortTitle, "");
    }

    public ModelAndView doInPlaceEdit(HttpServletRequest request, Object command, Errors error) throws Exception {
        String name = request.getParameter(IN_PLACE_PARAM_NAME);
        String value = request.getParameter(name);
        return postProcessInPlaceEditing(request, (C) command, name, value);
    }

    protected ModelAndView postProcessInPlaceEditing(HttpServletRequest request, C command, String property, String value) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String pathToGet = request.getParameter(PATH_TO_GET);

        if (StringUtils.isNotEmpty(pathToGet)) {
            value = (String) new DefaultObjectPropertyReader(command, pathToGet).getPropertyValueFromPath();
        }

        if (value == null) value = "";
        map.put(getFreeTextModelName(), value);
        return new ModelAndView("", map);
    }
}
