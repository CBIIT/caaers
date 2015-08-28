package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.Physician;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class PhysicianSynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
    public void migrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        Physician xmlPhysician = src.getPhysician();
        Physician dbPhysician = dest.getPhysician();
        
        //  CAAERS-6848 if physician in input message is null, don't override the one in db
        if(xmlPhysician == null){
        	return;
        }
        
        if(dbPhysician == null){
            dest.setPhysician(xmlPhysician);
            return;
        }

        Investigator xmlInv = xmlPhysician.getInvestigator();
        Investigator dbInv = dbPhysician.getInvestigator();
        if(dbInv == null || xmlInv == null || !dbInv.getId().equals(xmlInv.getId())){
            dbPhysician.setInvestigator(xmlInv);
        }


        //copy physician details.
        dbPhysician.copy(xmlPhysician);
    }
}
