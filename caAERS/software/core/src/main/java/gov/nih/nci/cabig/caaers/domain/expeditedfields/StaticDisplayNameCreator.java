/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.expeditedfields;

/**
 * This implementation returns the given display name, unmodified, for every query.
 * 
 * @author Rhett Sutphin
 */
public class StaticDisplayNameCreator implements DisplayNameCreator {
    private String displayName;

    public StaticDisplayNameCreator(String displayName) {
        this.displayName = displayName;
    }

    public String createIndexedName(int i) {
        return displayName;
    }

    public String createGenericName() {
        return displayName;
    }
}
