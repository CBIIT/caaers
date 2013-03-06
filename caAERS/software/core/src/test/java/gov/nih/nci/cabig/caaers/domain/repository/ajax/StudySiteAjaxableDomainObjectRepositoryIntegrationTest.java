/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository.ajax;

import gov.nih.nci.cabig.caaers.CaaersDbNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.dao.query.ajax.StudySiteAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.ajax.StudySiteAjaxableDomainObject;

import java.util.List;
/**
 * 
 * @author Biju
 *
 */
public class StudySiteAjaxableDomainObjectRepositoryIntegrationTest extends	CaaersDbNoSecurityTestCase {
	
	StudySiteAjaxableDomainObjectRepository repository;
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = (StudySiteAjaxableDomainObjectRepository)getApplicationContext().getBean("studySiteAjaxableDomainObjectRepository");
	}
	
	public void testFindStudySites(){
		
		StudySiteAjaxableDomainObjectQuery query = new StudySiteAjaxableDomainObjectQuery();
		query.filterByStudy(-2);
		List<StudySiteAjaxableDomainObject> results = repository.findStudySites(query);
		
		assertEquals(results.get(0).getName(), "CALGB");
	}
	
	public void testFindStudySites_NoSuchStudy(){
		
		StudySiteAjaxableDomainObjectQuery query = new StudySiteAjaxableDomainObjectQuery();
		query.filterByStudy(-255);
		List<StudySiteAjaxableDomainObject> results = repository.findStudySites(query);
		
		assertEquals(0, results.size());
	}
}
