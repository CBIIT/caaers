package gov.nih.nci.cabig.caaers.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

/**
 * @author Kulasekaran
 */
@Entity
@Table (name="study_investigators")
@GenericGenerator(name="id-generator", strategy = "native",
    parameters = {
        @Parameter(name="sequence", value="seq_study_investigators_id")
    }
)
public class StudyInvestigator extends AbstractMutableDomainObject {
	
	private String signatureText;
	private String roleCode;
	private String statusCode;
	private SiteInvestigator siteInvestigator;
	private StudySite studySite;
	
	@Column(name = "signature_text")
	public String getSignatureText() {
		return signatureText;
	}
	
	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}
	
	@ManyToOne
    @JoinColumn(name = "site_investigators_id")
	public SiteInvestigator getSiteInvestigator() {
		return siteInvestigator;
	}
	
	public void setSiteInvestigator(SiteInvestigator siteInvestigator) {
		this.siteInvestigator = siteInvestigator;
	}
		
	@ManyToOne
    @JoinColumn(name = "study_sites_id")    	
	public StudySite getStudySite() {
		return studySite;
	}

	public void setStudySite(StudySite studySite) {
		this.studySite = studySite;
	}

	@Column(name = "role_code")
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	@Column(name = "status_code")
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
