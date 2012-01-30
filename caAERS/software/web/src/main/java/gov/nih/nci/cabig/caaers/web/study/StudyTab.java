package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.fields.*;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * @author Rhett Sutphin, Priyatam
 */
public abstract class StudyTab extends TabWithFields<StudyCommand> {


    private ConfigProperty configurationProperty;
    protected static final Log log = LogFactory.getLog(StudyTab.class);

    public StudyTab(String longTitle, String shortTitle, String viewName) {
        super(longTitle, shortTitle, viewName);
    }

    public ConfigProperty getConfigurationProperty() {
        return configurationProperty;
    }

    public void setConfigurationProperty(ConfigProperty configProperty) {
        this.configurationProperty = configProperty;
    }


    protected Map<Object, Object> collectOptions(List list, String nameProperty, String valueProperty, String... exclusionProperties) {
        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.put("", "Please select");
        options.putAll(WebUtils.collectOptions(list, nameProperty, valueProperty));
        for (String key : exclusionProperties) {
            options.remove(key);
        }
        return options;
    }

    protected Map<Object, Object> collectOptionsFromConfig(String configPropertyName, String nameProperty, String valueProperty, String... exclusionProperties) {
        return collectOptions(configurationProperty.getMap().get(configPropertyName), nameProperty, valueProperty, exclusionProperties);
    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(StudyCommand command) {
        return new InputFieldGroupMap();
    }

    protected List<Lov> collectStudyOrganizations(Study study) {
        ArrayList<Lov> list = new ArrayList<Lov>();
        list.add(new Lov("-1", " - Please select - "));
        if (study.getStudyOrganizations() != null) {
            int i = -1;
            for (StudyOrganization so : study.getActiveStudyOrganizations()) {
                i++;
                if (so.getOrganization() == null) continue;
                list.add(new Lov(String.valueOf(i), so.getOrganization().getName() + " (" + so.getRoleName() + ")"));
            }
        }

        return list;
    }

    public void rejectFields(List<InputField> fields, Errors errors, String errorMessage) {
        for (InputField field : fields) {
            errors.rejectValue(field.getPropertyName(), "REQUIRED", errorMessage + " " + field.getDisplayName());
        }
    }

}
