/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.meddra;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

 
/**
 * The Class LowLevelTerm.
 */
@Entity
@Table(name = "meddra_llt")
public class LowLevelTerm extends AbstractMeddraDomainObject {

    /** The preferred term. */
    private PreferredTerm preferredTerm;
    
    /**
     * Gets the preferred term.
     *
     * @return the preferred term
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "meddra_pt_id")
    @Cascade(value = { CascadeType.LOCK })
    public PreferredTerm getPreferredTerm(){
    	return preferredTerm;
    }
    
    /**
     * Sets the preferred term.
     *
     * @param preferredTerm the new preferred term
     */
    public void setPreferredTerm(PreferredTerm preferredTerm){
    	this.preferredTerm = preferredTerm;
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    @Transient
    public String getFullName() {
    	if(getMeddraTerm() == null) 
    		return getMeddraCode();
		return getMeddraTerm();
    }
    
    @Transient
    public String getTerm() {
    	if(getMeddraTerm() == null) 
    		return getMeddraCode();
		return getMeddraTerm();
    }
    
    
}
