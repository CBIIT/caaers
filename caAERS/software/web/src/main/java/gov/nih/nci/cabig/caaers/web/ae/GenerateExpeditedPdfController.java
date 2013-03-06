/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.api.AdeersReportGenerator;
import gov.nih.nci.cabig.caaers.api.AdverseEventReportSerializer;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.report.Mandatory;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportContent;

import java.io.*;
import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nih.nci.cabig.caaers.domain.report.ReportMandatoryFieldDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class GenerateExpeditedPdfController extends AbstractCommandController {

    private static final Log log = LogFactory.getLog(GenerateExpeditedPdfController.class);

	private ReportDao reportDao;
	private ExpeditedAdverseEventReportDao aeReportDao;
	private AdeersReportGenerator adeersReportGenerator;
	
	public GenerateExpeditedPdfController() {
		setCommandClass(GenerateExpeditedPdfCommand.class);
    }
	
	private void generateOutput(String outFile,HttpServletResponse response,Integer reportId) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
		File file = new File(tempDir + File.separator + outFile);
        FileInputStream fileIn = null;
        OutputStream out = null;
        
        try {
            fileIn = new FileInputStream(file);
            response.setContentType( "application/x-download" );
            response.setHeader( "Content-Disposition", "attachment; filename="+outFile );
            response.setHeader("Content-length", String.valueOf(file.length()));
            response.setHeader("Pragma", "private");
            response.setHeader("Cache-control","private, must-revalidate");

            out = response.getOutputStream();

            byte[] buffer = new byte[2048];
            int bytesRead = fileIn.read(buffer);
            while (bytesRead >= 0) {
              if (bytesRead > 0)
                out.write(buffer, 0, bytesRead);
                bytesRead = fileIn.read(buffer);
            }
        } catch (FileNotFoundException e) {
            log.error("File not found: " + file);
            log.error(e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (fileIn != null) fileIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object arg2, BindException arg3) throws Exception {
		
		String tempDir = System.getProperty("java.io.tmpdir");
		String strAeReportId = request.getParameter("aeReport");
		String strReportId = request.getParameter("reportId");
		String format = request.getParameter("format");
        if (format == null) format = "xml";
		
   		try {
               Integer aeReportId = Integer.parseInt(strAeReportId);
               Integer reportId = Integer.parseInt(strReportId);

               ExpeditedAdverseEventReport aeReport = aeReportDao.getById(aeReportId);
               Report report = reportDao.getById(reportId);

               //if report is completed xml should be obtained from saved data.
               String xml = null;

               // This method was moved the AdverseEventReportSerializer.serialize()
               // report.getReportDefinition().buildMandatoryFieldsForXML(Mandatory.MANDATORY, Mandatory.OPTIONAL);
               
               if (report.getLastVersion().getReportStatus().equals(ReportStatus.COMPLETED) || report.getLastVersion().getReportStatus().equals(ReportStatus.AMENDED)) {
                   ReportContent reportContent = null;
                   // if the report is submitted and assigned with a ticket number , we need to populate that ticket number in XML , so generate new XML
                   if (report.getAssignedIdentifer() == null) {
                       //obtain the saved xml report
                       reportContent = reportDao.getReportContent(report);
                   }
                   if (reportContent == null) {
                       xml = adeersReportGenerator.generateCaaersXml(aeReport, report);
                   } else {
                       xml = new String(reportContent.getContent());
                   }
               } else {
                   //obtain newly generated caaers xml
                   xml = adeersReportGenerator.generateCaaersXml(aeReport, report);
               }
    			
    			int reportVersionId = report.getLastVersion().getId();
    			if (format.equals("pdf")) {
	    			String pdfOutFile = "expeditedAdverseEventReport-"+reportVersionId+".pdf";
	    	        // generate report and send ...
	    			//AdeersReportGenerator gen = new AdeersReportGenerator();
	    			adeersReportGenerator.generatePdf(xml,tempDir+File.separator+pdfOutFile);
	    			generateOutput(pdfOutFile,response,aeReportId);
    			} else if (format.equals("medwatchpdf")) {
	    			String pdfOutFile = "MedWatchReport-"+reportVersionId+".pdf";
	    			adeersReportGenerator.generateMedwatchPdf(xml,tempDir+File.separator+pdfOutFile);
	    			generateOutput(pdfOutFile,response,aeReportId);
    			} else if (format.equals("dcp")) {
    				String pdfOutFile = "dcp-"+reportVersionId+".pdf";
    				adeersReportGenerator.generateDcpSaeForm(xml, tempDir+File.separator+pdfOutFile);
    				generateOutput(pdfOutFile,response,aeReportId);
    			} else if (format.equals("cioms")) {
    				String pdfOutFile = "cioms-"+reportVersionId+".pdf";
    				adeersReportGenerator.generateCIOMS(xml, tempDir+File.separator+pdfOutFile);
    				generateOutput(pdfOutFile,response,aeReportId);
    			} else if (format.equals("ciomssae")) {
    				String pdfOutFile = "ciomssae-"+reportVersionId + ".pdf";
    				adeersReportGenerator.generateCIOMSTypeForm(xml, tempDir+File.separator+pdfOutFile);
    				
    				generateOutput(pdfOutFile,response,aeReportId);
    			} else if (format.equals("customPDF")) {

                    String xmlOutFile = "customPDF-" + reportVersionId + ".xml";
                    BufferedWriter outw = new BufferedWriter(new FileWriter(tempDir + File.separator + xmlOutFile));
                    outw.write(xml);
                    outw.close();

                    String customPDFFile = "customPDF-" + reportVersionId + ".pdf";
                    adeersReportGenerator.generateCustomPDF(xml, tempDir + File.separator + customPDFFile);
                    generateOutput(customPDFFile, response, aeReportId);
                    
                } else  {
	    			String xmlOutFile = "expeditedAdverseEventReport-" + reportVersionId + ".xml";
	    			BufferedWriter outw = new BufferedWriter(new FileWriter(tempDir+File.separator+xmlOutFile));
	    			outw.write(xml);
	    			outw.close();
	    			generateOutput(xmlOutFile,response,aeReportId);  				
    			}

           } catch (IOException e) {
               log.error(e.getMessage(), e);
               e.printStackTrace();
           }

        return null;
	}

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public ExpeditedAdverseEventReportDao getAeReportDao() {
		return aeReportDao;
	}

	public void setAeReportDao(ExpeditedAdverseEventReportDao aeReportDao) {
		this.aeReportDao = aeReportDao;
	}


	public AdeersReportGenerator getAdeersReportGenerator() {
		return adeersReportGenerator;
	}

	public void setAdeersReportGenerator(AdeersReportGenerator adeersReportGenerator) {
		this.adeersReportGenerator = adeersReportGenerator;
	}
   
}
