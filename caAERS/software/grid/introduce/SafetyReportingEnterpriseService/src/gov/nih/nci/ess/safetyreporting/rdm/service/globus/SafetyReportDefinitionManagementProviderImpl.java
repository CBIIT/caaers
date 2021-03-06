/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.safetyreporting.rdm.service.globus;

import gov.nih.nci.ess.safetyreporting.rdm.service.SafetyReportDefinitionManagementImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the SafetyReportingEnterpriseServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class SafetyReportDefinitionManagementProviderImpl{
	
	SafetyReportDefinitionManagementImpl impl;
	
	public SafetyReportDefinitionManagementProviderImpl() throws RemoteException {
		impl = new SafetyReportDefinitionManagementImpl();
	}
	

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionResponse createSafetyReportDefinition(gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionResponse();
    impl.createSafetyReportDefinition(params.getReportDefinition().getReportDefinition());
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsResponse updateSafetyReportDefinitionDetails(gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsResponse();
    impl.updateSafetyReportDefinitionDetails(params.getReportDefinition().getReportDefinition());
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsResponse updateSafetyReportDefinitionDeliveryDetails(gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsResponse();
    impl.updateSafetyReportDefinitionDeliveryDetails(params.getReportDefinition().getReportDefinition());
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsResponse updateSafetyReportDefinitionMandatoryFields(gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsResponse();
    impl.updateSafetyReportDefinitionMandatoryFields(params.getReportDefinition().getReportDefinition());
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionResponse deactivateSafetyReportDefinition(gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionResponse();
    impl.deactivateSafetyReportDefinition(params.getReportDefinitionId().getId(),params.getReasonForDeactivation().getST());
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyResponse updateSafetyReportTerminologyForStudy(gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyResponse boxedResult = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyResponse();
    impl.updateSafetyReportTerminologyForStudy(params.getReportDefinitionId().getId(),params.getStudyId().getId(),params.getReportTerminologyId().getId());
    return boxedResult;
  }

}
