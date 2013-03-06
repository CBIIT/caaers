/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.DaoNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.DiseaseCodeTerm;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.TransactionStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.dao.DataAccessException;

import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * @author Mehul Gulati
 * Date: Oct 29, 2009
 */
public class StudyAuditSavedInTransactionTest extends DaoNoSecurityTestCase<StudyDao> {

    private TransactionTemplate transactionTemplate;
    StudyDao studyDao;

    public void setUp() throws Exception {
        super.setUp();
        studyDao = (StudyDao) getDeployedApplicationContext().getBean("studyDao");
        transactionTemplate = (TransactionTemplate) getApplicationContext().getBean(
                "transactionTemplate");
    }

    public void testAuditStudyRollBack() {

        {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    System.out.println("Entering stuff");
                    Study newStudy = new LocalStudy();
                    newStudy.setShortTitle("Short Title Inserted");
                    newStudy.setLongTitle("Long Title Inserted");
                    newStudy.setAeTerminology(Fixtures.createCtcV3Terminology(newStudy));
                    newStudy.getDiseaseTerminology().setDiseaseCodeTerm(DiseaseCodeTerm.CTEP);
                    newStudy.setMultiInstitutionIndicator(Boolean.FALSE);
                    newStudy.setAdeersReporting(Boolean.TRUE);
                    studyDao.save(newStudy);
                }
            });

        }

        interruptSession();
        {


            JdbcTemplate jdbcTemplate = (JdbcTemplate) getDeployedApplicationContext().getBean("jdbcTemplate");
            Integer objectId = (Integer) jdbcTemplate.execute(new StatementCallback() {
                public Object doInStatement(Statement st) throws SQLException,
                        DataAccessException {
                    ResultSet rs = st.executeQuery("select object_id from audit_events where class_name = 'gov.nih.nci.cabig.caaers.domain.LocalStudy'");
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    return null;
                }
            });
            assertNotNull(objectId);
        }


    }



}
