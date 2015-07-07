/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.ws;

import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.ws.faults.CaaersFault;

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
@WebService(name="SAEEvaluationServiceInterface",
			targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules")
public interface SAEEvaluationService {
	/**
	 *  Evaluate Adverse Events for a Study from an external system.<br/>
	 *  Refer to SAERulesServiceSchema.xsd for schema definition.<br/>
	 * Few rules to enforce in implementation , return messages to client in CaaersServiceResponse.<br/>
	 *	1. Study should be existing in caAERS.<br/>
	 *  2. Site should exist in caAERS but need not be study site
	 * @param evaluateAEsInputMessage
	 * @return gov.nih.nci.cabig.caaers.webservice.CaaersServiceResponse
	 */
	@WebMethod	
	public EvaluateAEsOutputMessage evaluateAEs(@WebParam(name="EvaluateAEsInputMessage", targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules") EvaluateAEsInputMessage evaluateAEsInputMessage) throws CaaersFault ;

    /**
     *  Evaluate Adverse Events for a Study from an external system.<br/>
     *  Refer to SAERulesServiceSchema.xsd for schema definition.<br/>
     * Few rules to enforce in implementation , return messages to client in CaaersServiceResponse.<br/>
     *	1. Study should be existing in caAERS.<br/>
     *  2. Site should exist in caAERS but need not be study site
     * @param saveAndEvaluateAEsInputMessage
     * @return gov.nih.nci.cabig.caaers.webservice.CaaersServiceResponse
     */
    @WebMethod
    public SaveAndEvaluateAEsOutputMessage saveAndEvaluateAEs(@WebParam(name="SaveAndEvaluateAEsInputMessage", targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules") SaveAndEvaluateAEsInputMessage saveAndEvaluateAEsInputMessage) throws CaaersFault ;
	
    
    @WebMethod
    public EvaluateAndInitiateOutputMessage EvaluateAndInitiate
    (@WebParam(name="EvaluateAndInitiateInputMessage",
            targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/saerules")
    EvaluateAndInitiateInputMessage evaluateAndInitiateInputMessage) throws CaaersFault;
	
}
