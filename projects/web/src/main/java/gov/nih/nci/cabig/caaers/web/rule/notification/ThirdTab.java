package gov.nih.nci.cabig.caaers.web.rule.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.Errors;

public class ThirdTab extends TabWithFields<ReportDefinitionCommand>{

	private InputFieldGroup fieldGroup;
	private InputFieldGroupMap map;
		
	public ThirdTab(String longTitle, String shortTitle, String viewName) {
		super(longTitle, shortTitle, viewName);
	}
	
	public ThirdTab() {
		this("Review","Review","rule/notification/thirdTab");
		
	}


	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.ctms.web.tabs.Tab#postProcess(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void postProcess(HttpServletRequest req, ReportDefinitionCommand nfCmd, Errors errors) {
		super.postProcess(req,nfCmd,errors);
		nfCmd.setValidationFailed(errors.hasErrors());
		if(errors.hasErrors()) return;
		nfCmd.removePlannedNotification();
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.web.fields.TabWithFields#createFieldGroups(java.lang.Object)
	 */
	@Override
	public Map<String, InputFieldGroup> createFieldGroups(ReportDefinitionCommand command) {
		map = new InputFieldGroupMap();
		return map;
	}
	
	
	public Pair fetchFieldValue(InputField field , BeanWrapper command){
		Object value = command.getPropertyValue(field.getPropertyName());
		String strValue = (value == null)? null : String.valueOf(value);
		return new Pair(field.getDisplayName(), strValue );
	}
	
	/**
	 * This will return a list of Pair objects, with each pair consiting of the <code>displayName</code>
	 * of the field and its associated <code>value</code>.
	 * @param fieldGroup - An InputFieldGroup
	 * @param command - The BeanWrapper instance wrapping {@link ReportDefinitionCommand}
	 * @param exclusions - A list of <code>displayName</code> to be eliminated from the display.
	 * @return List&lt;Pair&gt; objects. 
	 */
	public List<Pair> fetchFieldValues(InputFieldGroup fieldGroup, BeanWrapper command, String...exclusions){
		List<Pair> fieldList = new ArrayList<Pair>();
		for(InputField field : fieldGroup.getFields()){
			//do not add if the display name is included in the exclusion list
			if(exclusions != null && ArrayUtils.contains(exclusions, field.getDisplayName())) continue;
			fieldList.add(fetchFieldValue(field, command));
		}
		return fieldList;
	}
	
	@Override
	public Map<String, Object> referenceData(ReportDefinitionCommand command) {
	
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		BeanWrapper wrappedCommand = new BeanWrapperImpl(command);
		
		//basic details tab fields 
		TabWithFields<ReportDefinitionCommand> tab = (TabWithFields<ReportDefinitionCommand>)getFlow().getTab(0) ;
		Map<String, InputFieldGroup> fieldGroupMap = tab.createFieldGroups(command);
		InputFieldGroup fieldGroup = fieldGroupMap.get("ruleset"); //the name of the fieldgroup
		map.put(tab.getShortTitle(), fetchFieldValues(fieldGroup, wrappedCommand));
		
		//report definition tab
		tab = (TabWithFields<ReportDefinitionCommand>) getFlow().getTab(1);
		fieldGroupMap = tab.createFieldGroups(command);
		Map<String, Object> rddMap = new LinkedHashMap<String, Object>();
		int i = 0;
		String exclude = "";
		for(String key : fieldGroupMap.keySet()){
			int entityType = command.getReportDefinition().getDeliveryDefinitions().get(i).getEntityType();
			if(entityType == ReportDeliveryDefinition.ENTITY_TYPE_ROLE)
				exclude = "Address";
			else
				exclude = "Role";
			i++;
			fieldGroup = fieldGroupMap.get(key);
			rddMap.put(key, fetchFieldValues(fieldGroup, wrappedCommand,exclude));
		}
		map.put("Report Delivery Definition", rddMap);
		map.put("rddKeySet", rddMap.keySet());
		
		//Notification details tab
		tab = (TabWithFields<ReportDefinitionCommand>) getFlow().getTab(2);
		fieldGroupMap = tab.createFieldGroups(command);
		Map<String, Object> pnfMap = new LinkedHashMap<String, Object>();
		for(String key : fieldGroupMap.keySet()){
			fieldGroup = fieldGroupMap.get(key);
			pnfMap.put(key, fetchFieldValues(fieldGroup, wrappedCommand));
		}
		map.put("Planned Notification", pnfMap);
		map.put("pnfKeySet", pnfMap.keySet());
		
		Map<String, Object> refDataMap = super.referenceData(command);
		refDataMap.put("FIELDS", map);
		return refDataMap;
		
	}
	
}
