package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import gov.nih.nci.cabig.ctms.domain.DomainObjectTools;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OrderBy;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Krikor Krumlian
 */
@Entity
@Table(name = "participant_assignments")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_participant_assignments_id")})
@Where(clause = "load_status > 0")
public class StudyParticipantAssignment extends AbstractMutableDomainObject {

    private Participant participant;
    private StudySite studySite;

    private Date dateOfEnrollment;


    //private List<RoutineAdverseEventReport> aeRoutineReports;

    private List<LabLoad> labLoads;

    private Integer loadStatus = LoadStatus.COMPLETE.getCode();

    private String studySubjectIdentifier;

    private Date startDateOfFirstCourse;

    private List<AdverseEventReportingPeriod> reportingPeriods;

    private List<StudyParticipantPreExistingCondition> preExistingConditions;
    private List<StudyParticipantConcomitantMedication> concomitantMedications;
    private List<StudyParticipantPriorTherapy> priorTherapies;
    private StudyParticipantDiseaseHistory diseaseHistory;
    private String baselinePerformance;

    public StudyParticipantAssignment(Participant participant, StudySite studySite) {
        this.participant = participant;
        this.studySite = studySite;
        this.dateOfEnrollment = new Date();
    }

    public StudyParticipantAssignment() {
    }

    // //// LOGIC


    public void addRoutineReport(RoutineAdverseEventReport routineReport) {
        routineReport.setAssignment(this);
        getAeRoutineReports().add(routineReport);
    }

    public void addReportingPeriod(AdverseEventReportingPeriod reportingPeriod) {
        if (reportingPeriods == null) reportingPeriods = new ArrayList<AdverseEventReportingPeriod>();
        if (reportingPeriod != null) {
            reportingPeriod.setAssignment(this);
            reportingPeriods.add(reportingPeriod);
        }
    }

    public void addPreExistingCondition(StudyParticipantPreExistingCondition preExistingCondition) {
        if (preExistingConditions == null)
            preExistingConditions = new ArrayList<StudyParticipantPreExistingCondition>();
        if (preExistingCondition != null) {
            preExistingCondition.setAssignment(this);
            preExistingConditions.add(preExistingCondition);
        }
    }

    public void addConcomitantMedication(StudyParticipantConcomitantMedication concomitantMedication) {
        if (concomitantMedications == null)
            concomitantMedications = new ArrayList<StudyParticipantConcomitantMedication>();
        if (concomitantMedication != null) {
            concomitantMedication.setAssignment(this);
            concomitantMedications.add(concomitantMedication);
        }
    }

    public void addPriorTherapy(StudyParticipantPriorTherapy priorTherapy) {
        if (priorTherapies == null) priorTherapies = new ArrayList<StudyParticipantPriorTherapy>();
        if (priorTherapy != null) {
            priorTherapy.setAssignment(this);
            priorTherapies.add(priorTherapy);
        }
    }

    // //// BEAN PROPERTIES

