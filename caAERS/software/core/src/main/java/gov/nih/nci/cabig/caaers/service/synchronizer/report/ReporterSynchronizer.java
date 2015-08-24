package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class ReporterSynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
    public void migrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        Reporter xmlReporter = src.getReporter();
        Reporter dbReporter = dest.getReporter();
        
        //  CAAERS-6848 if reporter in input message is null, don't override the one in db
        if(xmlReporter == null){
        	return;
        }
        if(dbReporter == null){
            dest.setReporter(xmlReporter);
            return;
        }

        Investigator xmlInv = xmlReporter.getInvestigator();
        Investigator dbInv = dbReporter.getInvestigator();
        if(dbInv == null || xmlInv == null || !dbInv.getId().equals(xmlInv.getId())){
            dbReporter.setInvestigator(xmlInv);
        }

        ResearchStaff xmlRs = xmlReporter.getResearchStaff();
        ResearchStaff dbRs = dbReporter.getResearchStaff();
        if(dbRs == null || xmlRs == null || !dbRs.getId().equals(xmlRs.getId())){
            dbReporter.setResearchStaff(xmlRs);
        }

        //copy reporter details.
        dbReporter.copy(xmlReporter);
    }
}
