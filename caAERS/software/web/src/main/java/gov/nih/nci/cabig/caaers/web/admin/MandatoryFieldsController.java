/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.dao.CaaersFieldDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.CaaersFieldDefinition;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.CaaersFieldsTree;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TabSection;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TreeNode;
import gov.nih.nci.cabig.caaers.domain.report.Mandatory;
import gov.nih.nci.cabig.caaers.validation.CaaersFieldConfigurationManager;
import gov.nih.nci.cabig.caaers.web.ae.AdverseEventCaptureTab;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Sameer Sawant
 * @author Ion C. Olaru
 * 
 */
public class MandatoryFieldsController extends SimpleFormController {

	private CaaersFieldsTree caaersFieldsTree;
	private CaaersFieldDefinitionDao caaersFieldDefinitionDao;
	private CaaersFieldConfigurationManager caaersFieldConfigurationManager;
	
    public MandatoryFieldsController() {
        setFormView("admin/mandatory_fields");
        setBindOnNewForm(true);
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
    	MandatoryFieldsCommand command = new MandatoryFieldsCommand(caaersFieldDefinitionDao);
        return command;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object cmd, BindException errors) throws Exception {
    	// If there are no binding errors then save the updated values of CaaersFieldDefinition
    	MandatoryFieldsCommand command = (MandatoryFieldsCommand) cmd;
    	// If there are no erros then save the CaaersFieldsDefinitions list
    	if(!errors.hasErrors()){
    		for(CaaersFieldDefinition cfd: command.getMandatoryFields()) {
                // System.out.println("Saving..." + cfd.getFieldPath() + "=" + cfd.getMandatory());
    			caaersFieldDefinitionDao.save(cfd);
            }
    		// reinitialize caaersFieldConfigurationManager
    		caaersFieldConfigurationManager.initializeConfigurationManager();
    	}
    	Map map = this.referenceData(request, command, errors);
        map.putAll(errors.getModel());
    	
        ModelAndView modelAndView = new ModelAndView(getFormView(), map);
        return modelAndView.addObject("updated", true);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(final HttpServletRequest request, final Object cmd, final Errors errors) throws Exception {
        Map<Object, Object> refDataMap = new LinkedHashMap<Object, Object>();
        MandatoryFieldsCommand command = (MandatoryFieldsCommand) cmd;
    	command.initializeMandatoryFieldMap();
        
        Map<String, InputFieldGroup> fieldMap;
        fieldMap = new LinkedHashMap<String, InputFieldGroup>();
        populateFieldMap(command, fieldMap, caaersFieldsTree.getNodeForSection(TabSection.CAPTURE_AE_TAB_SECTION));
        populateFieldMap(command, fieldMap, caaersFieldsTree.getNodeForSection(TabSection.COURSE_CYCLE_SECTION));
        refDataMap.put("fieldGroups", fieldMap);
        return refDataMap;
    }
    
    protected void populateMandatoryFields(List<CaaersFieldDefinition> mfList, TreeNode node) {
        if (StringUtils.isNotEmpty(node.getPropertyPath())) {
            CaaersFieldDefinition mf = new CaaersFieldDefinition(AdverseEventCaptureTab.class.getName(), node.getPropertyPath());
            mfList.add(mf);
        }
        if (node.getChildren() != null) {
            for (TreeNode n : node.getChildren())
                populateMandatoryFields(mfList, n);
        }
    }
    
    /**
     * Populates the fields, the key of the map will be qualified name of the parent. Display name
     * of the field will be display name of the node. In case if the node does not have a display
     * name, the display name of the parent will be used instead.
     */
    //protected void populateFieldMap(MandatoryFieldsCommand command, Map<String, InputFieldGroup> map, TreeNode node) {
    protected void populateFieldMap(MandatoryFieldsCommand command, Map<String, InputFieldGroup> map, TreeNode node){
        // only add leaf nodes in the filed map. (others are just sections)
    	if(node.isLeaf()){
            String key = node.getParent().getQualifiedDisplayName();
            InputFieldGroup group = map.get(key);
            if (group == null) {
                group = new DefaultInputFieldGroup(key);
                map.put(key, group);
            }
            List<InputField> fields = group.getFields();
            
            String displayName = node.getDisplayName();
            String path = node.getPropertyPath();
            if (StringUtils.isEmpty(path)) return;
            Integer pathIndex = command.getMandatoryFieldMap().get(path);
            if (pathIndex == null) return;
            
            int index = pathIndex.intValue();
            if (StringUtils.isEmpty(displayName)) displayName = node.getParent().getDisplayName();

            fields.add(InputFieldFactory.createSelectField("mandatoryFields["+ index + "].mandatory", displayName, false, WebUtils.collectOptions(Arrays.asList(Mandatory.values()), "name", "displayName")));
        } else {
            // add children of this node in the map
            for (TreeNode n : node.getChildren())
                populateFieldMap(command, map, n);
        }
    }

    @Required
    public void setCaaersFieldsTree(CaaersFieldsTree caaersFieldsTree){
    	this.caaersFieldsTree = caaersFieldsTree;
    }
    
    @Required
    public void setCaaersFieldDefinitionDao(CaaersFieldDefinitionDao caaersFieldDefinitionDao){
    	this.caaersFieldDefinitionDao = caaersFieldDefinitionDao;
    }
    
    @Required 
    public void setCaaersFieldConfigurationManager(CaaersFieldConfigurationManager caaersFieldConfigurationManager){
    	this.caaersFieldConfigurationManager = caaersFieldConfigurationManager;
    }
}