    public void setStudySite(StudySite studySite) {
        this.studySite = studySite;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_site_id")
    @Cascade({CascadeType.LOCK})
    public StudySite getStudySite() {
        return studySite;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    @Cascade({CascadeType.LOCK})
    public Participant getParticipant() {
        return participant;
    }

    public void setDateOfEnrollment(Date dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    @Column(name = "date_of_enrollment")
    public Date getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    @Transient
    public List<ExpeditedAdverseEventReport> getAeReports() {
        ArrayList<ExpeditedAdverseEventReport> aeReports = new ArrayList<ExpeditedAdverseEventReport>();
        if (reportingPeriods != null) {
            for (AdverseEventReportingPeriod reportingPeriod : reportingPeriods) {
                for (ExpeditedAdverseEventReport aeReport : reportingPeriod.getAeReports()) {
                    aeReports.add(aeReport);
                }
            }
        }
        return aeReports;
    }

    @Transient
    public List<RoutineAdverseEventReport> getAeRoutineReports() {
//        if (aeRoutineReports == null) aeRoutineReports = new ArrayList<RoutineAdverseEventReport>();
        return new ArrayList<RoutineAdverseEventReport>();
    }

    public void setAeRoutineReports(List<RoutineAdverseEventReport> aeRoutineReports) {
//        this.aeRoutineReports = aeRoutineReports;
    }

    @OneToMany(mappedBy = "assignment")
    @OrderBy(clause = "start_date desc")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<AdverseEventReportingPeriod> getReportingPeriods() {
        return reportingPeriods;
    }

    public void setReportingPeriods(List<AdverseEventReportingPeriod> reportingPeriods) {
        this.reportingPeriods = reportingPeriods;
    }

    @OneToMany(mappedBy = "assignment")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<StudyParticipantPreExistingCondition> getPreExistingConditions() {
        return preExistingConditions;
    }

    public void setPreExistingConditions(List<StudyParticipantPreExistingCondition> preExistingConditions) {
        this.preExistingConditions = preExistingConditions;
    }

    @OneToMany(mappedBy = "assignment")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<StudyParticipantConcomitantMedication> getConcomitantMedications() {
        return concomitantMedications;
    }

    public void setConcomitantMedications(List<StudyParticipantConcomitantMedication> concomitantMedications) {
        this.concomitantMedications = concomitantMedications;
    }

    @OneToMany(mappedBy = "assignment")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<StudyParticipantPriorTherapy> getPriorTherapies() {
        return priorTherapies;
    }

    public void setPriorTherapies(List<StudyParticipantPriorTherapy> priorTherapies) {
        this.priorTherapies = priorTherapies;
    }

    @OneToOne(mappedBy = "assignment")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public StudyParticipantDiseaseHistory getDiseaseHistory() {
        return diseaseHistory;
    }

    public void setDiseaseHistory(StudyParticipantDiseaseHistory diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
    }

    @OneToMany(mappedBy = "assignment")
    @OrderBy(clause = "lab_date desc")
    public List<LabLoad> getLabLoads() {

        if (labLoads == null) labLoads = new ArrayList<LabLoad>();

        return labLoads;

    }

    public void setLabLoads(List<LabLoad> labLoads) {

        this.labLoads = labLoads;

    }

    public Integer getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Integer loadStatus) {
        this.loadStatus = loadStatus;
    }

    @Column(name = "first_course_date")
    public Date getStartDateOfFirstCourse() {
        return startDateOfFirstCourse;
    }

    public void setStartDateOfFirstCourse(Date startDateOfFirstCourse) {
        this.startDateOfFirstCourse = startDateOfFirstCourse;
    }

    public String getBaselinePerformance() {
        return baselinePerformance;
    }

    public void setBaselinePerformance(String baselinePerformance) {
        this.baselinePerformance = baselinePerformance;
    }

    // //// OBJECT METHODS

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StudyParticipantAssignment that = (StudyParticipantAssignment) o;

        if (dateOfEnrollment != null ? !dateOfEnrollment.equals(that.dateOfEnrollment)
                : that.dateOfEnrollment != null) return false;
        if (studySite != null ? !studySite.equals(that.studySite) : that.studySite != null) return false;
        // Participant#equals calls this method, so we can't use it here
        if (!DomainObjectTools.equalById(participant, that.participant)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (studySite != null ? studySite.hashCode() : 0);
        result = 29 * result + (participant != null ? participant.hashCode() : 0);
        result = 29 * result + (dateOfEnrollment != null ? dateOfEnrollment.hashCode() : 0);
        return result;
    }

    public String getStudySubjectIdentifier() {
        return studySubjectIdentifier;
    }

    public void setStudySubjectIdentifier(final String studySubjectIdentifier) {
        this.studySubjectIdentifier = studySubjectIdentifier;
    }


    public void syncrhonizePriorTherapies(final List<SAEReportPriorTherapy> saeReportPriorTherapies) {

        for (SAEReportPriorTherapy saeReportPriorTherapy : saeReportPriorTherapies) {
            if (saeReportPriorTherapy.getId() == null) {
                StudyParticipantPriorTherapy priorTherapy = StudyParticipantPriorTherapy.createAssignmentPriorTherapy(saeReportPriorTherapy);
                addPriorTherapy(priorTherapy);
            }
        }

    }

    public void syncrhonizePreExistingCondition(final List<SAEReportPreExistingCondition> saeReportPreExistingConditions) {

        for (SAEReportPreExistingCondition saeReportPreExistingCondition : saeReportPreExistingConditions) {
            if (saeReportPreExistingCondition.getId() == null) {
                StudyParticipantPreExistingCondition studyParticipantPreExistingCondition = StudyParticipantPreExistingCondition.createAssignmentPreExistingCondition(
                        saeReportPreExistingCondition);
                addPreExistingCondition(studyParticipantPreExistingCondition);
            }
        }

    }

    public void syncrhonizeConcomitantMedication(final List<ConcomitantMedication> saeReportConcomitantMedications) {

        for (ConcomitantMedication saeReportConcomitantMedication : saeReportConcomitantMedications) {
            if (saeReportConcomitantMedication.getId() == null) {
                StudyParticipantConcomitantMedication studyParticipantConcomitantMedication = StudyParticipantConcomitantMedication.createAssignmentConcomitantMedication(
                        saeReportConcomitantMedication);
                addConcomitantMedication(studyParticipantConcomitantMedication);
            }
        }

    }

}
