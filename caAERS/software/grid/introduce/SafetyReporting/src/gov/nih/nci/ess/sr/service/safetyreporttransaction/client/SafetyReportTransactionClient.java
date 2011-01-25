package gov.nih.nci.ess.sr.service.safetyreporttransaction.client;

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

import gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SafetyReportTransactionPortType;
import gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.service.SafetyReportTransactionServiceAddressingLocator;
import gov.nih.nci.ess.sr.service.safetyreporttransaction.common.SafetyReportTransactionI;
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

  public sr.SafetyReportVersion amendSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportIdentifier,ess.caaers.nci.nih.gov.Id amendmentIdentifier,_21090.org.iso.ST reasonForAmend) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"amendSafetyReport");
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequest params = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequest();
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestSafetyReportIdentifier safetyReportIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestSafetyReportIdentifier();
    safetyReportIdentifierContainer.setId(safetyReportIdentifier);
    params.setSafetyReportIdentifier(safetyReportIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestAmendmentIdentifier amendmentIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestAmendmentIdentifier();
    amendmentIdentifierContainer.setId(amendmentIdentifier);
    params.setAmendmentIdentifier(amendmentIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestReasonForAmend reasonForAmendContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportRequestReasonForAmend();
    reasonForAmendContainer.setST(reasonForAmend);
    params.setReasonForAmend(reasonForAmendContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.AmendSafetyReportResponse boxedResult = portType.amendSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public sr.SafetyReportVersion submitSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportIdentifier,ess.caaers.nci.nih.gov.Id submitterIdentifier,_21090.org.iso.DSET_II additionalRecipientIdentifiers) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"submitSafetyReport");
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequest params = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequest();
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestSafetyReportIdentifier safetyReportIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestSafetyReportIdentifier();
    safetyReportIdentifierContainer.setId(safetyReportIdentifier);
    params.setSafetyReportIdentifier(safetyReportIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestSubmitterIdentifier submitterIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestSubmitterIdentifier();
    submitterIdentifierContainer.setId(submitterIdentifier);
    params.setSubmitterIdentifier(submitterIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestAdditionalRecipientIdentifiers additionalRecipientIdentifiersContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportRequestAdditionalRecipientIdentifiers();
    additionalRecipientIdentifiersContainer.setDSET_II(additionalRecipientIdentifiers);
    params.setAdditionalRecipientIdentifiers(additionalRecipientIdentifiersContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.SubmitSafetyReportResponse boxedResult = portType.submitSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public sr.SafetyReportVersion withdrawSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportIdentifier,ess.caaers.nci.nih.gov.Id withdrawerIdentifier) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"withdrawSafetyReport");
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequest params = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequest();
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequestSafetyReportIdentifier safetyReportIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequestSafetyReportIdentifier();
    safetyReportIdentifierContainer.setId(safetyReportIdentifier);
    params.setSafetyReportIdentifier(safetyReportIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequestWithdrawerIdentifier withdrawerIdentifierContainer = new gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportRequestWithdrawerIdentifier();
    withdrawerIdentifierContainer.setId(withdrawerIdentifier);
    params.setWithdrawerIdentifier(withdrawerIdentifierContainer);
    gov.nih.nci.ess.sr.service.safetyreporttransaction.stubs.WithdrawSafetyReportResponse boxedResult = portType.withdrawSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
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

}