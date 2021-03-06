/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.testdata.loader.ae;

import gov.nih.nci.cabig.caaers.api.impl.AdverseEventManagementServiceImpl;
import gov.nih.nci.cabig.caaers.integration.schema.common.WsError;
import gov.nih.nci.cabig.caaers.testdata.TestDataFileUtils;
import gov.nih.nci.cabig.caaers.testdata.loader.DataLoader;
import gov.nih.nci.cabig.caaers.utils.XmlValidator;
import gov.nih.nci.cabig.caaers.integration.schema.manageae.AdverseEventsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author: Biju Joseph
 */
public class AdverseEventLoader extends DataLoader{

    AdverseEventManagementServiceImpl service;

    public AdverseEventLoader(ApplicationContext appContext) throws Exception {
        this(appContext, TestDataFileUtils.getAdverseEventTestDataFolder().getPath());
    }
    public AdverseEventLoader(ApplicationContext appContext, String loc ) throws Exception {
        super(appContext, loc, "gov.nih.nci.cabig.caaers.integration.schema.adverseevent");
        service = (AdverseEventManagementServiceImpl) appContext.getBean("adverseEventManagementServiceImpl") ;
    }

    @Override
    public boolean loadFile(File f, StringBuffer detailsBuffer) throws Exception {

        //validate
//        boolean valid = XmlValidator.validateAgainstSchema(TestDataFileUtils.getContent(f), "classpath:schema/integration/ManageAdverseEventsSchema.xsd", detailsBuffer);
//        if(!valid) return false;

        boolean loadStatus = true;
        CaaersServiceResponse caaersResponse = service.createAdverseEvent(getAdverseEventInput(f));
        for(WsError wsError : caaersResponse.getServiceResponse().getWsError()){
            loadStatus=false;
            detailsBuffer.append(wsError.getErrorDesc()).append("\n");
        }
        if(StringUtils.isNotEmpty(caaersResponse.getServiceResponse().getResponsecode())) loadStatus = false;
        return loadStatus;

    }


    /**
     * Will read the adverse event message from the file.
     * @param f
     * @return
     */
    public AdverseEventsInputMessage getAdverseEventInput(File f) throws Exception{
         return (AdverseEventsInputMessage)unmarshaller.unmarshal(new FileInputStream(f));
    }

}
