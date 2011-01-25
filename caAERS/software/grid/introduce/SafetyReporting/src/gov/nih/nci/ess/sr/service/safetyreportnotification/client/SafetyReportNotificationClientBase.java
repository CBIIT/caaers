package gov.nih.nci.ess.sr.service.safetyreportnotification.client;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import java.util.Calendar;
import java.util.List;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.globus.gsi.GlobusCredential;

import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.container.ContainerException;

import org.oasis.wsrf.lifetime.ImmediateResourceTermination;
import org.oasis.wsrf.lifetime.WSResourceLifetimeServiceAddressingLocator;

import gov.nih.nci.ess.sr.service.safetyreportnotification.stubs.SafetyReportNotificationPortType;
import gov.nih.nci.ess.sr.service.safetyreportnotification.stubs.service.SafetyReportNotificationServiceAddressingLocator;
import gov.nih.nci.ess.sr.service.safetyreportnotification.common.SafetyReportNotificationI;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;


/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.4
 */
public abstract class SafetyReportNotificationClientBase extends ServiceSecurityClient {	
	protected SafetyReportNotificationPortType portType;
	protected Object portTypeMutex;
    protected NotificationConsumerManager consumer = null;
    protected EndpointReferenceType consumerEPR = null;

	public SafetyReportNotificationClientBase(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	   	initialize();
	}
	
	public SafetyReportNotificationClientBase(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
		initialize();
	}
	
	protected void initialize() throws RemoteException {
	    this.portTypeMutex = new Object();
		this.portType = createPortType();
	}

	protected SafetyReportNotificationPortType createPortType() throws RemoteException {

		SafetyReportNotificationServiceAddressingLocator locator = new SafetyReportNotificationServiceAddressingLocator();
		// attempt to load our context sensitive wsdd file
		InputStream resourceAsStream = getClass().getResourceAsStream("client-config.wsdd");
		if (resourceAsStream != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		SafetyReportNotificationPortType port = null;
		try {
			port = locator.getSafetyReportNotificationPortTypePort(getEndpointReference());
		} catch (Exception e) {
			throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
		}

		return port;
	}
	
	public org.oasis.wsrf.lifetime.DestroyResponse destroy() throws RemoteException {
        synchronized (portTypeMutex) {
            org.oasis.wsrf.lifetime.Destroy params = new org.oasis.wsrf.lifetime.Destroy();
            configureStubSecurity((Stub) portType, "destroy");
            return portType.destroy(params);
        }
    }


    public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(Calendar terminationTime)
        throws RemoteException {
        synchronized (portTypeMutex) {
            configureStubSecurity((Stub) portType, "setTerminationTime");
            org.oasis.wsrf.lifetime.SetTerminationTime params = new org.oasis.wsrf.lifetime.SetTerminationTime(
                terminationTime);
            return portType.setTerminationTime(params);

        }
    }
    

}