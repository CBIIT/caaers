package gov.nih.nci.cabig.caaers.rules.business.service;

import com.semanticbits.rules.api.RuleAuthoringService;
import com.semanticbits.rules.brxml.*;
import com.semanticbits.rules.brxml.RuleSet;
import com.semanticbits.rules.utils.RuleUtil;
import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.rules.common.CategoryConfiguration;
import gov.nih.nci.cabig.caaers.rules.common.RuleType;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CaaersRulesEngineService Tester.
 *
 * @author Biju Joseph
 * @since <pre>03/08/2010</pre>
 * 
 */
public class CaaersRulesEngineServiceTest extends AbstractTestCase {
    

    CaaersRulesEngineService service;
    RuleAuthoringService ruleAuthoringService;

    public void setUp() throws Exception {
        super.setUp();
        service = new CaaersRulesEngineService();
        ruleAuthoringService = registerMockFor(RuleAuthoringService.class);
        service.setRuleAuthoringService(ruleAuthoringService);
    }

    //tests that call is delegated properly to rules engine.
    public void testGetFieldRuleSet(){
        String pName = "gov.nih.nci.cabig.caaers.rules.field_rules";
        EasyMock.expect(ruleAuthoringService.getRuleSet(pName, true)).andReturn(null);
        replayMocks();
        service.getFieldRuleSet(RuleType.FIELD_LEVEL_RULES.getName());
        verifyMocks();
    }


    //tests that call is delegated properly to rules engine.
    public void testGetFieldRuleSetNotFromCache(){
        String pName = "gov.nih.nci.cabig.caaers.rules.field_rules";
        EasyMock.expect(ruleAuthoringService.getRuleSet(pName, false)).andReturn(null);
        replayMocks();
        service.getFieldRuleSet(RuleType.FIELD_LEVEL_RULES.getName(), false);
        verifyMocks();
    }

    //checks that non ui related conditions are filtered off. 
    public void testCleanRuleSet(){
        RuleSet rs = new RuleSet();

        ArrayList<Rule> rules = new ArrayList<Rule>();
        rs.setRule(rules);
        rules.add(Fixtures.createRule(Fixtures.createCondition("abcd", "studySDO", "hello", "organizationSDO", "jay", "factResolver", "adverseEventEvaluationResult", "thankyou")));

        service.cleanRuleSet(rs);

        assertEquals("abcd", rules.get(0).getCondition().getColumn().get(0).getIdentifier());
        assertEquals("hello", rules.get(0).getCondition().getColumn().get(1).getIdentifier());
        assertEquals("jay", rules.get(0).getCondition().getColumn().get(2).getIdentifier());
        assertEquals("thankyou", rules.get(0).getCondition().getColumn().get(3).getIdentifier());
    }


    //pouplates redable attribute in rules.
    // BJ - tests for 3 types of action. 
    public void testMakeRuleSetReadable(){
       RuleSet rs = new RuleSet();

       ArrayList<Rule> rules = new ArrayList<Rule>();
       rs.setRule(rules);
       Rule r1 = Fixtures.createRule(Fixtures.createCondition("abc", "def"));
       Rule r2  = Fixtures.createRule(Fixtures.createCondition("ghi"));
       Rule r3 = Fixtures.createRule(Fixtures.createCondition("field"));
       rules.add(r1);
       rules.add(r2);
       rules.add(r3);

       r1.setAction(Arrays.asList("Hello", "Below"));
       r2.setAction(Arrays.asList("NA"));
       r3.setAction(Arrays.asList(ExpeditedReportSection.ADDITIONAL_INFO_SECTION.name()));
        
       {
           RuleSet rs1 = new RuleSet();
           rs1.setDescription("SAE Reporting Rules");
           rs1.setRule(Arrays.asList(r1,r2, r3));

           service.makeRuleSetReadable(rs1);

           assertCorrectValuesInList(r1.getReadableRule().getLine(), "If", "\t &nbsp;&nbsp;&nbsp; prefix is  'B' ", "And","\t &nbsp;&nbsp;&nbsp; prefix is  'B' ");
           assertCorrectValuesInList(r1.getReadableAction(), "Hello", "Below");
           assertEquals("Rule-1", r1.getMetaData().getName());
           assertEquals("Rule-3", r3.getMetaData().getName());
       }
        

       {
           RuleSet rs1 = new RuleSet();
           rs1.setDescription("Mandatory Sections Rules");
           rs1.setRule(Arrays.asList(r3));

           service.makeRuleSetReadable(rs1);

           assertCorrectValuesInList(r2.getReadableRule().getLine(), "If", "\t &nbsp;&nbsp;&nbsp; prefix is  'B' ");
           assertCorrectValuesInList(r2.getReadableAction(), "NA");

       }


       {
           RuleSet rs1 = new RuleSet();
           rs1.setDescription("Field Rules");
           rs1.setRule(Arrays.asList(r2));

           service.makeRuleSetReadable(rs1);

           assertCorrectValuesInList(r3.getReadableRule().getLine(), "If", "\t &nbsp;&nbsp;&nbsp; prefix is  'B' ");
           assertCorrectValuesInList(r3.getReadableAction(), "Additional Info");
           assertEquals("Rule-1", r3.getMetaData().getName());

       }

    }

    public void testGetRuleSetByPackageName(){
        fail("BJ: todo.") ;
    }

    public void testConstructPackageName(){
        fail("Bj todo");
    }

    public void testDeleteRule(){
        fail("todo implement it");
    }

    public void testGeneratePath(){
        fail("to do");
    }

    public void testPopulateCategoryBasedColumns(){
       fail("to do");
    }

    

    public void testParseRuleLevel(){
      String c = service.parseRuleLevel("gov.nih.nci.cabig.caaers.rules.sponsor.ORG_22.STU_99.odododo") ;
      assertEquals("gov.nih.nci.cabig.caaers.rules.sponsor", c);
      assertNull(service.parseRuleLevel("junki"));
    }


    public void testParseOrganizationId(){
        String orgId = service.parseOrganizationId("gov.nih.nci.cabig.caaers.rules.sponsor.ORG_11.STU_33.abcdefg") ;
        assertEquals("11", orgId);
        assertNull(service.parseOrganizationId("test"));
    }

    public void testParseStudyId(){
        String stuId = service.parseOrganizationId("gov.nih.nci.cabig.caaers.rules.sponsor.ORG_11.STU_33.abcdefg") ;
        assertEquals("33", stuId);
        assertNull(service.parseOrganizationId("test"));
    }

    public void assertCorrectValuesInList(List<String> list, String... values){
        int i =0;
        for(String v : values){
            assertEquals("[" + v +"]", "[" + list.get(i) + "]");
            i++;
        }
    }

}
