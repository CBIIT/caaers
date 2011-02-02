package gov.nih.nci.cabig.caaers.domain.attribution;

import gov.nih.nci.cabig.caaers.domain.SurgeryIntervention;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

 
/**
 * The Class SurgeryAttribution.
 *
 * @author Rhett Sutphin
 */
@Entity
@DiscriminatorValue("SI")
public class SurgeryAttribution extends AdverseEventAttribution<SurgeryIntervention> {
    
    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.attribution.AdverseEventAttribution#getCause()
     */
    @ManyToOne
    @JoinColumn(name = "cause_id")
    @Override
    public SurgeryIntervention getCause() {
        return super.getCause();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.attribution.AdverseEventAttribution#copy()
     */
    @Override
    public SurgeryAttribution copy() {
        return super.copy();
    }
}
