package gov.nih.nci.ess.sr.service.safetyreportquery.service.globus;

import gov.nih.nci.ess.sr.service.safetyreportquery.service.SafetyReportQueryImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the SafetyReportingImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correctly mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.4
 * 
 */
public class SafetyReportQueryProviderImpl{
	
	SafetyReportQueryImpl impl;
	
	public SafetyReportQueryProviderImpl() throws RemoteException {
		impl = new SafetyReportQueryImpl();
	}
	

    public gov.nih.nci.ess.sr.service.safetyreportquery.stubs.QuerySafetyReportsResponse querySafetyReports(gov.nih.nci.ess.sr.service.safetyreportquery.stubs.QuerySafetyReportsRequest params) throws RemoteException, gov.nih.nci.ess.sr.service.safetyreportquery.stubs.types.SafetyReportException {
    gov.nih.nci.ess.sr.service.safetyreportquery.stubs.QuerySafetyReportsResponse boxedResult = new gov.nih.nci.ess.sr.service.safetyreportquery.stubs.QuerySafetyReportsResponse();
    boxedResult.setDSET_SafetyReportVersion(impl.querySafetyReports(params.getSafetyReportVersion().getSafetyReportVersion()));
    return boxedResult;
  }

    public gov.nih.nci.ess.sr.service.safetyreportquery.stubs.GetSafetyReportsResponse getSafetyReports(gov.nih.nci.ess.sr.service.safetyreportquery.stubs.GetSafetyReportsRequest params) throws RemoteException, gov.nih.nci.ess.sr.service.safetyreportquery.stubs.types.SafetyReportException {
    gov.nih.nci.ess.sr.service.safetyreportquery.stubs.GetSafetyReportsResponse boxedResult = new gov.nih.nci.ess.sr.service.safetyreportquery.stubs.GetSafetyReportsResponse();
    boxedResult.setDSET_SafetyReportVersion(impl.getSafetyReports(params.getSafetyReportIdentifiers().getDSET_II()));
    return boxedResult;
  }

    public gov.nih.nci.ess.sr.service.safetyreportquery.stubs.ViewSafetyReportResponse viewSafetyReport(gov.nih.nci.ess.sr.service.safetyreportquery.stubs.ViewSafetyReportRequest params) throws RemoteException, gov.nih.nci.ess.sr.service.safetyreportquery.stubs.types.SafetyReportException {
    gov.nih.nci.ess.sr.service.safetyreportquery.stubs.ViewSafetyReportResponse boxedResult = new gov.nih.nci.ess.sr.service.safetyreportquery.stubs.ViewSafetyReportResponse();
    boxedResult.setSafetyReportFormat(impl.viewSafetyReport(params.getSafetyReportIdentifier().getId(),params.getDesiredFormat().getST()));
    return boxedResult;
  }

}