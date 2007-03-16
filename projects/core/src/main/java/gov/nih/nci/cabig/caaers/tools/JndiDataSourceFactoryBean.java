package gov.nih.nci.cabig.caaers.tools;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import javax.naming.NameNotFoundException;

/**
 * @author Rhett Sutphin
 */
public class JndiDataSourceFactoryBean extends DatabaseConfigurationAccessor implements FactoryBean {
    private static final Log log = LogFactory.getLog(JndiDataSourceFactoryBean.class);
    private JndiTemplate jndiTemplate;

    public Object getObject() throws Exception {
        try {
            return jndiTemplate.lookup(getName(), getObjectType());
        } catch (NameNotFoundException nnfe) {
            log.debug("JNDI datasource not configured (looked for " + getName() + ')');
            return null;
        }
    }

    private String getName() {
        return "java:/comp/env/caaers/db/" + getDatabaseConfigurationName();
    }

    public Class getObjectType() {
        return DataSource.class;
    }

    public boolean isSingleton() {
        return true;
    }

    ////// CONFIGURATION

    public JndiTemplate getJndiTemplate() {
        return jndiTemplate;
    }

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate;
    }
}
