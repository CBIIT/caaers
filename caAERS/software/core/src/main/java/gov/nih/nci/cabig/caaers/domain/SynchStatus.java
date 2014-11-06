/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import static gov.nih.nci.cabig.ctms.domain.CodedEnumHelper.getByClassAndCode;
import static gov.nih.nci.cabig.ctms.domain.CodedEnumHelper.register;
import static gov.nih.nci.cabig.ctms.domain.EnumHelper.sentenceCasedName;
import gov.nih.nci.cabig.ctms.domain.CodedEnum;

public enum SynchStatus implements CodedEnum<Integer> {
	
     REQUEST_SENT_TO_SERVICEMIX(1,"caAERS sent request to Servicemix"),
	 REQUEST_RECEIVED(5, "Message Received"),
     ROUTED_TO_ADEERS_REQUEST_SINK(10, "Message Routed to AdEERS Request Sink Channel"),
     ROUTED_TO_ADEERS_RESPONSE_SINK(11, "Message Routed to AdEERS Response Sink Channel"),
     ROUTED_TO_ADEERS_WS_INVOCATION_CHANNEL(20, "Routed to AdEERS Webservice Invocation route"),
     ADEERS_WS_IN_TRANSFORMATION(30, "AdEERS Webservice request transformation"),
     ADEERS_WS_INVOCATION_INITIATED(35, "AdEERS Webservice invocation initiated"),
     ADEERS_WS_INVOCATION_COMPLETED(36, "AdEERS Webservice invocation completed"),
     ADEERS_WS_OUT_TRANSFORMATION(40, "AdEERS Webservice response transformation") ,
     ROUTED_TO_CAAERS_REQUEST_SINK(50, "Message Routed to caAERS Request Sink Channel"),
     ROUTED_TO_CAAERS_RESPONSE_SINK(51, "Message Routed to caAERS Response Sink Channel"),
     ROUTED_TO_CAAERS_WS_INVOCATION_CHANNEL(52, "Routed to caAERS Webservice Invocation route"),
     CAAERS_WS_IN_TRANSFORMATION(60, "caAERS Webservice request transformation"),
     CAAERS_WS_INVOCATION_INITIATED(65, "caAERS Webservice invocation initiated"),
     CAAERS_WS_INVOCATION_COMPLETED(66, "caAERS Webservice invocation completed"),
     CAAERS_WS_OUT_TRANSFORMATION(70, "caAERS Webservice response transformation") ,

    PRE_PROCESS_EDI_MSG(80, "e2b message preprocessing"),
    PRE_PROCESS_RAV_CAAERS_INTEG_MSG(81, "e2b message EOL preprocessing"),
    E2B_SCHEMATRON_VALIDATION(82, "E2B request schematron validation"),
    POST_PROCESS_EDI_MSG(83, "AdEERS report submission response"),
    ADEERS_REPORT_SUBMISSION_RESPONSE(84, "AdEERS report submission response"),
    ADEERS_REPORT_SUBMISSION_RESPONSE_TRASNSFORMATION(85, "AdEERS report submission response transformation"),


     REQUST_PROCESSING_ERROR(900, "Error while processing request"),
     NO_DATA_AVAILABLE(998, "No data available"),
     REQUEST_COMPLETION(999, "Message processing complete"),
     
     PRE_PROCESS_OPEN_ODM_MSG(12, "Add Exchange headers to OPEN ODM participant message");


// need more fine grained stages in caAERS?. Rename Stage.
    private Integer code;
    
    private String stageName;

    private SynchStatus(int code, String stageName) {
        this.code = code;
        this.stageName=stageName;
        register(this);
    }

    public String getStageName() {
		return stageName;
	}

	public Integer getCode() {
        return code;
    }

    public String getDisplayName() {
        return sentenceCasedName(this);
    }

    public String getName() {
        return name();
    }

    public static SynchStatus getByCode(Integer code) {
        return getByClassAndCode(SynchStatus.class, code);
    }
}
