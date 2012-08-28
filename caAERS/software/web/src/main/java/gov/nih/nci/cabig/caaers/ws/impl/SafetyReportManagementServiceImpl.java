package gov.nih.nci.cabig.caaers.ws.impl;

import gov.nih.nci.cabig.caaers.integration.schema.icsr.Ichicsr;
import gov.nih.nci.cabig.caaers.integration.schema.icsr.Ichicsrack;
import gov.nih.nci.cabig.caaers.ws.SafetyReportManagementService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author: Biju Joseph
 */
@WebService(endpointInterface="gov.nih.nci.cabig.caaers.ws.SafetyReportManagementService",
        serviceName="SafetyReportManagementService",
        targetNamespace="")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)

public class SafetyReportManagementServiceImpl implements SafetyReportManagementService {

    @WebMethod
    public Ichicsrack submitSafetyReport(@WebParam Ichicsr icsr){
        return null;
    }

}
