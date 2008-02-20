package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * This class represents the MeddraStudyDisease domain object associated with the Adverse event report.
 * @author Krikor Krumlian
 */
@Entity
@DiscriminatorValue("meddra")
public class MeddraStudyDisease extends AbstractStudyDisease<LowLevelTerm> {
    
	private String meddraCode;
	
	public String getMeddraCode() {
		return meddraCode;
	}

	public void setMeddraCode(String meddraCode) {
		this.meddraCode = meddraCode;
	}

	@ManyToOne
    @JoinColumn(name = "term_id")
    @Override
    public LowLevelTerm getTerm() {
        return super.getTerm();
    }
	
	@Override
	@Transient
	public String getTermName() {
		return getTerm().getFullName();
	}
}
