package gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.client;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;

import org.globus.gsi.GlobusCredential;

import gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.SafetyReportRulesManagementPortType;
import gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.service.SafetyReportRulesManagementServiceAddressingLocator;
import gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.common.SafetyReportRulesManagementI;
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
public class SafetyReportRulesManagementClient extends SafetyReportRulesManagementClientBase implements SafetyReportRulesManagementI {	

	public SafetyReportRulesManagementClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SafetyReportRulesManagementClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SafetyReportRulesManagementClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SafetyReportRulesManagementClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SafetyReportRulesManagementClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SafetyReportRulesManagementClient client = new SafetyReportRulesManagementClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"destroy");
    return portType.destroy(params);
    }
  }

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"setTerminationTime");
    return portType.setTerminationTime(params);
    }
  }

  public void createOrganizationSafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createOrganizationSafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateOrganizationSafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateOrganizationSafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateOrganizationSafetyReportingRulesResponse boxedResult = portType.createOrganizationSafetyReportingRules(params);
    }
  }

  public void updateOrganizationSafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateOrganizationSafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateOrganizationSafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateOrganizationSafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateOrganizationSafetyReportingRulesResponse boxedResult = portType.updateOrganizationSafetyReportingRules(params);
    }
  }

  public void deactivateOrganizationSafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"deactivateOrganizationSafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateOrganizationSafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateOrganizationSafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateOrganizationSafetyReportingRulesResponse boxedResult = portType.deactivateOrganizationSafetyReportingRules(params);
    }
  }

  public void createStudySafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createStudySafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateStudySafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateStudySafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.CreateStudySafetyReportingRulesResponse boxedResult = portType.createStudySafetyReportingRules(params);
    }
  }

  public void updateStudySafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateStudySafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateStudySafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateStudySafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.UpdateStudySafetyReportingRulesResponse boxedResult = portType.updateStudySafetyReportingRules(params);
    }
  }

  public void deactivateStudySafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"deactivateStudySafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateStudySafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateStudySafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.DeactivateStudySafetyReportingRulesResponse boxedResult = portType.deactivateStudySafetyReportingRules(params);
    }
  }

  public void querySafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"querySafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.QuerySafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.QuerySafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.QuerySafetyReportingRulesResponse boxedResult = portType.querySafetyReportingRules(params);
    }
  }

  public void getSafetyReportingRules() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getSafetyReportingRules");
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.GetSafetyReportingRulesRequest params = new gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.GetSafetyReportingRulesRequest();
    gov.nih.nci.ess.sr.service.safetyreportrulesmanagement.stubs.GetSafetyReportingRulesResponse boxedResult = portType.getSafetyReportingRules(params);
    }
  }

}