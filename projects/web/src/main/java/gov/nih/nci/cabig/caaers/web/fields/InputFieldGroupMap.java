package gov.nih.nci.cabig.caaers.web.fields;

import org.apache.commons.collections15.map.LazyMap;

import java.util.LinkedHashMap;

/**
 * @author Rhett Sutphin
 */
public class InputFieldGroupMap extends LazyMap<String, InputFieldGroup> {
    public InputFieldGroupMap() {
        super(new LinkedHashMap<String, InputFieldGroup>(), new RepeatingFieldGroupTransformer());
    }

    public static InputFieldGroupMap create(InputFieldGroup... groups) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        for (InputFieldGroup group : groups)
            map.addInputFieldGroup(group);
        return map;
    }

    @SuppressWarnings("unchecked")
    public void addInputFieldGroup(InputFieldGroup group) {
        map.put(group.getName(), group);
    }

    public void addRepeatingFieldGroupFactory(RepeatingFieldGroupFactory groupFactory) {
        addRepeatingFieldGroupFactory(groupFactory, 0);
    }

    /** Add a {@link RepeatingFieldGroupFactory} plus some number of existing field groups */
    public void addRepeatingFieldGroupFactory(RepeatingFieldGroupFactory groupFactory, int count) {
        ((RepeatingFieldGroupTransformer) transformer).addFactory(groupFactory);
        for (int i = 0; i < count; i++)
            addInputFieldGroup(groupFactory.createGroup(i));
    }
}
