package gov.nih.nci.cabig.caaers.domain.report;

import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Submitter;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * Every report has a report version
 *
 * @author Krikor Krumlian
 *
 */
@Entity
@Table(name = "report_versions")
@GenericGenerator(name = "id-generator", strategy = "native",
    parameters = {
        @Parameter(name = "sequence", value = "seq_report_versions_id")
    }
)
public class ReportVersion extends AbstractMutableDomainObject implements Serializable {
	
	private Date createdOn;
	private Date dueOn;
	private Date submittedOn;
	private Date withdrawnOn;
	
	private Submitter submitter;
    private Boolean physicianSignoff;
    private String ccEmails;
    private String reportVersionId;
	private ReportStatus reportStatus;
	
	private Report report; 
	
	//////Logic
	

	@Column(name = "status_code")
	@Type(type = "reportStatus")
	public ReportStatus getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(ReportStatus reportStatus) {
		this.reportStatus = reportStatus;
	}
	

    // This is annotated this way so that the IndexColumn in the parent
    // will work with the bidirectional mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable=false, updatable=false, nullable=false)
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Temporal(value=TemporalType.TIMESTAMP)
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Temporal(value=TemporalType.TIMESTAMP)
	public Date getSubmittedOn() {
		return submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	public Date getWithdrawnOn() {
		return withdrawnOn;
	}

	public void setWithdrawnOn(Date withdrawnOn) {
		this.withdrawnOn = withdrawnOn;
	}

	public Date getDueOn() {
		return dueOn;
	}

	public void setDueOn(Date dueOn) {
		this.dueOn = dueOn;
	}
	
	@Transient
	public void addSubmitter(){
		if (submitter == null) setSubmitter(new Submitter());
	}

	// non-total cascade allows us to skip saving if the reporter hasn't been filled in yet
    @OneToOne(mappedBy = "report")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public Submitter getSubmitter() {
       return submitter;
    }

    public void setSubmitter(Submitter submitter) {
        this.submitter = submitter;
        if (submitter != null) submitter.setReport(this);
    }

    @Column(name="physician_signoff")
	public Boolean getPhysicianSignoff() {
		return physicianSignoff;
	}

	public void setPhysicianSignoff(Boolean physicianSignoff) {
		this.physicianSignoff = physicianSignoff;
	}

	@Column(name = "email")
	public String getCcEmails() {
		return ccEmails;
	}

	public void setCcEmails(String email) {
		this.ccEmails = email;
	}

	@Transient
	public String[] getEmailAsArray(){
		if (this.ccEmails == null) {
			return null;
		}
		String[] emails = this.ccEmails.split(",");
		return emails;
	}

	public String getReportVersionId() {
		if ((this.reportVersionId == null) || 
				(this.reportVersionId != null && this.reportVersionId.equals("NA")) ){
			setReportVersionId("NA");
		}
		return reportVersionId;
	}

	public void setReportVersionId(String reportVersionId) {
		String id;
		if ( getId() != null ){
			id = Integer.toString(getId() + 1000);
			if (this.getReport() != null && this.getReport().getReportDefinition() != null && this.getReport().getReportDefinition().getOrganization() !=null){
				id = id + this.getReport().getReportDefinition().getOrganization().getId();
				this.reportVersionId = id;
			}
		}else
		{
			this.reportVersionId = reportVersionId;
		}
	}
	
	
	
	
	
	
	

	
}

