package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.GeneticIntervention;

import java.util.Arrays;
import java.util.List;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class GeneticInterventionSynchronizer extends AbstractAEInterventionSynchronizer {

    private List<String> context = Arrays.asList("e2b");
    @Override
    public List<String> contexts() {
        return context;
    }
    @Override
    public List<GeneticIntervention> existingInterventions(ExpeditedAdverseEventReport aeReport) {
        return aeReport.getGeneticInterventions();
    }

    @Override
    public List<GeneticIntervention> xmlInterventions(ExpeditedAdverseEventReport aeReport) {
        return aeReport.getGeneticInterventions();
    }
}
