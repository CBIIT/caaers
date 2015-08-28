package gov.nih.nci.cabig.caaers.service.synchronizer.report;

import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.synchronizer.Synchronizer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author Biju Joseph
 * @since 1.5
 */
public class ExpeditedReportingPeriodSynchronizer implements Synchronizer<ExpeditedAdverseEventReport> {

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }

    public void migrate(ExpeditedAdverseEventReport aeReportSrc, ExpeditedAdverseEventReport aeReportDest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {
        AdverseEventReportingPeriod src = aeReportSrc.getReportingPeriod();
        AdverseEventReportingPeriod dest = aeReportDest.getReportingPeriod();
        
        String srctac = src.getTreatmentAssignment() != null ? src.getTreatmentAssignment().getCode() : src.getTreatmentAssignmentDescription();
        String desttac = dest.getTreatmentAssignment() != null ? dest.getTreatmentAssignment().getCode() : dest.getTreatmentAssignmentDescription();
        
        if((desttac != null && dest!= null && dest.getStartDate() != null && dest.getCycleNumber() != null) && (!desttac.equals(srctac) || !dest.getStartDate().equals(src.getStartDate()) || !dest.getCycleNumber().equals(src.getCycleNumber())) ) {
        	outcome.addError("ER-RP-2", "Reporting period with TAC: " + srctac + 
					 		(src.getStartDate() != null? " Start date of course: " +  src.getStartDate() :"") +
							 (src.getCycleNumber() != null? " Course number: " +  src.getCycleNumber() :"") +
							 " is not part of the report with report id, " + aeReportDest.getExternalId()
						);
	            return;
        }
        
        dest.setEndDate(src.getEndDate());
        if(!StringUtils.isBlank(src.getTreatmentAssignmentDescription())){
        	dest.setTreatmentAssignmentDescription(src.getTreatmentAssignmentDescription());
        }
     }
}
