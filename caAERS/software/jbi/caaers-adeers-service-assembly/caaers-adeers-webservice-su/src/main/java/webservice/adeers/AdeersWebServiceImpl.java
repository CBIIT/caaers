/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package webservice.adeers;


import gov.nih.nci.ctep.adeers.client.AEReportXMLServiceSoapBindingStub;
import gov.nih.nci.ctep.adeers.client.AEReportXMLService_ServiceLocator;
import gov.nih.nci.ctep.adeers.client.ReportingMode;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import webservice.AdeersWebService;


public class AdeersWebServiceImpl implements AdeersWebService {
    private String xmlProlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ;
	Logger log = Logger.getLogger(getClass());


	public String callWebService(String aeReport) throws Exception {
		
		return submitOrWithdraw(aeReport);
	}
	
	private String submitOrWithdraw(String aeReportWithCaaersId) throws Exception {
		ServiceContext serviceContext = ServiceContext.getServiceContext();
		log.info("caAERS-adEERS-Service-Assembly processing report submitted by caAERS"); 
		String aeReport = detach(aeReportWithCaaersId,serviceContext);	
		//FIXME: The below ensures the message is only sent to one system, not all.
		String adeersEPR = serviceContext.externalEPRs.split(",")[0];
		String url=adeersEPR.split("::")[0];
		String uid=adeersEPR.split("::")[1];
		String pwd=adeersEPR.split("::")[2];
		//String clientTrustStore = "caAERs-AdEERS";
		//String userDir = System.getProperty("user.home");
		//String fileSeparator = System.getProperty("file.separator");
		//String clientAbsoluteTrustStore = System.getProperty("user.home") + fileSeparator + clientTrustStore;

		AEReportXMLServiceSoapBindingStub binding;
        try {
            binding = (AEReportXMLServiceSoapBindingStub)   new AEReportXMLService_ServiceLocator(url).getAEReportXMLService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            log.error("caAERS-adEERS-Service-Assembly caught JAX-RPC ServiceException :", jre);
            throw new RuntimeException("Unable to communicate to Adeers report submission webservice", jre);
        }
        // Time out after a minute
        binding.setTimeout(60000);
        binding.setUsername(uid);
        binding.setPassword(pwd);
        aeReport = aeReport.startsWith("<?xml") ? aeReport : (xmlProlog + aeReport);
        StringReader reader = new StringReader(aeReport);
        String reponseStr = "";
        if (serviceContext.withdraw) {
        	log.info("Withdraw to adEERS...");
        	Source attachment = new StreamSource(reader,"");
        	log.info("MESSAGE TO ADEERS : ======================================================\n" + aeReport + "\n===================================================");
	        //call the web service  - withdraw method..              
	        binding.withdrawAEReport( attachment);
	        reponseStr = binding._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString();
            log.info("Actual Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");
	        //attach the id to the returned message
	        reponseStr=reponseStr.replace("</ns1:AEReportCancelInfo>","<CAEERS_AEREPORT_ID>"+serviceContext.caaersAeReportId+"</CAEERS_AEREPORT_ID><CAAERSRID>"+serviceContext.reportId+"</CAAERSRID><SUBMITTER_EMAIL>"+serviceContext.submitterEmail+"</SUBMITTER_EMAIL><MESSAGE_COMBO_ID>"+serviceContext.messageComboId+"</MESSAGE_COMBO_ID></ns1:AEReportCancelInfo>");
	        log.info("Processed Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");
        } else {
	        log.info("Submitting to adEERS...");
	        Source attachment = new StreamSource(reader,"");
	        //call the web service    - submit method ..   
            log.info("MESSAGE TO ADEERS : ======================================================\n" + aeReport + "\n===================================================");
	        binding.submitAEDataXMLAsAttachment(ReportingMode.SYNCHRONOUS, attachment);
	        reponseStr = binding._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString();
            log.info("Actual Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");

            //attach the id to the returned message
	        reponseStr=reponseStr.replace("</ns1:AEReportJobInfo>","<CAEERS_AEREPORT_ID>"+serviceContext.caaersAeReportId+"</CAEERS_AEREPORT_ID><CAAERSRID>"+serviceContext.reportId+"</CAAERSRID><SUBMITTER_EMAIL>"+serviceContext.submitterEmail+"</SUBMITTER_EMAIL><MESSAGE_COMBO_ID>"+serviceContext.messageComboId+"</MESSAGE_COMBO_ID></ns1:AEReportJobInfo>");
            log.info("Processed Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");
        }
        return reponseStr;
		
	}

	private String detach(String aeReportWithCaaersId,ServiceContext serviceContext) throws Exception {
		//detach the id and store it to attach later
		
		int si = aeReportWithCaaersId.indexOf("<CAEERS_AEREPORT_ID>");
		int ei = aeReportWithCaaersId.indexOf("</CAEERS_AEREPORT_ID>");
		serviceContext.caaersAeReportId = aeReportWithCaaersId.substring(si+20, ei);

		si = aeReportWithCaaersId.indexOf("<EXTERNAL_SYSTEMS>");
		ei = aeReportWithCaaersId.indexOf("</EXTERNAL_SYSTEMS>");
		serviceContext.externalEPRs = aeReportWithCaaersId.substring(si+18, ei);
		
		si = aeReportWithCaaersId.indexOf("<CAAERSRID>");
		ei = aeReportWithCaaersId.indexOf("</CAAERSRID>");
		serviceContext.reportId = aeReportWithCaaersId.substring(si+11, ei);
		
		si = aeReportWithCaaersId.indexOf("<SUBMITTER_EMAIL>");
		ei = aeReportWithCaaersId.indexOf("</SUBMITTER_EMAIL>");
		serviceContext.submitterEmail = aeReportWithCaaersId.substring(si+17, ei);
		
		si = aeReportWithCaaersId.indexOf("<MESSAGE_COMBO_ID>");
		ei = aeReportWithCaaersId.indexOf("</MESSAGE_COMBO_ID>");
		serviceContext.messageComboId = aeReportWithCaaersId.substring(si+18, ei);
		
		int withdrawIndex = aeReportWithCaaersId.indexOf("<WITHDRAW>true</WITHDRAW>");
		if (withdrawIndex > 0 ) {
			serviceContext.withdraw = true;
		} 
		
		String aeReport = aeReportWithCaaersId.replace("<CAEERS_AEREPORT_ID>"+serviceContext.caaersAeReportId+"</CAEERS_AEREPORT_ID>", "");
		aeReport = aeReport.replace("<EXTERNAL_SYSTEMS>"+serviceContext.externalEPRs+"</EXTERNAL_SYSTEMS>", "");
		aeReport = aeReport.replace("<CAAERSRID>"+serviceContext.reportId+"</CAAERSRID>", "");
		aeReport = aeReport.replace("<SUBMITTER_EMAIL>"+serviceContext.submitterEmail+"</SUBMITTER_EMAIL>", "");
		aeReport = aeReport.replace("<MESSAGE_COMBO_ID>"+serviceContext.messageComboId+"</MESSAGE_COMBO_ID>", "");
		aeReport = aeReport.replace("<ADDITIONAL_INFORMATION/>", "");
		aeReport = aeReport.replace("<WITHDRAW>true</WITHDRAW>", "");
		return aeReport;
	}
	

	public static class ServiceContext {
		public String caaersAeReportId = "";
		public String externalEPRs = "";
		public String reportId = "";
		public String submitterEmail = "";
		public String messageComboId = "";
		public boolean withdraw = false;
		

		
		public static ServiceContext getServiceContext(){
			return new ServiceContext();
		}
	}
}
