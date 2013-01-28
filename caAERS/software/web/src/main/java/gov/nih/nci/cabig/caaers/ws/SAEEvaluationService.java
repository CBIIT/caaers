package gov.nih.nci.cabig.caaers.ws;

import gov.nih.nci.cabig.caaers.integration.schema.common.*;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsInputMessage;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


/**
 * API to evaluate adverse events for SAE reporting using caAERS System. 
 * Other external systems like any CDMS can evaluate their AEs for SAE reporting using this webservice
 * Pre-Conditions:
 * Expects the study to be present in caAERS
 * All required data types are in sync between the systems
 * 
 * @author chandrasekaravr
 *
 */
@WebService(name="SAEEvaluationServiceInterface",targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules")
public interface SAEEvaluationService {
	/**
	 *  Evaluate Adverse Events for a Study from an external system.<br/>
	 *  Refer to SAERulesServiceSchema.xsd for schema definition.<br/>
	 * Few rules to enforce in implementation , return messages to client in CaaersServiceResponse.<br/>
	 *	1. Study should be existing in caAERS.<br/>
	 *  2. Site should exist in caAERS but need not be study site
	 * @param adverseEventsInputMessage
	 * @return gov.nih.nci.cabig.caaers.webservice.CaaersServiceResponse
	 */
	@WebMethod
	public CaaersServiceResponse evaluateAEs(@WebParam(name="EvaluateAEsInputMessage", targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules") EvaluateAEsInputMessage evaluateAEsInputMessage) ;
	
	
}