/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers2adeers.exchnage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

import static gov.nih.nci.cabig.caaers2adeers.exchnage.ExchangePreProcessor.*;

/**
 * This bean will pre-process the messages
 */
public class HeaderGeneratorProcessor implements Processor {

    protected static final Log log = LogFactory.getLog(HeaderGeneratorProcessor.class);

    public void process(Exchange exchange) throws Exception {
        Map<String,Object> headers = exchange.getIn().getHeaders();
        if(!exchange.getProperties().containsKey(CORRELATION_ID)) {
        	exchange.setProperty(CORRELATION_ID, makeCorrelationId());
        }
        for(Map.Entry<String, Object> e : exchange.getProperties().entrySet()){
            if(e.getKey().startsWith("c2a_")) headers.put(e.getKey(), e.getValue());
        }
        if(log.isDebugEnabled()) log.debug("Headers :" + String.valueOf(exchange.getIn().getHeaders()));
    }
    
    public static String makeCorrelationId() {
		return String.valueOf(System.currentTimeMillis()) + RandomStringUtils.randomAlphanumeric(5);
	}

}
