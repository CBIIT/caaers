/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.rule.notification;

import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.integration.schema.reportdefinition.ReportDefinitions;
import gov.nih.nci.cabig.caaers.service.migrator.ReportDefinitionConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImportReportDefinitionController extends SimpleFormController{
	
	protected ReportDefinitionDao reportDefinitionDao;
	protected ReportDefinitionConverter reportDefinitionConverter;
	
	
	public ImportReportDefinitionController() {
        setCommandClass(ImportReportDefinitionCommand.class);
        setBindOnNewForm(true);
        setFormView("rule/notification/import");
        setSuccessView("rule/notification/import");
    }
	
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        ImportReportDefinitionCommand command = new ImportReportDefinitionCommand();
        command.setFolder(request.getParameter("importDir"));
        return command;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
    	
    	ImportReportDefinitionCommand importReportDefinitionCommand = (ImportReportDefinitionCommand) command;
        
    	if (importReportDefinitionCommand.getRuleSetFile1().isEmpty()) {
        	importReportDefinitionCommand.setMessage("Please choose a file");
        	importReportDefinitionCommand.setUpdated(true);
        	ModelAndView modelAndView = new ModelAndView(getFormView(), errors.getModel());
            return modelAndView;

        }
    	
    	ReportDefinitions reportDefinitions = null;
    	JAXBContext jaxbContext = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.integration.schema.reportdefinition");
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    	reportDefinitions = (ReportDefinitions)unmarshaller.unmarshal(importReportDefinitionCommand.getRuleSetFile1().getInputStream());
    	String reportDefName = reportDefinitions.getReportDefinition().get(0).getName();
    	
    	
    	if (reportDefinitionDao.getByName(reportDefName) != null){
    		StringBuffer message = new StringBuffer(reportDefName);
    		message.append("\n");
    		message.append("Exists in caAERS");
//    		importReportDefinitionCommand.setMessage(message.toString());
    		importReportDefinitionCommand.setErrorMessage(message.toString());
    		importReportDefinitionCommand.setUpdated(true);
        	ModelAndView modelAndView = new ModelAndView(getFormView(), errors.getModel());
            return modelAndView;
        }
    	
    	ReportDefinition reportDefinition = reportDefinitionConverter.dtoToDomain(reportDefinitions.getReportDefinition().get(0));
    	reportDefinitionDao.save(reportDefinition);
    	StringBuffer messageSb = new StringBuffer(reportDefinition.getName());
    	messageSb.append("\n");
    	messageSb.append("Imported Successfully !");
    	importReportDefinitionCommand.setMessage(messageSb.toString());
    	importReportDefinitionCommand.setUpdated(true);
    	ModelAndView modelAndView = new ModelAndView(getSuccessView(), errors.getModel());
    	return modelAndView;

    }

	public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
		this.reportDefinitionDao = reportDefinitionDao;
	}

	public void setReportDefinitionConverter(ReportDefinitionConverter reportDefinitionConverter) {
		this.reportDefinitionConverter = reportDefinitionConverter;
	}
    
}
