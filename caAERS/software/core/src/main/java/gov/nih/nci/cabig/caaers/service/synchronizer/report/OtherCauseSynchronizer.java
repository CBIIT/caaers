package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.OtherCause;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class OtherCauseSynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = Arrays.asList("e2b");
    @Override
    public List<String> contexts() {
        return context;
    }
    public void migrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        List<OtherCause> newCauses = new ArrayList<OtherCause>();
        List<OtherCause> existingCauses = new ArrayList<OtherCause>();
        if(dest.getOtherCauses() != null) existingCauses.addAll(dest.getOtherCauses());
        if(src.getOtherCauses() != null){
            for(OtherCause oc : src.getOtherCauses()){
                int i = existingCauses.indexOf(oc);
                OtherCause found = i >= 0 ? existingCauses.get(i) : null;
                if(found != null){
                    existingCauses.remove(i);
                }else {
                    newCauses.add(oc);
                }
            }
        }
        for(OtherCause oc : existingCauses){
            dest.cascaeDeleteToAttributions(oc);
            dest.getOtherCauses().remove(oc);
        }
        for(OtherCause oc : newCauses) dest.addOtherCause(oc);

    }
}
