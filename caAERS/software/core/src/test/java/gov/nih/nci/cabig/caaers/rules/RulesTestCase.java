/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.rules;

import gov.nih.nci.cabig.caaers.AbstractNoSecurityTestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;

import com.semanticbits.rules.utils.RepositoryCleaner;
import com.semanticbits.rules.utils.RulesPropertiesFileLoader;

public abstract class RulesTestCase extends AbstractNoSecurityTestCase {
	RulesPropertiesFileLoader propertiesBean;

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        propertiesBean = RulesPropertiesFileLoader.getLoadedInstance();
        String url = propertiesBean.getProperties().getProperty("rules.repository");
        new RepositoryCleaner(url);
    }

    public abstract Class<? extends TestCase> getTestClass();

    public String getFileContext(String fileName) throws Exception {
        File testFile = new ClassPathResource("/gov/nih/nci/cabig/caaers/rules/deploy/" + fileName).getFile();
        BufferedReader ds = new BufferedReader(new FileReader(testFile));
        String line = null;
        StringBuffer xml = new StringBuffer();
        while ((line = ds.readLine()) != null) {
            xml.append(line);
        }
        assertTrue("Content of the xml should not be null", xml.toString().length() > 0);
        return xml.toString();
    }
    
  
}
