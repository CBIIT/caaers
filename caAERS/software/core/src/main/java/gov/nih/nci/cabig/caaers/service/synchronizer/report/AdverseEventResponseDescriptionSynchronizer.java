package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.AdverseEventResponseDescription;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class AdverseEventResponseDescriptionSynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = Arrays.asList("e2b");

    @Override
    public List<String> contexts() {
        return context;
    }

    public void migrate(ExpeditedAdverseEventReport aeReportSrc, ExpeditedAdverseEventReport aeReportDest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        AdverseEventResponseDescription src = aeReportSrc.getResponseDescription();
        AdverseEventResponseDescription dest = aeReportDest.getResponseDescription();

        dest.setEventDescription(src.getEventDescription());
        dest.setRecoveryDate(src.getRecoveryDate());
        dest.setRetreated(src.getRetreated());
        dest.setDateRemovedFromProtocol(src.getDateRemovedFromProtocol());
        dest.setBlindBroken(src.getBlindBroken());
        dest.setStudyDrugInterrupted(src.getStudyDrugInterrupted());
        dest.setReducedDose(src.getReducedDose());
        dest.setReducedDate(src.getReducedDate());
        dest.setDaysNotGiven(src.getDaysNotGiven());
        dest.setPrimaryTreatment(src.getPrimaryTreatment());
        dest.setCauseOfDeath(src.getCauseOfDeath());
        dest.setAutopsyPerformed(src.getAutopsyPerformed());
        dest.setPrimaryTreatmentApproximateTime(src.getPrimaryTreatmentApproximateTime());
        dest.setEventAbate(src.getEventAbate());
        dest.setEventReappear(src.getEventReappear());
        dest.setPresentStatus(src.getPresentStatus());
    }
}
