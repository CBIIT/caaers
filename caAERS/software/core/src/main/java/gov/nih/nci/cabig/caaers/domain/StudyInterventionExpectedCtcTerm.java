/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


/**
 * @author Ion C. Olaru
 */
@Entity
@DiscriminatorValue(value = "ctep")
public class StudyInterventionExpectedCtcTerm extends AbstractStudyInterventionExpectedAE<CtcTerm> {

    public StudyInterventionExpectedCtcTerm(
    		TreatmentAssignmentAgent treatmentAssignmentAgent,
			AgentSpecificTerm agentSpecificTerm, boolean shouldHonor) {
		super(treatmentAssignmentAgent, agentSpecificTerm, shouldHonor);
		// TODO Auto-generated constructor stub
	}
    
	public StudyInterventionExpectedCtcTerm() {
		super();
		// TODO Auto-generated constructor stub
	}

	private LowLevelTerm otherMeddraTerm;
    
    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractExpectedAE#getTerm()
     */
    @ManyToOne
    @JoinColumn(name = "term_id")
    @Cascade(value = {CascadeType.LOCK, CascadeType.EVICT})
    @Override
    public CtcTerm getTerm() {
        return super.getTerm();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractExpectedAE#getFullName()
     */
    @Override
    @Transient
    public String getFullName() {
    	if(getTerm() == null) return "";
    	return getTerm().getFullName();
    }

    /**
     * Gets the ctc term.
     *
     * @return CtcTerm
     */
    @Transient
    public CtcTerm getCtcTerm() {
        return super.getTerm();
    }

    /**
     * Sets the ctc term.
     *
     * @param ctcTerm The CTC term
     */
    @Transient
    public void setCtcTerm(CtcTerm ctcTerm) {
        super.setTerm(ctcTerm);
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractExpectedAE#isMedDRA()
     */
    @Override
    @Transient
    public boolean isMedDRA() {
    	return false;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractExpectedAE#isOtherRequired()
     */
    @Transient
    public boolean isOtherRequired() {
        if (getTerm() == null) return false;
        return getTerm().isOtherRequired();
    }

    /**
     * Gets the other meddra term.
     * @return LowLevelTerm
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "low_level_term_id", nullable = true)
    public LowLevelTerm getOtherMeddraTerm() {
        return otherMeddraTerm;
    }

    /**
     * Sets the other meddra term.
     * @param otherMeddraTerm the new other meddra term
     */
    public void setOtherMeddraTerm(LowLevelTerm otherMeddraTerm) {
        this.otherMeddraTerm = otherMeddraTerm;
    }
    
}
