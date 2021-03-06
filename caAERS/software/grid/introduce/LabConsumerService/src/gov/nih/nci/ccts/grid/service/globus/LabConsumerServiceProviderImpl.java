/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ccts.grid.service.globus;

import gov.nih.nci.ccts.grid.service.LabConsumerServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the LabConsumerServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class LabConsumerServiceProviderImpl{
	
	LabConsumerServiceImpl impl;
	
	public LabConsumerServiceProviderImpl() throws RemoteException {
		impl = new LabConsumerServiceImpl();
	}
	

    public gov.nih.nci.ccts.grid.stubs.LoadLabsResponse loadLabs(gov.nih.nci.ccts.grid.stubs.LoadLabsRequest params) throws RemoteException {
    gov.nih.nci.ccts.grid.stubs.LoadLabsResponse boxedResult = new gov.nih.nci.ccts.grid.stubs.LoadLabsResponse();
    boxedResult.setAcknowledgementMessage(impl.loadLabs(params.getLoadLabsRequest().getLoadLabsRequest()));
    return boxedResult;
  }

}
