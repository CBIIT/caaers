package gov.nih.nci.cabig.caaers.web.fields;

import gov.nih.nci.cabig.caaers.CaaersTestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class CompositeFieldTest extends CaaersTestCase {
    private DefaultInputFieldGroup group;

    private CompositeField field;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        group = new DefaultInputFieldGroup(null, "Group display name");

        field = new CompositeField("root", group);
    }

    public void testDisplayNameIsGroupDisplayName() throws Exception {
        assertEquals(group.getDisplayName(), field.getDisplayName());
    }

    public void testSubfieldPropertyNames() throws Exception {
        group.setFields(Collections.<InputField> singletonList(InputFieldFactory.createTextField(
                        "field", "DC", true)));

        List<InputField> subfields = field.createSubfields();
        assertEquals("Wrong number of subfields", 1, subfields.size());
        assertEquals("Wrong name for subfield", "root.field", subfields.get(0).getPropertyName());
    }

    public void testSubfieldPropertyNamesWhenPropertyNameIsNull() throws Exception {
        group.setFields(Collections.<InputField> singletonList(InputFieldFactory.createTextField(
                        "field", "DC", true)));
        field.setPropertyName(null);

        List<InputField> subfields = field.createSubfields();
        assertEquals("Wrong number of subfields", 1, subfields.size());
        assertEquals("Wrong name for subfield", "field", subfields.get(0).getPropertyName());
    }
}
