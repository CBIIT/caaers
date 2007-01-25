package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.domain.Study;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Sujith Vellat Thayyilthodi
 * @author Rhett Sutphin
 */
public class StudyDaoTest extends DaoTestCase<StudyDao>{
    
    public void testGet() throws Exception {
        Study loaded = getDao().getById(-2);
        assertNotNull("Study not found", loaded);
        assertEquals("Short Title", loaded.getShortTitle());
    }
    
    public void testGetByGridId() throws Exception{
        Study study = getDao().getByGridId("gridStudy");
        assertNotNull("Study not found", study);
    }    
    
    public void testSave() throws Exception {
        Integer savedId;
        {
            Study newStudy = new Study();
            newStudy.setShortTitle("Short Title Inserted");
            newStudy.setLongTitle("Long Title Inserted");            
           // newStudy.setPrincipalInvestigatorCode("ICODE_101");
           // newStudy.setPrincipalInvestigatorName("Investigator Name Inserted");
            newStudy.setMultiInstitutionIndicator(Boolean.FALSE);
            newStudy.setPrimarySponsorCode("SCODE_101");
          //  newStudy.setPrimarySponsorName("Sponsor Name Inserted");
            getDao().save(newStudy);
            assertNotNull("No ID for newly saved study", newStudy.getId());
            savedId = newStudy.getId();
        }

        interruptSession();

        {
            Study reloaded = getDao().getById(savedId);
            assertNotNull("Saved Study not found", reloaded);
           // assertEquals("ICODE_101", reloaded.getPrincipalInvestigatorCode());
           // assertEquals("Investigator Name Inserted", reloaded.getPrincipalInvestigatorName());
            assertEquals("SCODE_101", reloaded.getPrimarySponsorCode());
          //  assertEquals("Sponsor Name Inserted", reloaded.getPrimarySponsorName());
        }
    }

    public void testGetBySubnameMatchesShortTitle() throws Exception {
        List<Study> actual = getDao().getBySubnames(new String[] { "orter" });
        assertEquals("Wrong number of matches", 1, actual.size());
        assertEquals("Wrong match", -3, (int) actual.get(0).getId());
    }

    public void testGetBySubnameMatchesLongTitle() throws Exception {
        List<Study> actual = getDao().getBySubnames(new String[] { "long" });
        assertEquals("Wrong number of matches", 1, actual.size());
        assertEquals("Wrong match", -2, (int) actual.get(0).getId());
    }
    
    public void testGetBySubnameMatchesIntersectionOfSubnames() throws Exception {
        List<Study> actual = getDao().getBySubnames(new String[] { "long", "title" });
        assertEquals("Wrong number of matches", 1, actual.size());
        assertEquals("Wrong match", -2, (int) actual.get(0).getId());
    }
    
    public void testSearchByExactExample() throws Exception {
        Study example = new Study();
        example.setDescription("Description");

        List<Study> actual = getDao().searchByExample(example, false);
        assertEquals("Wrong number of matches", 1, actual.size());
        assertEquals("Wrong match", -2, (int) actual.get(0).getId());
    }

    public void testSearchByWildcardExample() throws Exception {
        Study example = new Study();
        example.setShortTitle("orte");

        List<Study> actual = getDao().searchByExample(example, true);
        assertEquals("Wrong number of matches", 2, actual.size());
        Set<Integer> ids = new HashSet<Integer>();
        for (Study study : actual) ids.add(study.getId());
        assertTrue(ids.contains(-3));
        assertTrue(ids.contains(-4));
    }
}
