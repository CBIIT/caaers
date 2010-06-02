package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Ion C. Olaru
 */
@Entity
@DiscriminatorValue("meddra")
public class ExpectedAEMeddraLowLevelTerm extends AbstractExpectedAE<LowLevelTerm> {

    @Transient
    public String getFullName() {
    	if(getTerm() == null) return "";
    	return getTerm().getFullName();
    }

    @ManyToOne
    @JoinColumn(name = "term_id")
    @Override
    public LowLevelTerm getTerm() {
        return super.getTerm();
    }

    @Transient
    public void setLowLevelTerm(LowLevelTerm lowlevelTerm) {
        super.setTerm(lowlevelTerm);
    }

    @Override
    public ExpectedAEMeddraLowLevelTerm copy() {
        return (ExpectedAEMeddraLowLevelTerm) super.copy();
    }

    @Override
    @Transient
    public boolean isMedDRA() {
    	return true;
    }

    @Override
    @Transient
    public boolean isOtherRequired() {
        return false;
    }

}