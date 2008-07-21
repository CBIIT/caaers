package gov.nih.nci.cabig.ctms.grid.ae.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class AdverseEventConsumerConfiguration implements ServiceConfiguration {

	public static AdverseEventConsumerConfiguration  configuration = null;

	public static AdverseEventConsumerConfiguration getConfiguration() throws Exception {
		if (AdverseEventConsumerConfiguration.configuration != null) {
			return AdverseEventConsumerConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			AdverseEventConsumerConfiguration.configuration = (AdverseEventConsumerConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return AdverseEventConsumerConfiguration.configuration;
	}
	
	private String etcDirectoryPath;
	
	
	
	public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}

	
}
