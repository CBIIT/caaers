package gov.nih.nci.cabig.ctms.grid.ae.service;



import java.rmi.RemoteException;
import gov.nih.nci.cabig.ctms.grid.ae.common.AdverseEventConsumerI;

import java.rmi.RemoteException;

import org.globus.wsrf.config.ContainerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class AdverseEventConsumerImpl extends AdverseEventConsumerImplBase {

	
    private static final String SPRING_CLASSPATH_EXPRESSION = "springClasspathExpression";

    private static final String DEFAULT_SPRING_CLASSPATH_EXPRESSION = "classpath:applicationContext-grid-ae.xml";

    private static final String CONSUMER_BEAN_NAME = "adverseEventConsumerBeanName";

    private static final String DEFAULT_CONSUMER_BEAN_NAME = "adverseEventConsumer";

    private AdverseEventConsumerI consumer;

    public AdverseEventConsumerImpl() throws RemoteException {
        super();
        String exp = ContainerConfig.getConfig().getOption(SPRING_CLASSPATH_EXPRESSION,
                        DEFAULT_SPRING_CLASSPATH_EXPRESSION);
        String bean = ContainerConfig.getConfig().getOption(CONSUMER_BEAN_NAME,
                        DEFAULT_CONSUMER_BEAN_NAME);
        ApplicationContext ctx = new ClassPathXmlApplicationContext(exp);
        this.consumer = (AdverseEventConsumerI) ctx.getBean(bean);
    }
	
  public void register(aenotification.AENotificationType aeNotification) throws RemoteException {
	  this.consumer.register(aeNotification);
  }

}

