package gov.nih.nci.cabig.caaers.web;

import gov.nih.nci.cabig.caaers.utils.CaaersSerializerUtil;
import gov.nih.nci.cabig.caaers.utils.MethodParamsHolder;
import gov.nih.nci.cabig.caaers.utils.ObjectToSerialize;
import gov.nih.nci.cabig.ctms.web.filters.ContextRetainingFilterAdapter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * A filter which implements the Open Session In View pattern. Different from the one built into
 * Spring because this one delegates to an instance of {@link OpenSessionInViewInterceptor}
 * configured in the application context. This permits the use of the same interceptor for deployed
 * code & unit tests.
 * 
 * @see org.springframework.orm.hibernate3.support.OpenSessionInViewFilter
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
/* TODO: this class is shared with PSC. Refactor into a shared library. */
public class OpenSessionInViewInterceptorFilter extends ContextRetainingFilterAdapter {
    private String interceptorBeanName;
    protected final Log log = LogFactory.getLog(getClass());

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        setInterceptorBeanName(filterConfig.getInitParameter("interceptorBeanName"));
    }

    /**
     * @see OpenSessionInViewInterceptor
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = ((HttpServletRequest) request);

        if (log.isDebugEnabled()) {
            log.debug("Opening session for request " + httpReq.getMethod() + ' ' + httpReq.getRequestURI());
        }
        OpenSessionInViewInterceptor interceptor = (OpenSessionInViewInterceptor) getApplicationContext().getBean(getInterceptorBeanName());
        WebRequest webRequest = new ServletWebRequest(httpReq);
        interceptor.preHandle(webRequest);
        try {
            chain.doFilter(request, response);
            interceptor.postHandle(webRequest, null);
        }catch(IOException e){
           serailizeContext(request, interceptor.getSessionFactory().getCurrentSession(), e);
           throw e;
        }catch(ServletException e){
           serailizeContext(request, interceptor.getSessionFactory().getCurrentSession(), e);
           throw e;
        }catch(RuntimeException e){
           serailizeContext(request, interceptor.getSessionFactory().getCurrentSession(), e);
           throw e;
        }finally {
            interceptor.afterCompletion(webRequest, null);
            log.debug("Session closed");
        }
    }

    /**
     * This method will take care of printing the context details. 
     */
    public void serailizeContext(ServletRequest request, Session session, Exception e){
        try{
                ObjectToSerialize os = new ObjectToSerialize();
                if(request instanceof HttpServletRequest){
                    os.setHttpRequest((HttpServletRequest)request);
                    os.setHttpSession(((HttpServletRequest) request).getSession());
                }
                os.setException(e);
                os.setHibernateSession(session);
                os.setMethodParameters(MethodParamsHolder.getParams());
                CaaersSerializerUtil.serialize(os);
        }catch(Exception ex){
            log.warn(ex);
        }
    }

    public String getInterceptorBeanName() {
        return interceptorBeanName;
    }

    public void setInterceptorBeanName(String interceptorBeanName) {
        this.interceptorBeanName = interceptorBeanName;
    }
}
