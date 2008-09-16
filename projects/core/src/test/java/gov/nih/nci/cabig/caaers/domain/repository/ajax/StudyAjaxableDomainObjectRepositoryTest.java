package gov.nih.nci.cabig.caaers.domain.repository.ajax;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.dao.query.ajax.StudyAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.ajax.StudyAjaxableDomainObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: saurabhagrawal
 * Date: Sep 15, 2008
 * Time: 12:20:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudyAjaxableDomainObjectRepositoryTest extends AbstractTestCase {

    private StudyAjaxableDomainObjectRepository studyAjaxableDomainObjectRepository;
    private HibernateTemplate hibernateTemplate;
    private StudyAjaxableDomainObjectQuery query;
    List<Object[]> objects = new ArrayList<Object[]>();

    Object[] studyWithNoPrimaryIdentifier = new Object[4];
    Object[] studyWithPrimaryIdentifier = new Object[4];
    Object[] studyWithNullValues = new Object[4];
    Object[] matchingStudyWithPrimaryIdentifier = new Object[4];

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        studyAjaxableDomainObjectRepository = new StudyAjaxableDomainObjectRepository();

        hibernateTemplate = registerMockFor(HibernateTemplate.class);
        studyAjaxableDomainObjectRepository.setHibernateTemplate(hibernateTemplate);
        query = new StudyAjaxableDomainObjectQuery();

        studyWithNoPrimaryIdentifier[0] = 1;
        studyWithNoPrimaryIdentifier[1] = "study short title";
        studyWithNoPrimaryIdentifier[2] = "identifier value";
        studyWithNoPrimaryIdentifier[3] = Boolean.FALSE;


        studyWithPrimaryIdentifier[0] = 2;
        studyWithPrimaryIdentifier[1] = "study short title";
        studyWithPrimaryIdentifier[2] = "identifier value";
        studyWithPrimaryIdentifier[3] = Boolean.TRUE;

        matchingStudyWithPrimaryIdentifier[0] = 1;
        matchingStudyWithPrimaryIdentifier[1] = "study short title";
        matchingStudyWithPrimaryIdentifier[2] = "another identifier value";
        matchingStudyWithPrimaryIdentifier[3] = Boolean.TRUE;

        objects.add(studyWithNoPrimaryIdentifier);
        objects.add(studyWithPrimaryIdentifier);
        objects.add(studyWithNullValues);
        objects.add(matchingStudyWithPrimaryIdentifier);

    }

    public void testObjectArrayHasNullValues() {
        objects.clear();
        objects.add(studyWithNullValues);


        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(1, studyAjaxableDomainObjects.size());

        StudyAjaxableDomainObject studyAjaxableDomainObject = studyAjaxableDomainObjects.get(0);
        assertNull(studyAjaxableDomainObject.getId());
        assertNull(studyAjaxableDomainObject.getPrimaryIdentifierValue());
        assertNull(studyAjaxableDomainObject.getShortTitle());


    }

    public void testIfContainsObjectMethodReturnsTrue() {
        objects.clear();
        objects.add(studyWithNoPrimaryIdentifier);
        objects.add(matchingStudyWithPrimaryIdentifier);

        assertEquals(matchingStudyWithPrimaryIdentifier[0], studyWithNoPrimaryIdentifier[0]);
        assertEquals(matchingStudyWithPrimaryIdentifier[1], studyWithNoPrimaryIdentifier[1]);


        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(1, studyAjaxableDomainObjects.size());

        StudyAjaxableDomainObject studyAjaxableDomainObject = studyAjaxableDomainObjects.get(0);
        assertEquals(studyWithNoPrimaryIdentifier[0], studyAjaxableDomainObject.getId());
        assertEquals(matchingStudyWithPrimaryIdentifier[2], studyAjaxableDomainObject.getPrimaryIdentifierValue());
        assertEquals(studyWithNoPrimaryIdentifier[1], studyAjaxableDomainObject.getShortTitle());
    }

    public void testIfContainsObjectMethodReturnsTrue_AddStudiesInReverseOrder() {
        objects.clear();
        objects.add(matchingStudyWithPrimaryIdentifier);
        objects.add(studyWithNoPrimaryIdentifier);

        assertEquals(matchingStudyWithPrimaryIdentifier[0], studyWithNoPrimaryIdentifier[0]);
        assertEquals(matchingStudyWithPrimaryIdentifier[1], studyWithNoPrimaryIdentifier[1]);


        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(1, studyAjaxableDomainObjects.size());

        StudyAjaxableDomainObject studyAjaxableDomainObject = studyAjaxableDomainObjects.get(0);
        assertEquals(studyWithNoPrimaryIdentifier[0], studyAjaxableDomainObject.getId());
        assertEquals(matchingStudyWithPrimaryIdentifier[2], studyAjaxableDomainObject.getPrimaryIdentifierValue());
        assertEquals(studyWithNoPrimaryIdentifier[1], studyAjaxableDomainObject.getShortTitle());
    }

    public void testFindStudyHavingPrimaryIdentifier() {
        objects.clear();
        objects.add(studyWithPrimaryIdentifier);

        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(1, studyAjaxableDomainObjects.size());

        StudyAjaxableDomainObject studyAjaxableDomainObject = studyAjaxableDomainObjects.get(0);
        assertEquals(studyWithPrimaryIdentifier[0], studyAjaxableDomainObject.getId());
        assertEquals(studyWithPrimaryIdentifier[2], studyAjaxableDomainObject.getPrimaryIdentifierValue());
        assertEquals(studyWithPrimaryIdentifier[1], studyAjaxableDomainObject.getShortTitle());
    }

    public void testFindStudyHavingNoPrimaryIdentifier() {
        objects.clear();
        objects.add(studyWithNoPrimaryIdentifier);

        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(1, studyAjaxableDomainObjects.size());

        StudyAjaxableDomainObject studyAjaxableDomainObject = studyAjaxableDomainObjects.get(0);
        assertNull(studyAjaxableDomainObject.getPrimaryIdentifierValue());
        assertEquals(studyWithNoPrimaryIdentifier[0], studyAjaxableDomainObject.getId());
        assertEquals(studyWithNoPrimaryIdentifier[1], studyAjaxableDomainObject.getShortTitle());
    }

    public void testFindStudies() {
        expect(hibernateTemplate.execute(isA(HibernateCallback.class))).andReturn(objects);
        replayMocks();
        List<StudyAjaxableDomainObject> studyAjaxableDomainObjects = studyAjaxableDomainObjectRepository.findStudies(query);
        verifyMocks();
        assertNotNull(studyAjaxableDomainObjects);
        assertEquals(3, studyAjaxableDomainObjects.size());


    }
}
