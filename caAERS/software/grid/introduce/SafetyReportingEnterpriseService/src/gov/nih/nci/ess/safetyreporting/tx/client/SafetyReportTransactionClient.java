package gov.nih.nci.ess.safetyreporting.tx.client;

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

import gov.nih.nci.ess.safetyreporting.tx.stubs.SafetyReportTransactionPortType;
import gov.nih.nci.ess.safetyreporting.tx.stubs.service.SafetyReportTransactionServiceAddressingLocator;
import gov.nih.nci.ess.safetyreporting.tx.common.SafetyReportTransactionI;
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
 * @created by Introduce Toolkit version 1.3
 */
public class SafetyReportTransactionClient extends SafetyReportTransactionClientBase implements SafetyReportTransactionI {	

	public SafetyReportTransactionClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SafetyReportTransactionClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SafetyReportTransactionClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SafetyReportTransactionClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SafetyReportTransactionClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SafetyReportTransactionClient client = new SafetyReportTransactionClient(args[1]);
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

  public void amendSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"amendSafetyReport");
    gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.AmendSafetyReportResponse boxedResult = portType.amendSafetyReport(params);
    }
  }

  public void submitSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId,ess.caaers.nci.nih.gov.Id submitterId,_21090.org.iso.DSET_TEL additionalRecipientEmails) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"submitSafetyReport");
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestSubmitterId submitterIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestSubmitterId();
    submitterIdContainer.setId(submitterId);
    params.setSubmitterId(submitterIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestAdditionalRecipientEmails additionalRecipientEmailsContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportRequestAdditionalRecipientEmails();
    additionalRecipientEmailsContainer.setDSET_TEL(additionalRecipientEmails);
    params.setAdditionalRecipientEmails(additionalRecipientEmailsContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.SubmitSafetyReportResponse boxedResult = portType.submitSafetyReport(params);
    }
  }

  public void withdrawSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"withdrawSafetyReport");
    gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.tx.stubs.WithdrawSafetyReportResponse boxedResult = portType.withdrawSafetyReport(params);
    }
  }

}
