package gov.nih.nci.cabig.caaers.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * This class represents the SAEReportPreExistingCondition domain object associated with the Adverse
 * event report.
 *
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "ae_pre_existing_conds")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_ae_pre_existing_conds")})
public class SAEReportPreExistingCondition extends AbstractExpeditedReportCollectionElementChild {
    private PreExistingCondition preExistingCondition;

    private String other;

    // //// LOGIC

    @Transient
    public String getName() {
        if (getPreExistingCondition() != null) {
            return getPreExistingCondition().getText();
        } else if (getOther() != null) {
            return "Other: " + getOther();
        } else {
            return null;
        }
    }

    // //// BOUND PROPERTIES

    @ManyToOne
    public PreExistingCondition getPreExistingCondition() {
        return preExistingCondition;
    }

    public void setPreExistingCondition(PreExistingCondition preExistingCondition) {
        this.preExistingCondition = preExistingCondition;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


    ///OBJECT METHODS
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((other == null) ? 0 : other.hashCode());
        result = prime
                * result
                + ((preExistingCondition == null) ? 0 : preExistingCondition
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SAEReportPreExistingCondition other = (SAEReportPreExistingCondition) obj;
        if (this.other == null) {
            if (other.other != null)
                return false;
        } else if (!this.other.equals(other.other))
            return false;
        if (preExistingCondition == null) {
            if (other.preExistingCondition != null)
                return false;
        } else if (!preExistingCondition.equals(other.preExistingCondition))
            return false;
        return true;
    }

    public static SAEReportPreExistingCondition createSAEReportPreExistingCondition(StudyParticipantPreExistingCondition studyParticipantPreExistingCondition) {

        if (studyParticipantPreExistingCondition != null) {
            SAEReportPreExistingCondition saeReportPreExistingCondition = new SAEReportPreExistingCondition();
            BeanUtils.copyProperties(studyParticipantPreExistingCondition, saeReportPreExistingCondition, new String[]{"id", "gridId",
                    "version", "report"});


            return saeReportPreExistingCondition;

        }
        return null;

    }


}
