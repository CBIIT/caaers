package gov.nih.nci.cabig.caaers.web.filters;
/*
 * Copyright (c) 2013 John I. Moore, Jr. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.cxf.common.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Wraps an <code>HttpServletRequest</code> so that parameters can
 * be modified by a filter.
 *
 * Copied from jawaworld article
 * www.javaworld.com/article/2078901/open-source-tools/badinputfilter-revisited.html?page=3
 *
 * @author John I. Moore, Jr. (original)
 *
 */
public class FilterableRequest extends HttpServletRequestWrapper
{
    private Map<String, String[]> parameters = null;

    /**
     * Construct a wrapper for the original request.
     *
     * @param request the original HttpServletRequest
     */
    public FilterableRequest(HttpServletRequest request)
    {
        super(request);
        parameters = new TreeMap<String, String[]>();
        parameters.putAll(super.getParameterMap());
    }

    @Override
    public String getParameter(final String name)
    {
        String[] values = parameters.get(name);
        return values != null ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Returns a ParameterMap that can be modified.
     */
    protected Map<String, String[]> getModifiableParameterMap()
    {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(final String name)
    {
        return parameters.get(name);
    }

    @Override
    public String getHeader(String name) {
        return sanitizeHeader(getHeader(name));
    }

    private String sanitizeHeader(String value) {
        if(StringUtils.isEmpty(value)) return value;
        return value.replaceAll("<", "&lt;")
                .replaceAll("\\(", "&#40;")
                .replaceAll("'", "&#39;");
    }
}
