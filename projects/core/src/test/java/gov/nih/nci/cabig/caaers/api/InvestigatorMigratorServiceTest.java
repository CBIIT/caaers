package gov.nih.nci.cabig.caaers.api;

import gov.nih.nci.cabig.caaers.CaaersDbTestCase;
import gov.nih.nci.cabig.caaers.dao.InvestigatorDao;
import gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.SiteInvestigator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class InvestigatorMigratorServiceTest extends CaaersDbTestCase {

	private InvestigatorMigratorService svc = null;
	private JAXBContext jaxbContext = null;
	private Unmarshaller unmarshaller = null;
	private gov.nih.nci.cabig.caaers.integration.schema.investigator.Staff staff = null;
	private File xmlFile = null;
	private InvestigatorDao investigatorDao = null;
	Identifier identifier = null;
	Organization organization = null;
	Investigator updatedInvestigator = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		jaxbContext = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.integration.schema.investigator");
		unmarshaller = jaxbContext.createUnmarshaller();
		svc = (InvestigatorMigratorService)getDeployedApplicationContext().getBean("investigatorMigratorService");
		investigatorDao = (InvestigatorDao)getDeployedApplicationContext().getBean("investigatorDao");
	}

	public void testInvestigatorSave(){
		try {
			//Create or update , whatever it is new data will be populated ..
			//xmlFile = new File ("/Users/sakkala/tech-workspace/caaers12/core/src/test/resources/gov/nih/nci/cabig/caaers/api/testdata/CreateInvestigatorTest.xml");
			xmlFile = getResources("classpath*:gov/nih/nci/cabig/caaers/api/testdata/CreateInvestigatorTest.xml")[0].getFile();
			staff = (gov.nih.nci.cabig.caaers.integration.schema.investigator.Staff)unmarshaller.unmarshal(xmlFile);
			svc.saveInvestigator(staff);	
			
			//update with modified data ..
			//xmlFile = new File ("/Users/sakkala/tech-workspace/caaers12/core/src/test/resources/gov/nih/nci/cabig/caaers/api/testdata/UpdateInvestigatorTest.xml");
			xmlFile = getResources("classpath*:gov/nih/nci/cabig/caaers/api/testdata/UpdateInvestigatorTest.xml")[0].getFile();
			staff = (gov.nih.nci.cabig.caaers.integration.schema.investigator.Staff)unmarshaller.unmarshal(xmlFile);
			svc.saveInvestigator(staff);
			
			updatedInvestigator = fetchInvestigator("sr-1");
			
			assertNotNull(updatedInvestigator);
			
			assertEquals("870-098-7777", updatedInvestigator.getFaxNumber());
			assertEquals("888-098-0099", updatedInvestigator.getPhoneNumber());
			
			
			
			//	update site investigators data ..
			xmlFile = getResources("classpath*:gov/nih/nci/cabig/caaers/api/testdata/UpdateSiteInvestigatorsTest.xml")[0].getFile();
			staff = (gov.nih.nci.cabig.caaers.integration.schema.investigator.Staff)unmarshaller.unmarshal(xmlFile);
			svc.saveInvestigator(staff);
			
			assertNotNull(updatedInvestigator);
			updatedInvestigator = fetchInvestigator("sr-1");
			
			//get site investigators.
			List<SiteInvestigator> siteInvestigators = updatedInvestigator.getSiteInvestigatorsInternal();
			for (SiteInvestigator siteInvestigator:siteInvestigators) {
				if (siteInvestigator.getEmailAddress().equals("jd@dcp.org")) {
					assertEquals("NCI", siteInvestigator.getOrganization().getNciInstituteCode());
				}
				if (siteInvestigator.getEmailAddress().equals("jb@nci.gov")) {
					assertEquals("CTEP", siteInvestigator.getOrganization().getNciInstituteCode());
				}
				//newly added site investigator
				if (siteInvestigator.getEmailAddress().equals("new@nci.gov")) {
					assertEquals("DCP", siteInvestigator.getOrganization().getNciInstituteCode());
				}				
			}
						
			
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error running test: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Error running test: " + e.getMessage());
		}		
	}

	/**
     * Fetches the research staff from the DB
     * 
     * @param nciCode
     * @return
     */
	Investigator fetchInvestigator(String nciIdentifier) {
    	InvestigatorQuery invQuery = new InvestigatorQuery();
        if (StringUtils.isNotEmpty(nciIdentifier)) {
        	invQuery.filterByNciIdentifier(nciIdentifier);
        	
        }
        List<Investigator> rsList = investigatorDao.searchInvestigator(invQuery);
        
        if (rsList == null || rsList.isEmpty()) {
            return null;
        }
        return rsList.get(0);
    }
	
	private static Resource[] getResources(String pattern) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(pattern);
        return resources;
    }
}
