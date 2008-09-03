package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportTree;
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;

/**
 * @author Rhett Sutphin
 */
public class SubmitExpeditedAdverseEventCommand extends EditExpeditedAdverseEventCommand {
    private StudyParticipantAssignmentDao assignmentDao;

    // Used in SubmitReport
    private String reportIndex;

    private String reportId;

    private String from;

    // //// LOGIC

    public SubmitExpeditedAdverseEventCommand(ExpeditedAdverseEventReportDao expeditedAeReportDao,
                    ReportDefinitionDao reportDefinitionDao,
                    StudyParticipantAssignmentDao assignmentDao,
                    AdverseEventReportingPeriodDao reportingPeriodDao,
                    ExpeditedReportTree expeditedReportTree) {
        super(expeditedAeReportDao, reportDefinitionDao, assignmentDao, reportingPeriodDao, expeditedReportTree);
    }

    @Override
    public void setAeReport(ExpeditedAdverseEventReport aeReport) {
        super.setAeReport(aeReport);
        getReportIndex();
        aeReport.getReports().get(((int) (Integer.parseInt(reportIndex)))).getLastVersion()
                        .addSubmitter();
    }

    public String getReportIndex() {
        for (int i = 0; i < getAeReport().getReports().size(); i++) {
            if (getAeReport().getReports().get(i).getId().toString().equals(reportId)) {
                this.reportIndex = String.valueOf(i);
            }
        }
        return reportIndex;
    }

    public void setReportIndex(String reportIndex) {
        this.reportIndex = reportIndex;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
