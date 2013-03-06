/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.attribution;

import gov.nih.nci.cabig.caaers.domain.SurgeryIntervention;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Rhett Sutphin
 */
@Entity
@DiscriminatorValue("SI")
public class SurgeryAttribution extends AdverseEventAttribution<SurgeryIntervention> {
    @ManyToOne
    @JoinColumn(name = "cause_id")
    @Override
    public SurgeryIntervention getCause() {
        return super.getCause();
    }

    @Override
    public SurgeryAttribution copy() {
        return super.copy();
    }
}
