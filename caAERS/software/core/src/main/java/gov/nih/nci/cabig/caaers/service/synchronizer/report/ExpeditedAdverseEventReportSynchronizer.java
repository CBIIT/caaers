package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.CompositeSynchronizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class ExpeditedAdverseEventReportSynchronizer extends CompositeSynchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
    
	
	public void preMigrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {

        if(src.getInvestigationalDeviceAdministered() != null) dest.setInvestigationalDeviceAdministered(src.getInvestigationalDeviceAdministered());
        //set the created date is not present and is available in the source
        if(src.getCreatedAt() != null) dest.setCreatedAt(src.getCreatedAt());

    }
}
