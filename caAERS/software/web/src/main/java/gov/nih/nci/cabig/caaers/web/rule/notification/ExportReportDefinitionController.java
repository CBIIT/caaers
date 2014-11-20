/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.rule.notification;

import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.integration.schema.reportdefinition.ReportDefinitionType;
import gov.nih.nci.cabig.caaers.integration.schema.reportdefinition.ReportDefinitions;
import gov.nih.nci.cabig.caaers.integration.schema.reportdefinition.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.service.migrator.ReportDefinitionConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.semanticbits.rules.utils.RuleUtil;


public class ExportReportDefinitionController extends AbstractCommandController{
	
	protected ReportDefinitionDao reportDefinitionDao;
	protected ReportDefinitionConverter reportDefinitionConverter;
	
	public ExportReportDefinitionController(){
		setCommandClass(ReportDefinitionCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object arg2, BindException arg3)
			throws Exception {
		
		// fetch report definition Id
        Integer rpDefId = Integer.valueOf(request.getParameter("repDefId"));
        // feth the ReportDefinition by Id
        ReportDefinition rpDef = reportDefinitionDao.getById(rpDefId);
        // initialize all the lazy collections in rpDef
        reportDefinitionDao.initialize(rpDef);
		
        //Convert the Domain Object to Data Transfer Object.
        ReportDefinitions reportDefinitions = reportDefinitionConverter.domainToDto(rpDef);
        for(ReportDefinitionType rep : reportDefinitions.getReportDefinition()) {
        	for(ReportDeliveryDefinition dev : rep.getDeliveryDefinition()) {
        		if(dev.getPassword() != null && !dev.getPassword().isEmpty()) {
        			dev.setUserName("Username");
        			dev.setPassword("Password");
        		}
        	}
        }
        
        //Marshall the Data Transfer Object according to ReportDefinition.xsd schema,
        //and download it to the client machine.
		try {
			String tempDir = System.getProperty("java.io.tmpdir");
			String fileName = reportDefinitions.getReportDefinition().get(0).getName();
			fileName = RuleUtil.getStringWithoutSpaces(fileName);
			StringWriter sw = new StringWriter();
			JAXBContext jaxbContext = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.integration.schema.reportdefinition");
	    	Marshaller marshaller = jaxbContext.createMarshaller();
	    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true) ;
	    	marshaller.marshal(reportDefinitions, sw);
	        BufferedWriter out = new BufferedWriter(new FileWriter(tempDir+fileName+".xml"));
	        out.write(sw.toString());
	        out.close();
	        
	        response.setContentType("application/xml");
	        response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xml");
	        response.setHeader("Content-length", String.valueOf(sw.toString().length()));
	        response.setHeader("Pragma", "private");
	        response.setHeader("Cache-control", "private, must-revalidate");
	
	        OutputStream outputStream = response.getOutputStream();
	        File file = new File(tempDir+fileName+".xml");
	        FileInputStream fileIn = new FileInputStream(file);
	        byte[] buffer = new byte[2048];
            int bytesRead = fileIn.read(buffer);
            while (bytesRead >= 0) {
                if (bytesRead > 0) outputStream.write(buffer, 0, bytesRead);
                bytesRead = fileIn.read(buffer);
            }
            outputStream.flush();
            outputStream.close();
            fileIn.close();
            //FileUtils.deleteQuietly(file);
        
		}catch (Exception e) {
		
		}
		return null;
	}

	public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
		this.reportDefinitionDao = reportDefinitionDao;
	}

	public void setReportDefinitionConverter(
			ReportDefinitionConverter reportDefinitionConverter) {
		this.reportDefinitionConverter = reportDefinitionConverter;
	}
	
	
}
