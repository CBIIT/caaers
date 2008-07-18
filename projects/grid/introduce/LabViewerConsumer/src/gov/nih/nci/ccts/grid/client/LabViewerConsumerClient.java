package gov.nih.nci.ccts.grid.client;

import gov.nih.nci.ccts.grid.common.LabViewerConsumerI;

import java.io.FileInputStream;
import java.rmi.RemoteException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

import services.LoadLabsRequest;
import services.WsError;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class LabViewerConsumerClient extends LabViewerConsumerClientBase implements LabViewerConsumerI {	

	public LabViewerConsumerClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public LabViewerConsumerClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public LabViewerConsumerClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public LabViewerConsumerClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(LabViewerConsumerClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  LabViewerConsumerClient client = new LabViewerConsumerClient(args[1]);
			  java.lang.Object obj = ObjectDeserializer.deserialize(new InputSource(new FileInputStream("/Users/sakkala/tech/caaers/lab-viewer/c3dmessage.xml")),LoadLabsRequest.class);
				
			  
			  services.Acknowledgement ack = client.loadLabs((LoadLabsRequest)obj);
			  
			  System.out.println ("Status " +ack.getStatus());
			  WsError[] errors = ack.getErrors().getWsError();
			  if (errors != null) {
				  for (int i=0;i<errors.length;i++) {
					  System.out.println(errors[i].getErrorCode() + " - " + errors[i].getErrorDesc());
				  }
			  }
			  
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

  public services.Acknowledgement loadLabs(services.LoadLabsRequest loadLabsRequest) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"loadLabs");
    gov.nih.nci.ccts.grid.stubs.LoadLabsRequest params = new gov.nih.nci.ccts.grid.stubs.LoadLabsRequest();
    gov.nih.nci.ccts.grid.stubs.LoadLabsRequestLoadLabsRequest loadLabsRequestContainer = new gov.nih.nci.ccts.grid.stubs.LoadLabsRequestLoadLabsRequest();
    loadLabsRequestContainer.setLoadLabsRequest(loadLabsRequest);
    params.setLoadLabsRequest(loadLabsRequestContainer);
    gov.nih.nci.ccts.grid.stubs.LoadLabsResponse boxedResult = portType.loadLabs(params);
    return boxedResult.getAcknowledgementMessage();
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

}
