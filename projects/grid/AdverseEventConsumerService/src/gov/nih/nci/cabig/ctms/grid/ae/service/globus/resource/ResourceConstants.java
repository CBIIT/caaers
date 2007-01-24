package gov.nih.nci.cabig.ctms.grid.ae.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://ae.grid.ctms.cabig.nci.nih.gov/AdverseEventConsumer";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "AdverseEventConsumerKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "AdverseEventConsumerResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA_MD_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
