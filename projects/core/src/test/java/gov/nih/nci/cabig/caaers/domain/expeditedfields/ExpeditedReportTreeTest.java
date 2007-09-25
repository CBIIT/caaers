package gov.nih.nci.cabig.caaers.domain.expeditedfields;

import gov.nih.nci.cabig.caaers.CaaersError;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.Lab;
import gov.nih.nci.cabig.caaers.domain.DiseaseHistory;
import gov.nih.nci.cabig.caaers.domain.CtepStudyDisease;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class ExpeditedReportTreeTest extends TestCase {
    private ExpeditedReportTree tree = new ExpeditedReportTree();

    public void testAllPropertiesImpliedByTreeExistInModel() throws Exception {
        assertChildPropertiesExist(tree, ExpeditedAdverseEventReport.class);
    }

    @SuppressWarnings({ "RawUseOfParameterizedType" })
    private void assertChildPropertiesExist(TreeNode node, Class nodePropertyClass) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(nodePropertyClass);
        for (TreeNode child : node.getChildren()) {
            String childPropName = child.getPropertyName();
            if (childPropName == null) {
                // this child does not map to a property -- push down
                assertChildPropertiesExist(child, nodePropertyClass);
            } else {
                if (childPropName.indexOf('[') >= 0) {
                    childPropName = childPropName.substring(0, childPropName.indexOf('['));
                }
                // look for matching property
                boolean found = false;
                for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                    if (descriptor.getName().equals(childPropName)) {
                        // found matching descriptor; recurse
                        assertChildPropertiesExist(child, getPropertyType(descriptor));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    fail("Did not find property " + childPropName
                        + " in " + nodePropertyClass.getSimpleName()
                        + ".  Properties: " + listNames(beanInfo.getPropertyDescriptors())
                    );
                }
                // check for "other" property, if applicable
                if (child instanceof CodedOrOtherPropertyNode) {
                    boolean otherFound = false;
                    CodedOrOtherPropertyNode codedOrOther = ((CodedOrOtherPropertyNode) child);
                    for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                        otherFound = pd.getName().equals(codedOrOther.getOtherPropertyName());
                        if (otherFound) break;
                    }
                    if (!otherFound) {
                        fail("Did not find property " + codedOrOther.getOtherPropertyName()
                            + " ('other' for coded " + childPropName + ')'
                            + " in " + nodePropertyClass.getSimpleName()
                            + ".  Properties: " + listNames(beanInfo.getPropertyDescriptors())
                        );
                    }
                }
            }
        }
    }

    public void testFindSimplePropertyNode() throws Exception {
        TreeNode found = tree.find("reporter");
        assertNotNull(found);
        assertEquals("Reporter details", found.getDisplayName());
    }

    public void testFindNestedPropertyNode() throws Exception {
        TreeNode found = tree.find("radiationIntervention.description");
        assertNotNull(found);
        assertEquals("Treatment arm description", found.getDisplayName());
    }

    public void testFindListPropertyChildNode() throws Exception {
        TreeNode found = tree.find("diseaseHistory.metastaticDiseaseSites[].codedSite");
        assertNotNull(found);
        assertEquals("Site name", found.getDisplayName());
    }

    public void testNameForPrimaryAdverseEvent() throws Exception {
        assertEquals("Primary adverse event", tree.find("adverseEvents").getDisplayName(0));
    }

    public void testGetNodeForSection() throws Exception{
        TreeNode node = tree.getNodeForSection(ExpeditedReportSection.ADDITIONAL_INFO_SECTION);
        assertEquals("Name should be ",node.getDisplayName(), ExpeditedReportSection.ADDITIONAL_INFO_SECTION.name());
    }

    public void testSimplePropertyIsSatisfied() throws Exception {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();

        assertUnsatisfiedProperties("Reported present for null prop",
            "reporter.lastName", report, "reporter.lastName");
        report.setReporter(new Reporter());
        assertUnsatisfiedProperties("Reported present for null prop",
            "reporter.lastName", report, "reporter.lastName");
        report.getReporter().setLastName("Mendoza");
        assertNoUnsatisfiedProperties("Reported not present when present",
            "reporter.lastName", report);
    }

    public void testListPropertyIsSatisfied() throws Exception {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();

     /*   assertNoUnsatisfiedProperties("Reported not present for empty list",
            "labs[].name", report);*/
        report.addLab(new Lab());
        assertUnsatisfiedProperties("Reported present for null prop",
            "labs[].name", report, "labs[0].name");
        report.getLabs().get(0).setName("Eliza");
        assertNoUnsatisfiedProperties(
            "Reported not present for set prop",
            "labs[].name", report);
    }

    public void testListPropertyIsNotSatisfiedWhenOneInstanceIsMissing() throws Exception {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();

//        assertNoUnsatisfiedProperties("Reported not present for empty list",
//            "labs[].name", report);
        report.getLabs().get(0).setName("Eliza");
        report.getLabs().get(1).setName(null);
        assertUnsatisfiedProperties(
            "Wrong unsatisfied properties found",
            "labs[].name", report, "labs[1].name");
    }

    public void testCodedOrOtherSatisfiedByCoded() throws Exception {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();

        assertUnsatisfiedProperties("Should be initially unsatisfied",
            "diseaseHistory.ctepStudyDisease", report, "diseaseHistory.ctepStudyDisease");

        report.setDiseaseHistory(new DiseaseHistory());
        report.getDiseaseHistory().setCtepStudyDisease(new CtepStudyDisease());
        assertNoUnsatisfiedProperties("Coded didn't satisfy it",
            "diseaseHistory.ctepStudyDisease", report);
    }

    public void testCodedOrOtherSatisfiedByOther() throws Exception {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();

        assertUnsatisfiedProperties("Should be initially unsatisfied",
            "diseaseHistory.ctepStudyDisease", report, "diseaseHistory.ctepStudyDisease");

        report.setDiseaseHistory(new DiseaseHistory());
        report.getDiseaseHistory().setOtherPrimaryDisease("Hoolitis");
        assertNoUnsatisfiedProperties("Other didn't satisfy it",
            "diseaseHistory.ctepStudyDisease", report);
    }

    private void assertNoUnsatisfiedProperties(String msg, String expectedProp, ExpeditedAdverseEventReport report) {
        assertUnsatisfiedProperties(msg, expectedProp, report);
    }

    private void assertUnsatisfiedProperties(
        String msg, String expectedProp, ExpeditedAdverseEventReport report,
        String... expectedUnsatisfiedProperties
    ) {
        List<UnsatisfiedProperty> actualUnsatisfied = tree.verifyPropertiesPresent(expectedProp, report);
        assertEquals(msg + ": Wrong number of unsatisfied props: " + actualUnsatisfied,
            expectedUnsatisfiedProperties.length, actualUnsatisfied.size());
        for (int i = 0; i < expectedUnsatisfiedProperties.length; i++) {
            String expected = expectedUnsatisfiedProperties[i];
            assertEquals(msg + ": Mismatched prop", expected, actualUnsatisfied.get(i).getBeanPropertyName());
        }
    }

    /**
     * Figures out the next domain object type down from the descriptor given.
     */
    private Class<?> getPropertyType(PropertyDescriptor descriptor) {
        if (Map.class.isAssignableFrom(descriptor.getPropertyType())) {
            Type returnType = descriptor.getReadMethod().getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[1];
            } else {
                fail("Could not extract type of value for map property " + descriptor.getName());
            }
        } else if (List.class.isAssignableFrom(descriptor.getPropertyType())) {
            Type returnType = descriptor.getReadMethod().getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
            } else {
                fail("Could not extract type of value for list property " + descriptor.getName());
            }
        } else {
            return descriptor.getPropertyType();
        }
        throw new CaaersError("That's unpossible");
    }

    private List<String> listNames(PropertyDescriptor[] propertyDescriptors) {
        List<String> names = new ArrayList<String>(propertyDescriptors.length);
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            names.add(descriptor.getName());
        }
        return names;
    }
}
