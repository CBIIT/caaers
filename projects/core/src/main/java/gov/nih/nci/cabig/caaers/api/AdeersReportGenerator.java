package gov.nih.nci.cabig.caaers.api;

import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.PersonContact;
import gov.nih.nci.cabig.caaers.domain.ReportFormatType;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.esb.client.impl.CaaersAdeersMessageBroadcastServiceImpl;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;
import gov.nih.nci.cabig.caaers.utils.XsltTransformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;


public class AdeersReportGenerator {

    
    protected final Log log = LogFactory.getLog(getClass());

    // TO-DO set in spring config
    private String xmlXsltFile = "xslt/Caaers2Adeers-xml-AEReport.xslt";

    private String xslFOXsltFile = "xslt/Caaers2Adeers-pdf-AEReport.xslt";

    private String xslFOMedWatchXsltFile = "xslt/Caaers2Medwatch-pdf-AEReport.xslt";

    private String xslFODCPXsltFile = "xslt/Caaers2DCP-pdf-SAEForm.xslt";

    private String xslFOCIOMSTypeFormXsltFile = "xslt/Caaers2CIOMS-pdf-TypeForm.xslt";

    private String xslFOCIOMSXsltFile = "xslt/Caaers2CIOMS-pdf.xslt";

//BJ : never globally share variables on a multi threaded env (this is a singleton bean).
//    private String pdfOutFile = "/tmp/aeReport.pdf";


    protected  AdverseEventReportSerializer aeReportSerializer;


	public AdeersReportGenerator() {
		aeReportSerializer = new AdverseEventReportSerializer();
	};

    public String getAdeersXml(String adverseEventReportXml) throws Exception {
        XsltTransformer xsltTrans = new XsltTransformer();
        String transformedToAdeers = xsltTrans.toXml(adverseEventReportXml, xmlXsltFile);

        return transformedToAdeers;
    }



    public void generatePdf(String adverseEventReportXml, String pdfOutFileName) throws Exception {

        XsltTransformer xsltTrans = new XsltTransformer();
        xsltTrans.toPdf(adverseEventReportXml, pdfOutFileName, xslFOXsltFile);
    }



    public void generateDcpSaeForm(String adverseEventReportXml, String pdfOutFileName)
                    throws Exception {

        XsltTransformer xsltTrans = new XsltTransformer();
        xsltTrans.toPdf(adverseEventReportXml, pdfOutFileName, xslFODCPXsltFile);
    }

    public void generateCIOMSTypeForm(String adverseEventReportXml, String pdfOutFileName)
                    throws Exception {

        XsltTransformer xsltTrans = new XsltTransformer();
        xsltTrans.toPdf(adverseEventReportXml, pdfOutFileName, xslFOCIOMSTypeFormXsltFile);
    }

    public void generateCIOMS(String adverseEventReportXml, String pdfOutFileName) throws Exception {

        XsltTransformer xsltTrans = new XsltTransformer();
        xsltTrans.toPdf(adverseEventReportXml, pdfOutFileName, xslFOCIOMSXsltFile);
    }

    public void generateMedwatchPdf(String adverseEventReportXml, String pdfOutFileName)
                    throws Exception {

        XsltTransformer xsltTrans = new XsltTransformer();
        xsltTrans.toPdf(adverseEventReportXml, pdfOutFileName, xslFOMedWatchXsltFile);
    }
    /**
     * This method will generate the caAERS internal xml representation of the report.
     * @param aeReport
     */
    public String generateCaaersXml(ExpeditedAdverseEventReport aeReport) throws Exception{
    	return aeReportSerializer.serialize(aeReport);
    }
    
    /**
     * This method will generate the PDF file and store it in the file system and return its path.
     * @param report
     * @param caaersXml
     * @return
     * @throws Exception
     */
    public String[] generateExternalReports(Report report, String caaersXml) throws Exception {
    	assert report != null;
    	ReportFormatType formatType = report.getReportDefinition().getReportFormatType();
    	Integer aeReportId = report.getAeReport().getId();
    	
    	String pdfOutFile = System.getProperty("java.io.tmpdir");
    	
    	switch (formatType) {
			case DCPSAEFORM:
				pdfOutFile += "/dcpSAEForm-" + aeReportId + ".pdf";
	        	this.generateDcpSaeForm(caaersXml, pdfOutFile);
				break;
			case MEDWATCHPDF:
				pdfOutFile += "/medWatchReport-" + aeReportId + ".pdf";
	        	this.generateMedwatchPdf(caaersXml, pdfOutFile);
				break;
			case CIOMSFORM:
				pdfOutFile += "/CIOMSForm-" + aeReportId + ".pdf";
	        	this.generateCIOMS(caaersXml, pdfOutFile);
				break;
			case CIOMSSAEFORM:
				pdfOutFile += "/CIOMS-SAE-Form-" + aeReportId + ".pdf";
	        	this.generateCIOMSTypeForm(caaersXml, pdfOutFile);
				break;
			default: //adders /caaersxml (BJ)
				pdfOutFile  += "/expeditedAdverseEventReport-" + report.getAeReport().getId() + ".pdf";
				generatePdf(caaersXml, pdfOutFile);
				break;
		}
    	return new String[] { pdfOutFile };
    	
    }
  



	public void setAeReportSerializer(AdverseEventReportSerializer aeReportSerializer) {
		this.aeReportSerializer = aeReportSerializer;
	}
	
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str1 = "";
        try {
            AdeersReportGenerator aeg = new AdeersReportGenerator();
            FileReader input = new FileReader(
                            "/Users/sakkala/tech/caaers/dcp/ae.xml");
            BufferedReader bufRead = new BufferedReader(input);
            String line = bufRead.readLine();

            while (line != null) {
                str1 = str1 + line;
                line = bufRead.readLine();
            }
            // System.out.println(str1);

            aeg.generateMedwatchPdf(str1, "/Users/sakkala/tech/caaers/dcp/myMedWatch.pdf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
