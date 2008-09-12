package gov.nih.nci.cabig.caaers.tools.configuration;

import org.springframework.core.io.ClassPathResource;

import gov.nih.nci.cabig.ctms.tools.configuration.DatabaseBackedConfiguration;
import gov.nih.nci.cabig.ctms.tools.configuration.ConfigurationProperties;
import gov.nih.nci.cabig.ctms.tools.configuration.ConfigurationProperty;

/**
 * @author Rhett Sutphin
 */
public class Configuration extends DatabaseBackedConfiguration {
    private static final ConfigurationProperties PROPERTIES = new ConfigurationProperties(
                    new ClassPathResource("details.properties", Configuration.class));

    // public static final ConfigurationProperty<Boolean>
    // SHOW_FULL_EXCEPTIONS = new ConfigurationProperty.Bool("showFullExceptions");
    public static final ConfigurationProperty<Boolean> SHOW_DEBUG_INFORMATION = PROPERTIES
                    .add(new ConfigurationProperty.Bool("showDebugInformation"));

    public static final ConfigurationProperty<String> PSC_BASE_URL = PROPERTIES
                    .add(new ConfigurationProperty.Text("pscBaseUrl"));

    public static final ConfigurationProperty<String> LABVIEWER_BASE_URL = PROPERTIES
                    .add(new ConfigurationProperty.Text("labViewerBaseUrl"));

    public static final ConfigurationProperty<String> ESB_URL = PROPERTIES
                    .add(new ConfigurationProperty.Text("esbUrl"));
    
    public static final ConfigurationProperty<String> CAEXCHANGE_URL = PROPERTIES
    				.add(new ConfigurationProperty.Text("caExchangeUrl"));

    public static final ConfigurationProperty<String> SMTP_ADDRESS = PROPERTIES
                    .add(new ConfigurationProperty.Text("smtpAddress"));

    public static final ConfigurationProperty<Integer> SMTP_PORT = PROPERTIES
                    .add(new ConfigurationProperty.Int("smtpPort"));

    public static final ConfigurationProperty<String> SMTP_USER = PROPERTIES
                    .add(new ConfigurationProperty.Text("smtpUser"));

    public static final ConfigurationProperty<String> SMTP_PASSWORD = PROPERTIES
                    .add(new ConfigurationProperty.Text("smtpPassword"));
    
    public static final ConfigurationProperty<Boolean> SMTP_SSL_ENABLED = PROPERTIES.add(new ConfigurationProperty.Bool("smtpSSLEnabled"));

    public static final ConfigurationProperty<String> SYSTEM_FROM_EMAIL = PROPERTIES
                    .add(new ConfigurationProperty.Text("systemFromEmail"));

    public ConfigurationProperties getProperties() {
        return PROPERTIES;
    }
}
