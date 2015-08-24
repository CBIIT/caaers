/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.dao.ConfigPropertyDao;
import gov.nih.nci.cabig.caaers.domain.ConfigPropertyType;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportType;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sameer Sawant
 */
public class ReportDefinitionSynchronizer extends CompositeSynchronizer<ReportDefinition>{


	private ConfigPropertyDao configPropertyDao;
    private List<String> context = new ArrayList<String>();

	public void setConfigPropertyDao(ConfigPropertyDao configPropertyDao) {
		this.configPropertyDao = configPropertyDao;
	}

    @Override
    public List<String> contexts() {
        return context;
    }
	
	@Override
	/**
	 * Will sync the basic properties of the source ReportDefinition to destination ReportDefinition.
	 */
	public void preMigrate(ReportDefinition xmlReportDefinition, ReportDefinition dbReportDefinition,
		DomainObjectImportOutcome<ReportDefinition> outcome) {
		//Populate Class Level Attributes
		dbReportDefinition.setName(xmlReportDefinition.getName());
		dbReportDefinition.setLabel(xmlReportDefinition.getLabel());
		dbReportDefinition.setDescription(xmlReportDefinition.getDescription());
		dbReportDefinition.setAmendable(xmlReportDefinition.getAmendable());
		dbReportDefinition.setDuration(xmlReportDefinition.getDuration());
		dbReportDefinition.setPhysicianSignOff(xmlReportDefinition.getPhysicianSignOff());
		dbReportDefinition.setAttributionRequired(xmlReportDefinition.getAttributionRequired());
		dbReportDefinition.setTimeScaleUnitType(xmlReportDefinition.getTimeScaleUnitType());
		dbReportDefinition.setReportFormatType(xmlReportDefinition.getReportFormatType());
		
		//Populate Organization
		dbReportDefinition.setOrganization(xmlReportDefinition.getOrganization());
		
		//Populate Parent
		dbReportDefinition.setParent(xmlReportDefinition.getParent());
		dbReportDefinition.setWorkflowEnabled(xmlReportDefinition.getWorkflowEnabled());
		
		//populate the correct config property
		if(xmlReportDefinition.getGroup() == null){
			dbReportDefinition.setGroup(configPropertyDao.getByTypeAndCode(ConfigPropertyType.REPORT_GROUP,"RT_AdEERS"));
		}else{
			dbReportDefinition.setGroup(xmlReportDefinition.getGroup());
		}
		if(xmlReportDefinition.getReportType() == null){
			dbReportDefinition.setReportType(ReportType.REPORT);
		}else{
			dbReportDefinition.setReportType(xmlReportDefinition.getReportType());
		}
	}
}
