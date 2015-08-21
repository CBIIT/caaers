package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ParticipantHistory;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class ParticipantHistorySynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = Arrays.asList("e2b");
    @Override
    public List<String> contexts() {
        return context;
    }
    public void migrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        ParticipantHistory xmlParticipantHistory = src.getParticipantHistory();
        ParticipantHistory dbParticipantHistory = dest.getParticipantHistory();
        if(dbParticipantHistory == null || xmlParticipantHistory == null){
            dest.setParticipantHistory(xmlParticipantHistory);
            return;
        }

        //copy the details from XML ParticipantHistory
        dbParticipantHistory.setBaselinePerformanceStatus(xmlParticipantHistory.getBaselinePerformanceStatus());
        dbParticipantHistory.setBsa(xmlParticipantHistory.getBsa());
        dbParticipantHistory.setHeight(xmlParticipantHistory.getHeight());
        dbParticipantHistory.setWeight(xmlParticipantHistory.getWeight());

    }
}
