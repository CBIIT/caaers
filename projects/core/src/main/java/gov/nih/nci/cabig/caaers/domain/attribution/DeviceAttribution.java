package gov.nih.nci.cabig.caaers.domain.attribution;

import gov.nih.nci.cabig.caaers.domain.MedicalDevice;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * @author Rhett Sutphin
 */
@Entity
@DiscriminatorValue("DV")
public class DeviceAttribution extends AdverseEventAttribution<MedicalDevice> {
    @ManyToOne
    @JoinColumn(name = "cause_id")
    @Override
    public MedicalDevice getCause() {
        return super.getCause();
    }
}
