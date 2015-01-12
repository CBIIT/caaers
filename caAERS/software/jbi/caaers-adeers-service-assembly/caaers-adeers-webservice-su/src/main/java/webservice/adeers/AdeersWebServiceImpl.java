/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package webservice.adeers;

import gov.nih.nci.ctep.adeers.ws.types.AEReportXMLServiceSoapBindingStub;
import gov.nih.nci.ctep.adeers.ws.types.AEReportXMLService_ServiceLocator;
import gov.nih.nci.ctep.service.types.ReportingMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.client.Call;
import org.apache.log4j.Logger;

import webservice.AdeersWebService;


public class AdeersWebServiceImpl implements AdeersWebService {
    private static final String xmlProlog = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" ;
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

		AEReportXMLServiceSoapBindingStub binding = new AEReportXMLServiceSoapBindingStub(new URL(url), null);
       
        // Time out after a minute
        binding.setTimeout(60000);
        binding.setUsername(uid);
        binding.setPassword(pwd);
        
        aeReport = aeReport.startsWith("<?xml") ? aeReport.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", xmlProlog) : (xmlProlog + aeReport);
        
        Reader reader = new StringReader(aeReport);
        Source attachment = new StreamSource(reader,"");
        String reponseStr = "";
        
        if (serviceContext.withdraw) {
        	log.info("Withdraw to adEERS...");
        	log.info("MESSAGE TO ADEERS : ======================================================\n" + aeReport + "\n===================================================");
	        //call the web service  - withdraw method..              
	        binding.withdrawAEReport(attachment, "ISO-8859-1");
	        reponseStr = binding._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString();
            log.info("Actual Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");
	        //attach the id to the returned message
	        reponseStr=reponseStr.replaceAll("</ns1:AEReportCancelInfo>", "<CAEERS_AEREPORT_ID>" + serviceContext.caaersAeReportId + "</CAEERS_AEREPORT_ID><REPORT_ID>" + serviceContext.reportId + "</REPORT_ID><SUBMITTER_EMAIL>" + serviceContext.submitterEmail + "</SUBMITTER_EMAIL><MESSAGE_COMBO_ID>" + serviceContext.messageComboId + "</MESSAGE_COMBO_ID></ns1:AEReportCancelInfo>");
	        log.info("Processed Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");
        } else {
	        log.info("Submitting to adEERS...");
	        //call the web service    - submit method ..   
            log.info("MESSAGE TO ADEERS : ======================================================\n" + aeReport + "\n===================================================");
	        binding.submitAEDataXMLAsAttachment(ReportingMode.SYNCHRONOUS, attachment, "ISO-8859-1");
	        reponseStr = binding._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString();
            log.info("Actual Response Received from adEERS: ======================================================\n" + reponseStr + "\n===================================================");

            //attach the id to the returned message
	        reponseStr=reponseStr.replaceAll("</ns1:AEReportJobInfo>", "<CAEERS_AEREPORT_ID>" + serviceContext.caaersAeReportId + "</CAEERS_AEREPORT_ID><REPORT_ID>" + serviceContext.reportId + "</REPORT_ID><SUBMITTER_EMAIL>" + serviceContext.submitterEmail + "</SUBMITTER_EMAIL><MESSAGE_COMBO_ID>" + serviceContext.messageComboId + "</MESSAGE_COMBO_ID></ns1:AEReportJobInfo>");
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
		
		si = aeReportWithCaaersId.indexOf("<REPORT_ID>");
		ei = aeReportWithCaaersId.indexOf("</REPORT_ID>");
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
		
		String aeReport = aeReportWithCaaersId.replaceAll("<CAEERS_AEREPORT_ID>" + serviceContext.caaersAeReportId + "</CAEERS_AEREPORT_ID>", "");
		aeReport = aeReport.replaceAll("<EXTERNAL_SYSTEMS>" + serviceContext.externalEPRs + "</EXTERNAL_SYSTEMS>", "");
		aeReport = aeReport.replaceAll("<REPORT_ID>" + serviceContext.reportId + "</REPORT_ID>", "");
		aeReport = aeReport.replaceAll("<SUBMITTER_EMAIL>" + serviceContext.submitterEmail + "</SUBMITTER_EMAIL>", "");
		aeReport = aeReport.replaceAll("<MESSAGE_COMBO_ID>" + serviceContext.messageComboId + "</MESSAGE_COMBO_ID>", "");
		aeReport = aeReport.replaceAll("<ADDITIONAL_INFORMATION/>", "");
		aeReport = aeReport.replaceAll("<WITHDRAW>true</WITHDRAW>", "");
		return aeReport;
	}
	

	@Deprecated
    private void logToFile(String folderPath, String fileName, String aeReport) {
		//used for Debuging only.
        try {
            File folder = new File(folderPath);
            folder.mkdirs();
            File logFile = new File(folder, fileName);

            FileWriter writer = new FileWriter(logFile);
            writer.write(aeReport);
            writer.close();
        } catch(Exception e) {
            log.error("Problem logging", e);
        }
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
