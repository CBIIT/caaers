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

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion amendSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id amendmentId,_21090.org.iso.ST reasonForAmend) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion submitSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id submitterId,_21090.org.iso.DSET_II additionalRecipientIds) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion withdrawSafetyReport(ess.caaers.nci.nih.gov.Id safetyReportId,ess.caaers.nci.nih.gov.Id withdrawerId,_21090.org.iso.ST reasonForWithdraw) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

}
