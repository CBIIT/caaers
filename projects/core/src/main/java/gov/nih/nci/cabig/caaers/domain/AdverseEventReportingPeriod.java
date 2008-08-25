package gov.nih.nci.cabig.caaers.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportVersion;
import gov.nih.nci.cabig.ctms.collections.LazyListHelper;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

/**
 * This class represents the Reporting Period associated to StudyParticipant Associations
 *
 * @author Sameer Sawant
 */
@Entity
@Table(name = "ae_reporting_periods")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_ae_reporting_periods_id") })
public class AdverseEventReportingPeriod extends AbstractMutableDomainObject{
	private static final String BASELINE_REPORTING_TYPE = "Baseline";
	
	private String description;
	
	private Integer cycleNumber;
	
	//private ExpeditedAdverseEventReport expeditedAdverseEventReport;
	
	private TreatmentAssignment treatmentAssignment;
	
	private Epoch epoch;
	
	private Date startDate;
	
	private Date endDate;
	
	private StudyParticipantAssignment assignment;
	
	private List<AdverseEvent> adverseEvents;
	
	private List<ExpeditedAdverseEventReport> aeReports;
	
	private String name;
	
	private SimpleDateFormat formatter;
	
	private boolean baselineReportingType;
	
	// This holds the total number of reports within all the ExpeditedReport generated in this reporting period
	private int numberOfReports;
	
	// This gives the number of Aes in the reporting Period.
	private int numberOfAes;
	
	// This gives the Data Entry Status for ths reporing Period
	private String dataEntryStatus;
	
	// This gives the Report Status for the reporting Period
	private String reportStatus;
	
	public AdverseEventReportingPeriod() {
		formatter = new SimpleDateFormat("MM/dd/yy");
    }
	
	//LOGIC
	public void addAdverseEvent(AdverseEvent adverseEvent){
    	getAdverseEvents().add(adverseEvent);
    	adverseEvent.setReportingPeriod(this);
    }
	
	// BEAN PROPERTIES
    
    @Transient
    public Participant getParticipant() {
        return getAssignment() == null ? null : getAssignment().getParticipant();
    }

    @Transient
    public Study getStudy() {
        StudySite ss = getAssignment() == null ? null : getAssignment().getStudySite();
        return ss == null ? null : ss.getStudy();
    }
    
    @Transient
    public Map<String, String> getSummary() {
        Map<String, String> summary = new LinkedHashMap<String, String>();
        summary.put("Participant", getParticipantSummaryLine());
        summary.put("Study", getStudySummaryLine());
        summary.put("Adverse event count", Integer.toString(getAdverseEvents().size()));

        return summary;
    }
    
    @Transient
    public String getParticipantSummaryLine() {
        Participant participant = getParticipant();
        if (participant == null) return null;
        StringBuilder sb = new StringBuilder(participant.getFullName());
        appendPrimaryIdentifier(participant, sb);
        return sb.toString();
    }

    @Transient
    public String getStudySummaryLine() {
        Study study = getStudy();
        if (study == null) return null;
        StringBuilder sb = new StringBuilder(study.getShortTitle());
        appendPrimaryIdentifier(study, sb);
        return sb.toString();
    }
    
    private void appendPrimaryIdentifier(IdentifiableByAssignedIdentifers ided, StringBuilder sb) {
        if (ided.getPrimaryIdentifier() != null) {
            sb.append(" (").append(ided.getPrimaryIdentifier().getValue()).append(')');
        }
    }
   
    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany(mappedBy = "reportingPeriod")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<AdverseEvent> getAdverseEvents() {
    	if (adverseEvents == null) adverseEvents = new ArrayList<AdverseEvent>();
        return adverseEvents;
    }

    public void setAdverseEvents(final List<AdverseEvent> adverseEvents) {
        this.adverseEvents = adverseEvents;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(value={CascadeType.LOCK})
    public StudyParticipantAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(StudyParticipantAssignment assignment) {
        this.assignment = assignment;
    }
    
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_assignment_id")
    @Cascade(value = { CascadeType.LOCK })
    public TreatmentAssignment getTreatmentAssignment() {
        return treatmentAssignment;
    }

    public void setTreatmentAssignment(TreatmentAssignment treatmentAssignment) {
        this.treatmentAssignment = treatmentAssignment;
    }
    
    public String getDescription(){
    	return description;
    }
    
    public void setDescription(String description){
    	this.description = description;
    }

    public Integer getCycleNumber() {
		return cycleNumber;
	}
    
    public void setCycleNumber(Integer cycleNumber) {
		this.cycleNumber = cycleNumber;
	}
    
    @ManyToOne(fetch = FetchType.LAZY)
    public Epoch getEpoch(){
    	return epoch;
    }
    
    public void setEpoch(Epoch epoch){
    	this.epoch = epoch;
    }
    
    @OneToMany(mappedBy = "reportingPeriod")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public List<ExpeditedAdverseEventReport> getAeReports() {
		return aeReports;
	}
    
    public void setAeReports(List<ExpeditedAdverseEventReport> aeReports) {
		this.aeReports = aeReports;
	}
    
    public void addAeReport(ExpeditedAdverseEventReport aeReport){
    	if(aeReport == null) return;
    	if(this.aeReports == null) this.aeReports = new ArrayList<ExpeditedAdverseEventReport>();
    	aeReport.setReportingPeriod(this);
    	aeReports.add(aeReport);
    	
    }
    
    @Transient
    public String getName() {
		if(name == null || name.equals("")){
			name = formatter.format(startDate) + " - " + formatter.format(endDate);
			name.concat(", " + getEpoch().getName());
			if(cycleNumber != null)
				name.concat(", " + getCycleNumber());
 		}
		
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
    
    @Transient
    public boolean isBaselineReportingType(){
    	if(this.getEpoch() != null)
    		return getEpoch().getName().equals(BASELINE_REPORTING_TYPE);
    	return false;
    }
    
    @Transient
    public int getNumberOfReports(){
    	int count = 0;
    	for(ExpeditedAdverseEventReport report: this.getAeReports()){
    		count += report.getReports().size();
    	}
    	return count;
    }
    
    @Transient
    public int getNumberOfAes(){
    	int count = (this.getAdverseEvents() != null) ? this.getAdverseEvents().size() : 0;
    	return count;
    }
    
    @Transient
    public String getDataEntryStatus(){
    	return "In-process";
    }
    
    /**
     * Will tell the combined submission status of individual expedited reports
     * 
     * @return {@link ReportStatus}.COMPLETED -When all reports are submitted sucessfully or (withdrawn), {@link ReportStatus}.PENDING when any of the report is pending,inprocess or failed.
     */
    @Transient
    public ReportStatus getReportStatus(){
    	if(getAeReports().isEmpty()) return null;
    	
    	// If for any reports associated to all the Data Collection has status other than COMPLETED
    	// or WITHDRAWN then return a status "Report(s) Due" or else return a status "Report(s) Completed"
    	
    	for(ExpeditedAdverseEventReport aeReport: this.getAeReports()){
    		for(Report report: aeReport.getReports()){
    			ReportStatus status = report.getLastVersion().getReportStatus();
    			if(status == ReportStatus.PENDING   || status == ReportStatus.INPROCESS || status == ReportStatus.FAILED){
    				return ReportStatus.PENDING;
    			}
    		}	
    	}
    	
    	return ReportStatus.COMPLETED;
    }
}