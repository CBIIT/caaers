package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.report.ReportVersion;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

/**
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "ae_report_people")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ABSTRACT_BASE")
// should be ignored
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {
        @Parameter(name = "sequence", value = "seq_ae_report_people_id")
})
public abstract class ReportPerson extends PersonContact {

    private ExpeditedAdverseEventReport expeditedReport;

    private ReportVersion report;

    // //// BOUND PROPERTIES

    @OneToOne
    @JoinColumn(name = "report_version_id")
    public ReportVersion getReport() {
        return report;
    }

    public void setReport(ReportVersion report) {
        this.report = report;
    }

    // ////BOUND PROPERTIES

    @OneToOne
    @JoinColumn(name = "report_id")
    public ExpeditedAdverseEventReport getExpeditedReport() {
        return expeditedReport;
    }

    public void setExpeditedReport(ExpeditedAdverseEventReport expeditedReport) {
        this.expeditedReport = expeditedReport;
    }

    public ReportPerson copy() {
        ReportPerson reportPerson = (ReportPerson) BeanUtils.instantiateClass(getClass());
        BeanUtils.copyProperties(this, reportPerson, new String[]{"id", "gridId",
                "version", "expeditedReport", "primaryIdentifierValue"});

        return reportPerson;

    }

}
