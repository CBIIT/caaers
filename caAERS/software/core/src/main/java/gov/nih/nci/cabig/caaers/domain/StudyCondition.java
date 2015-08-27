/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

 
/**
 * The Class StudyCondition.
 *
 * @author Ion C. Olaru
 */

@Entity
@DiscriminatorValue("dcp")
public class StudyCondition extends AbstractStudyDisease<Condition> {

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractStudyDisease#getTerm()
     */
    @ManyToOne(optional=false)
    @JoinColumn(name = "term_id", nullable = false)
    @Override
    @Cascade(value = {CascadeType.SAVE_UPDATE, CascadeType.LOCK, CascadeType.EVICT})
    public Condition getTerm() {
        return super.getTerm();
    }
    
    public void setTerm(Condition term) {
    	super.setTerm(term);
    };

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.AbstractStudyDisease#getTermName()
     */
    @Override
    @Transient
    public String getTermName() {
        if(getTerm() == null) return null;
        return getTerm().getConditionName();
    }
    
    /**
     * Sets the term name.
     *
     * @param name the new term name
     */
    @Override
    @Transient
    public void setTermName(String name) {
    	super.setTermName(name);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if ( obj == null || !(obj instanceof StudyCondition)) return false;

        if (this.getId() != null && ((StudyCondition)obj).getId() != null) {
            return (this.getId().intValue() == ((StudyCondition)obj).getId().intValue()); 
        } else {
            return this.getTerm().getConditionName().equals(((StudyCondition)obj).getTerm().getConditionName());
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return getTermName();
    }
    
}
