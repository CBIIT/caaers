package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * @author Saurabh Agrawal
 */
public class OrganizationTab extends TabWithFields<Organization> {

	protected static final Log log = LogFactory.getLog(OrganizationTab.class);

	private static final String ORGANIZATION_FIELD_GROUP = "organization";

	public OrganizationTab() {
		super("Organization Details", "Organization Details", "admin/organization_details");
		setAutoPopulateHelpKey(true);
	}

	@Override
	public Map<String, InputFieldGroup> createFieldGroups(final Organization command) {
		InputFieldGroup organizationFieldGroup;

		organizationFieldGroup = new DefaultInputFieldGroup(ORGANIZATION_FIELD_GROUP);

		InputField nameField = InputFieldFactory.createTextField("name", "Name", true);
		InputFieldAttributes.setSize(nameField, 80);

		organizationFieldGroup.getFields().add(nameField);

		InputField descriptionField = InputFieldFactory.createTextArea("descriptionText", "Description", false);
		InputFieldAttributes.setSize(descriptionField, 1200);

		organizationFieldGroup.getFields().add(descriptionField);

		InputField nciInstituteField = InputFieldFactory.createTextField("nciInstituteCode", "NCI Identifier", true);
		InputFieldAttributes.setSize(nciInstituteField, 40);

		organizationFieldGroup.getFields().add(nciInstituteField);

		InputFieldGroupMap map = new InputFieldGroupMap();
		map.addInputFieldGroup(organizationFieldGroup);
		return map;
	}

	@Override
	protected void validate(final Organization command, final BeanWrapper commandBean,
			final Map<String, InputFieldGroup> fieldGroups, final Errors errors) {
		super.validate(command, commandBean, fieldGroups, errors);

	}

}