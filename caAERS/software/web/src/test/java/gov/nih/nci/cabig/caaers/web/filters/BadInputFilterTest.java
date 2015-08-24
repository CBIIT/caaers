package gov.nih.nci.cabig.caaers.web.filters;


import gov.nih.nci.cabig.caaers.AbstractTestCase;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;

public class BadInputFilterTest extends AbstractTestCase{
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private FilterChain filterChain;
    private MockFilterConfig filterConfig;
    BadInputFilter filter;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        filter = new BadInputFilter();
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        session.setAttribute(CsrfPreventionFilter.CSRF_TOKEN, "123");
        request.setSession(session);
        response = new MockHttpServletResponse();
        filterChain = registerMockFor(FilterChain.class);
        filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("escapeHtmlFormTags", "true");
        filterConfig.addInitParameter("escapeQuotes", "false");
        filterConfig.addInitParameter("escapeJavaScript", "true");
        filterConfig.addInitParameter("allowParams", "notification.content");
        filterConfig.addInitParameter("allowURIs", "rule/create,rule/createOptions,import");
        filter.init(filterConfig);
    }

    public void testDoFilter() throws Exception {
        request.addParameter("p", "abcd onkeyup=\"alert(hello)\"");
        request.addParameter("j", "some junk < input/autofocus onfocus=\"alert(263)\"");
        request.addParameter("k", "some junk <INPUT/autofocus onfocus=\"alert(263)\"");
        request.addParameter("l", "some junk <inPUT/autofocus onfocus=\"alert(263)\"");
        request.addParameter("m", "some junk <input name=\"x\" />");
        request.addParameter("n", "<div><input name=\"x\" /></div>");
        filter.doFilter(request, response, filterChain);
        assertEquals(request.getParameter("p"), "abcd onkeyuq=\"alert[hello)\"") ;
        assertEquals(request.getParameter("j"), "some junk < input/aut0f0cus onf0cus=\"alert[263)\"");
        assertEquals(request.getParameter("k"), "some junk input/aut0f0cus onf0cus=\"alert[263)\"");
        assertEquals(request.getParameter("l"), "some junk input/aut0f0cus onf0cus=\"alert[263)\"");
        assertEquals(request.getParameter("m"), "some junk input name=\"x\" />");
        assertEquals(request.getParameter("n"), "<div>input name=\"x\" /></div>");
    }
}
