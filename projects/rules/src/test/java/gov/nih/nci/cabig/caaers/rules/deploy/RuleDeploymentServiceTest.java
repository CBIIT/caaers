package gov.nih.nci.cabig.caaers.rules.deploy;

import gov.nih.nci.cabig.caaers.rules.RulesTestCase;

public class RuleDeploymentServiceTest extends RulesTestCase {
    RuleDeploymentServiceImpl deploymentService;

    @Override
    public Class<? extends RulesTestCase> getTestClass() {
        return RuleDeploymentServiceTest.class;
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentService = new RuleDeploymentServiceImpl();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLogin() {
        fail("Not yet implemented");
    }

    public void testConfigureRepository() {
        fail("Not yet implemented");
    }

    public void testRegisterRuleSet() {
        fail("Not yet implemented");
    }

    public void testDeregisterRuleSet() {
        fail("Not yet implemented");
    }

    public void testListRegistrations() {
        fail("Not yet implemented");
    }

    public void testRegisterRuleXml() throws Exception {
        String xml = getFileContext("test_rule.xml");
        String bindUri = "gov.nih.nci.cabig.caaers.rules.reporting.description_section";
        deploymentService.registerRuleXml(bindUri, xml);

    }

}
