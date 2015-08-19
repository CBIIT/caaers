package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A dirty way to extend CaaersServiceResponse
 */
public class CaaersServiceResponseWrapper {
    private CaaersServiceResponse caaersServiceResponse;
    private Map<String, Object> additionalData;

    public CaaersServiceResponseWrapper(CaaersServiceResponse caaersServiceResponse) {
        this.caaersServiceResponse = caaersServiceResponse;
        this.additionalData = new LinkedHashMap<String , Object>();
    }

    public CaaersServiceResponse getCaaersServiceResponse() {
        return caaersServiceResponse;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public CaaersServiceResponseWrapper addAdditionalInfo(String key, Object value) {
        additionalData.put(key, value);
        return this;
    }
    public Object getAdditionalInfo(String key) {
        return additionalData.get(key);
    }
}
