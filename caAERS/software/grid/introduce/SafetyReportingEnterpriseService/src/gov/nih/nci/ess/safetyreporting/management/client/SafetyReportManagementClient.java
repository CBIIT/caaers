/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.safetyreporting.management.client;

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

import gov.nih.nci.ess.safetyreporting.management.stubs.SafetyReportManagementPortType;
import gov.nih.nci.ess.safetyreporting.management.stubs.service.SafetyReportManagementServiceAddressingLocator;
import gov.nih.nci.ess.safetyreporting.management.common.SafetyReportManagementI;
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
public class SafetyReportManagementClient extends SafetyReportManagementClientBase implements SafetyReportManagementI {	

	public SafetyReportManagementClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SafetyReportManagementClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SafetyReportManagementClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SafetyReportManagementClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SafetyReportManagementClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SafetyReportManagementClient client = new SafetyReportManagementClient(args[1]);
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

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion associateAdditionalInformationToSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.AdditionalInformation additionalInformation) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"associateAdditionalInformationToSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequestAdditionalInformation additionalInformationContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportRequestAdditionalInformation();
    additionalInformationContainer.setAdditionalInformation(additionalInformation);
    params.setAdditionalInformation(additionalInformationContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdditionalInformationToSafetyReportResponse boxedResult = portType.associateAdditionalInformationToSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion initiateSafetyReport(ess.caaers.nci.nih.gov.Id studyId,ess.caaers.nci.nih.gov.Id subjectId,ess.caaers.nci.nih.gov.Id patientId,_21090.org.iso.DSET_II adverseEventIds,_21090.org.iso.DSET_II problemIds,gov.nih.nci.ess.safetyreporting.types.AdverseEventReportingPeriod adverseEventReportingPeriod) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"initiateSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestStudyId studyIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestStudyId();
    studyIdContainer.setId(studyId);
    params.setStudyId(studyIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestSubjectId subjectIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestSubjectId();
    subjectIdContainer.setId(subjectId);
    params.setSubjectId(subjectIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestPatientId patientIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestPatientId();
    patientIdContainer.setId(patientId);
    params.setPatientId(patientIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestAdverseEventIds adverseEventIdsContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestAdverseEventIds();
    adverseEventIdsContainer.setDSET_II(adverseEventIds);
    params.setAdverseEventIds(adverseEventIdsContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestProblemIds problemIdsContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestProblemIds();
    problemIdsContainer.setDSET_II(problemIds);
    params.setProblemIds(problemIdsContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestAdverseEventReportingPeriod adverseEventReportingPeriodContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportRequestAdverseEventReportingPeriod();
    adverseEventReportingPeriodContainer.setAdverseEventReportingPeriod(adverseEventReportingPeriod);
    params.setAdverseEventReportingPeriod(adverseEventReportingPeriodContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.InitiateSafetyReportResponse boxedResult = portType.initiateSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion associateAdverseEventsToSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,_21090.org.iso.DSET_II adverseEventIds) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"associateAdverseEventsToSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequestAdverseEventIds adverseEventIdsContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportRequestAdverseEventIds();
    adverseEventIdsContainer.setDSET_II(adverseEventIds);
    params.setAdverseEventIds(adverseEventIdsContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateAdverseEventsToSafetyReportResponse boxedResult = portType.associateAdverseEventsToSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion dissociateAdverseEventsFromSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,_21090.org.iso.DSET_II adverseEventIds) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"dissociateAdverseEventsFromSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequestAdverseEventIds adverseEventIdsContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportRequestAdverseEventIds();
    adverseEventIdsContainer.setDSET_II(adverseEventIds);
    params.setAdverseEventIds(adverseEventIdsContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.DissociateAdverseEventsFromSafetyReportResponse boxedResult = portType.dissociateAdverseEventsFromSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion updateAdverseEventInformationInSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.AdverseEvent adverseEvent) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateAdverseEventInformationInSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequestAdverseEvent adverseEventContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportRequestAdverseEvent();
    adverseEventContainer.setAdverseEvent(adverseEvent);
    params.setAdverseEvent(adverseEventContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.UpdateAdverseEventInformationInSafetyReportResponse boxedResult = portType.updateAdverseEventInformationInSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion associateProblemToSafetyReport() throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"associateProblemToSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateProblemToSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateProblemToSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateProblemToSafetyReportResponse boxedResult = portType.associateProblemToSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion associateStudyToSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id studyId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"associateStudyToSafetyReport");
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequest params = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequest();
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequestSafetyReportId safetyReportIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequestSafetyReportId();
    safetyReportIdContainer.setId(safetyReportId);
    params.setSafetyReportId(safetyReportIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequestStudyId studyIdContainer = new gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportRequestStudyId();
    studyIdContainer.setId(studyId);
    params.setStudyId(studyIdContainer);
    gov.nih.nci.ess.safetyreporting.management.stubs.AssociateStudyToSafetyReportResponse boxedResult = portType.associateStudyToSafetyReport(params);
    return boxedResult.getSafetyReportVersion();
    }
  }

}
