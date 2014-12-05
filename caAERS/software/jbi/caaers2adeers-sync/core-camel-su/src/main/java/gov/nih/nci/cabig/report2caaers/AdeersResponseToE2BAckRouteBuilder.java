/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.report2caaers;

import static gov.nih.nci.cabig.caaers2adeers.track.Tracker.track;
import static gov.nih.nci.cabig.caaers2adeers.track.IntegrationLog.Stage.*;
import gov.nih.nci.cabig.caaers2adeers.Caaers2AdeersRouteBuilder;

public class AdeersResponseToE2BAckRouteBuilder {

	private static String responseXSLBase = "xslt/e2b/response/";

	private String outputEDIDir;	
		

	public void configure(Caaers2AdeersRouteBuilder routeBuilder){

        routeBuilder.from("direct:routeAdEERSResponseSink")
        	.choice()
        		.when().xpath("not(//MESSAGE_COMBO_ID)")
        			.to("direct:donothing")
                .otherwise()
                	.to("direct:processAdEERSResponse");
        
        routeBuilder.from("direct:processAdEERSResponse")
			.processRef("adeersResponseProcessor")
			.to("xslt:" + responseXSLBase + "adeersResponse2ACK.xsl")			
			.process(track(REQUEST_COMPLETION))
			.to(routeBuilder.getFileTracker().fileURI(REQUEST_COMPLETION))
			.to("file://"+outputEDIDir);

	}	

	public String getOutputEDIDir() {
		return outputEDIDir;
	}


	public void setOutputEDIDir(String outputEDIDir) {
		this.outputEDIDir = outputEDIDir;
	}
		
}
