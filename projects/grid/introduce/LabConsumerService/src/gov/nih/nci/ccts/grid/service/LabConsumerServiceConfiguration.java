package gov.nih.nci.ccts.grid.service;

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
public class LabConsumerServiceConfiguration implements ServiceConfiguration {

	public static LabConsumerServiceConfiguration  configuration = null;

	public static LabConsumerServiceConfiguration getConfiguration() throws Exception {
		if (LabConsumerServiceConfiguration.configuration != null) {
			return LabConsumerServiceConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			LabConsumerServiceConfiguration.configuration = (LabConsumerServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return LabConsumerServiceConfiguration.configuration;
	}
	
	private String etcDirectoryPath;
	
	
	
	public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}

	
}
