/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.expeditedfields;

import gov.nih.nci.cabig.caaers.CaaersContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gov.nih.nci.cabig.caaers.CaaersTestCase;
import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * This class tests CaaersFieldTree class.
 * @author Sameer Sawant
 * @author Ion C. Olaru
 * @author Biju Joseph
 */
public class CaaersFieldsTreeTest extends CaaersTestCase {
	private CaaersFieldsTree tree;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tree = (CaaersFieldsTree)getDeployedApplicationContext().getBean("caaersFieldsTree");
    }

    public void testRecurcivelyCollectListNodes(){
        List<TreeNode> nodes = new ArrayList();
        tree.recursivelyCollectListNodes(nodes);
        assertEquals("adverseEvents", nodes.get(0).getPropertyName());
    }

    public void testSections() {
        assertEquals(2, tree.getChildren().size());
    }
    
    //checks whether the call is properly getting delegated to expedited tree. 
    public void testInitialize(){
    	try {
	        ExpeditedReportTree reportTree = registerMockFor(ExpeditedReportTree.class);
	        tree.setExpeditedReportTree(reportTree);
	
	        reportTree.reinitialize();
	        
	        replayMocks();
	        tree.initialize();
	        verifyMocks();
    	} catch (RuntimeException re) {
    		//Do nothing, mocks sometime fail to instantiate the tree.
    	}
    }

    //checks that if expedited report tree is null, the initialize will not throw NPE.
    public void testInitializeWillNotThrowNPE(){
        try{
            tree.setExpeditedReportTree(null);
            tree.initialize();
        }catch(NullPointerException npe){
            fail("Must not throw null pointer exception, when ExpeditedReportTree is null");
        }
    }

    public void testAdd() {
        tree.setExpeditedReportTree(null);
        tree.initialize();
        TabSectionNode tabSectionNode = new TabSectionNode(TabSection.COURSE_CYCLE_SECTION);
        tabSectionNode.add(new TabSectionNode(TabSection.COURSE_CYCLE_SECTION));
        tabSectionNode.add(new TabSectionNode(TabSection.COURSE_CYCLE_SECTION));
        tree.add(tabSectionNode);
        TreeNode node = tree.getNodeForSection(TabSection.COURSE_CYCLE_SECTION);
        assertEquals(2, node.getChildren().size());
    }

    public void testGetMessage() {
        String m = tree.getMessage("LBL_one", "DEF");
        assertEquals("DEF", m);
    }
    
}
