/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.migrator;

import com.aparzev.lang.StringUtils;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.RecommendedActions;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dirk Walter
 */

public class EvaluateAndInitiateReportConverter {
	private ReportDefinitionDao reportDefinitionDao;
	
	private ExpeditedAdverseEventReportConverterUtility utility = new ExpeditedAdverseEventReportConverterUtility();
	
	public void setStudyDao(StudyDao studyDao) {
		utility.setStudyDao(studyDao);
	}

	public ExpeditedAdverseEventReport convert(EvaluateAndInitiateInputMessage evaluateInputMessage, AdverseEventReportingPeriod repPeriod, SaveAndEvaluateAEsOutputMessage response) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ExpeditedAdverseEventReport aeSrcReport = new ExpeditedAdverseEventReport();
		for(AdverseEvent adverseEvent: repPeriod.getAdverseEvents()) {
			if(isTrue(adverseEvent.getRequiresReporting())  || (!isTrue(adverseEvent.isRetired()) && isTrue(adverseEvent.getReported()))) {
				aeSrcReport.addAdverseEventUnidirectional(adverseEvent);
			}
		}
		aeSrcReport.setExternalId(evaluateInputMessage.getReportId());
		aeSrcReport.setReporter(utility.convertReporter(evaluateInputMessage.getReporter()));
		aeSrcReport.setPhysician(utility.convertPhysician(evaluateInputMessage.getPhysician()));
		aeSrcReport.setCreatedAt(now);
		aeSrcReport.setReportingPeriod(repPeriod);
		List<Report> reports = new ArrayList<Report>();
        aeSrcReport.setReports(reports);
        if(CollectionUtils.isNotEmpty(response.getRecommendedActions())) {
            for(RecommendedActions action : response.getRecommendedActions()) {
                Report report = new Report();
                report.setReportDefinition(reportDefinitionDao.getByName(action.getReport()));
                if(BooleanUtils.isTrue(evaluateInputMessage.isWithdrawReport()) || StringUtils.equalsIgnoreCase("Withdraw", action.getAction())) {
                    report.setWithdrawnOn(now);
                }

                report.setCaseNumber(evaluateInputMessage.getReportId());
                reports.add(report);
            }
        }

		return aeSrcReport;
	}
	
	private boolean isTrue(Boolean bool) {
		if(bool != null) {
			return bool.booleanValue();
		}
		return false;
	}

	public ReportDefinitionDao getReportDefinitionDao() {
		return reportDefinitionDao;
	}

	public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
		this.reportDefinitionDao = reportDefinitionDao;
	}
	
}