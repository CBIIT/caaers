package gov.nih.nci.ess.safetyreporting.tx.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public interface SafetyReportTransactionI {

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException ;

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException ;

  public void amendSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

  public void submitSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId,ess.caaers.nci.nih.gov.Id submitterId,_21090.org.iso.DSET_TEL additionalRecipientEmails) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

  public void withdrawSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id reportDefinitionId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

}

