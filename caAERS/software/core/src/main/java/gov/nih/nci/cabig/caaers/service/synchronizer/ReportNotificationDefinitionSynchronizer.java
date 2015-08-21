/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.report.PlannedNotification;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chandrasekaravr
 */
public class ReportNotificationDefinitionSynchronizer implements Synchronizer<gov.nih.nci.cabig.caaers.domain.report.ReportDefinition>{

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	public void migrate(ReportDefinition xmlReportDefinition, ReportDefinition dbReportDefinition, DomainObjectImportOutcome<ReportDefinition> outcome) {
		List<PlannedNotification> dbNotificationDefs = dbReportDefinition.getPlannedNotificationsInternal();
		List<PlannedNotification> xmlNotificationDefs = xmlReportDefinition.getPlannedNotificationsInternal();
		if( dbNotificationDefs != null) {
			dbNotificationDefs.clear();
			if(xmlNotificationDefs == null) {
				return;
			}//end of if
			for(PlannedNotification defn: xmlNotificationDefs){
				dbReportDefinition.addPlannedNotification(defn);
			} //end of for
		}//end of outer if
	}
}
