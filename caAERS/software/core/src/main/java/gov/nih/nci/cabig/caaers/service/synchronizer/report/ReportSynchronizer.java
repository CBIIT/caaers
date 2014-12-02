/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.migrator.Migrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ramakrishna Gundala
 */
public class ReportSynchronizer implements Migrator<ExpeditedAdverseEventReport> {
	private static Log logger = LogFactory.getLog(ReportSynchronizer.class);
    public void migrate(ExpeditedAdverseEventReport xmlAeReport, ExpeditedAdverseEventReport dbAeReport, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
    	
        List<Report> newlyFoundReports = new ArrayList<Report>();

        if (xmlAeReport.getReports() == null || xmlAeReport.getReports().isEmpty()) {
            outcome.addWarning("RS-WR-1", "The input for Reports is null, so not performing any operation.");
            return;
        }

        //create an index of Reports
        HashMap<Integer, Report> reportsIndex = new HashMap<Integer, Report>();
        for(Report report : dbAeReport.getReports()){ reportsIndex.put(report.getId(), report);}

        //try to find the Report in source , if found synchronize it.
        for(Report report : xmlAeReport.getReports()){
        	if(!StringUtils.isBlank(report.getCaseNumber())){
        		Report reportFound = dbAeReport.findReportByCaseNumber(report.getCaseNumber());
	            if(reportFound != null) {
	                synchronizeReport(report, reportFound);
	            }else {
	            	logger.error("The report with given ID " + report.getCaseNumber() + " is not found in the system. So not performing any operation");
	                outcome.addError("RS-ERR-1", "The report ID: " +  report.getCaseNumber() + " in the input is not found in the system, so not performing any operation ");
	            }
        	} else {
        		newlyFoundReports.add(report);
        	}
        }

        //add the new Report that are present in source.
        for(Report report : newlyFoundReports){
            dbAeReport.addReport(report);
        }
    }

    /**
     * Copy the values from the XML input to db Report.
     * @param xmlReport
     * @param dbReport
     */
    public void synchronizeReport(Report xmlReport, Report dbReport){
        Map<String, String> xmlReportMetaData =  xmlReport.getMetaDataAsMap();

        //copy correlationId
        String[] correlationIds = xmlReport.getCorrelationIds();
        if(correlationIds != null) {
            for(String id : correlationIds) {
                dbReport.addToCorrelationId(id);
            }
        }
        //copy everything else
        for(Map.Entry<String, String> e : xmlReportMetaData.entrySet()) {
           if(e.getKey().equals("correlationId")) continue;
           dbReport.addToMetaData(e.getKey(), e.getValue());
        }



    	if(xmlReport.getLastVersion().getCcEmails() != null) {
    		dbReport.getLastVersion().setCcEmails(xmlReport.getLastVersion().getCcEmails());
    	}
    }
}
