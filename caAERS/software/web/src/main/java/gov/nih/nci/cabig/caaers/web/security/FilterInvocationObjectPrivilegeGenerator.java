/**
 * 
 */
package gov.nih.nci.cabig.caaers.web.security;

import gov.nih.nci.cabig.caaers.security.authorization.ObjectPrivilegeGenerator;
import org.acegisecurity.intercept.web.FilterInvocation;

import java.util.Map;

/**
 * @author <a href="mailto:ion.olaru@semanticbits.com">Ion C. Olaru</a>
 *
 */
public class FilterInvocationObjectPrivilegeGenerator implements ObjectPrivilegeGenerator<FilterInvocation> {

    protected Map<String, String> objectPrivilegeMap;

    /**
     * The mapping of object-reference to the ObjectPrivilege
     * @param map
     */
    public void setObjectPrivilegeMap(Map<String, String> map) {
        this.objectPrivilegeMap = map;
    }

    public String resolve(FilterInvocation fi) {
        
        String requestURL = fi.getRequestUrl();
        
        //remove ? parameters
        int i = requestURL.indexOf('?');
        String baseURL  = requestURL;

        if(i > 0) baseURL = requestURL.substring(0, i);

        //remove #jsessionId
        i = baseURL.indexOf(';');
        if(i > 0) baseURL = baseURL.substring(0,i);

        return objectPrivilegeMap.get(baseURL);
    }

}
