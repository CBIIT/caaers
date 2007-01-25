package gov.nih.nci.cabig.caaers.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.tools.configuration.ConfigurationProperty;
import gov.nih.nci.cabig.caaers.tools.configuration.ConfigurationPropertyEditor;

/**
 * @author Rhett Sutphin
 */
public class ConfigurationController extends SimpleFormController {
    private Configuration configuration;

    public ConfigurationController() {
        setCommandClass(ConfigurationCommand.class);
        setFormView("admin/configure");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new ConfigurationCommand(configuration);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        for (ConfigurationProperty<?> property : ConfigurationProperty.values()) {
            binder.registerCustomEditor(Object.class, "conf[" + property.getKey() + "].value",
                new ConfigurationPropertyEditor(property));
        }
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
        return new ModelAndView("redirectToConfiguration", "updated", true);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
