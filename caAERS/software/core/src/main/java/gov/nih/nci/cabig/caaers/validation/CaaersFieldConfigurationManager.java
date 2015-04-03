/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.validation;

import gov.nih.nci.cabig.caaers.dao.CaaersFieldDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.CaaersFieldDefinition;
import gov.nih.nci.cabig.caaers.domain.report.Mandatory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds the configuration of various fields on tabs basis. The datastructure
 * it holds is a Map, where the key is the tab's classname and value is the map with the 
 * field-paths as the key and "Mandatory" as the values. Once the configuration is changed
 * on the admin page, these values are modidifed in the Manager's instance so that the
 * fields are rendered as per the new configuration. The tab whose fields are configur-
 * able need to call this Manager to setup the fieldgroups accordingly.
 * @author Sameer Sawant
 */
public class CaaersFieldConfigurationManager{

    public static final String COURSE_FIELD_GROUP = "gov.nih.nci.cabig.caaers.web.ae.CourseCycleTab";
    public static final String AE_FIELD_GROUP = "gov.nih.nci.cabig.caaers.web.ae.AdverseEventCaptureTab";

	private CaaersFieldDefinitionDao caaersFieldDefinitionDao;
	private Map<String, Map<String, Mandatory>> fieldConfigurationMap;
	
	/**
	 * @return the fieldConfigurationMap
	 */
	public Map<String, Map<String, Mandatory>> getFieldConfigurationMap() {
		if(fieldConfigurationMap == null)
			initializeConfigurationManager();
		return fieldConfigurationMap;
	}

	/**
	 * @param fieldConfigurationMap the fieldConfigurationMap to set
	 */
	public void setFieldConfigurationMap(Map<String, Map<String, Mandatory>> fieldConfigurationMap) {
		this.fieldConfigurationMap = fieldConfigurationMap;
	}

	/**
	 * This method initializes the fieldConfigurationMap. It gets all of the caaersFieldConfiguration from the DB and populates
	 * this map. This will be called from the admin tab on save to restore the new configurations.
	 */
	public void initializeConfigurationManager(){
		fieldConfigurationMap = new HashMap<String, Map<String, Mandatory>>();
		
		List<CaaersFieldDefinition> caaersFieldsDefinitionList = caaersFieldDefinitionDao.getAll();
		// Iterate over this list and setup
		for(CaaersFieldDefinition cfd: caaersFieldsDefinitionList){
			if(fieldConfigurationMap.containsKey(cfd.getTabName())){
				fieldConfigurationMap.get(cfd.getTabName()).put(cfd.getFieldPath(), cfd.getMandatory());
			}else{
				Map<String, Mandatory> configMap = new HashMap<String, Mandatory>();
				configMap.put(cfd.getFieldPath(), cfd.getMandatory());
				fieldConfigurationMap.put(cfd.getTabName(), configMap);
			}
		}
	}
	
	/**
	 * This method returns the list of all the fields that need to be rendered on the screen.
	 * @param tabName
	 * @return
	 */
	public List<String> getListOfApplicableFields(String tabName){
		List<String> applicableFieldsList = new ArrayList<String>();
		Map<String, Mandatory> fieldConfigMap = getFieldConfigurationMap().get(tabName);
		if(fieldConfigMap != null){
			for(Map.Entry<String,Mandatory> entry: fieldConfigMap.entrySet()){
				if(!entry.getValue().equals(Mandatory.NA))
					applicableFieldsList.add(entry.getKey());
			}
		}
		return applicableFieldsList;
	}
	
	/**
	 * This method returns the list of all the fields that will not be rendered on the screen.
	 */
	public List<String> getListOfNotApplicableFields(String tabName){
		List<String> notApplicableFieldsList = new ArrayList<String>();
		Map<String, Mandatory> fieldConfigMap = getFieldConfigurationMap().get(tabName);
		if(fieldConfigMap != null){
			for(Map.Entry<String,Mandatory> entry: fieldConfigMap.entrySet()){
				if(entry.getValue().equals(Mandatory.NA))
					notApplicableFieldsList.add(entry.getKey());
			}
		}
		return notApplicableFieldsList;
	}
	
	/**
	 * This method determines if a particular field is mandatory or optional
	 * @param tabName
	 * @param fieldPath
	 * @return
	 */
	public boolean isFieldMandatory(String tabName, String fieldPath){
		Map<String, Mandatory> fieldConfigMap = getFieldConfigurationMap().get(tabName);
		if(fieldConfigMap != null && fieldConfigMap.get(fieldPath) != null && fieldConfigMap.get(fieldPath).equals(Mandatory.MANDATORY)) { 
			return true;
		}
		return false;
	}
	
	/**
	 * This method determines if a particular field is applicable
	 * @param tabName
	 * @param fieldPath
	 * @return boolean
	 */
	public boolean isFieldApplicable(String tabName, String fieldPath){
		Map<String, Mandatory> fieldConfigMap = getFieldConfigurationMap().get(tabName);
		if(fieldConfigMap != null && fieldConfigMap.get(fieldPath) != null){
			if(!fieldConfigMap.get(fieldPath).equals(Mandatory.NA))
				return true;
		}
		return false;
	}
	
	/**
	 * @return the caaersFieldDefinitionDao
	 */
	public CaaersFieldDefinitionDao getCaaersFieldDefinitionDao() {
		return caaersFieldDefinitionDao;
	}

	/**
	 * @param caaersFieldDefinitionDao the caaersFieldDefinitionDao to set
	 */
	public void setCaaersFieldDefinitionDao(
			CaaersFieldDefinitionDao caaersFieldDefinitionDao) {
		this.caaersFieldDefinitionDao = caaersFieldDefinitionDao;
	}
	
}
