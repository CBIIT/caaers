/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.CaaersDbTestCase;
import gov.nih.nci.cabig.caaers.DaoNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.dao.index.StudyIndexDao;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.index.StudyIndex;

public class StudyIndexDaoTest extends CaaersDbTestCase {
	StudyDao studyDao ; 
	@Override
	protected void setUp() throws Exception {
		// change the security interceptor with stub.
		super.setUp();
		studyDao = (StudyDao)getApplicationContext().getBean("studyDao");
	}
	
	
	public void testSave() throws Exception {
        Study study = studyDao.getById(-2);
        String userName = "srakkala";
        StudyIndex studyIndex = new StudyIndex();
        studyIndex.setLoginId(userName);
        studyIndex.setStudy(study);
    }
}
