/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.expeditedfields;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class LabsDisplayNameCreatorTest extends TestCase {
    private LabsDisplayNameCreator creator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        creator = new LabsDisplayNameCreator();
    }

    public void testIndexedName() throws Exception {
        assertEquals("Lab A", creator.createIndexedName(0));
        assertEquals("Lab E", creator.createIndexedName(4));
        assertEquals("Lab Z", creator.createIndexedName(25));
    }
}
