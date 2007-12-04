package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.CaaersSystemException;

/**
 * Created by IntelliJ IDEA. User: admin Date: Sep 14, 2007 Time: 4:59:56 PM To change this template use File | Settings | File Templates.
 */
public interface UserService {

    // public void saveResearchStaff(ResearchStaff staff) throws CaaersSystemException;
    
    /**
     * Create the csm users and groups for newly created research staff or updates csm users/groups if research staff already exists.
     * @throws CaaersSystemException if CSM user can not be created.
     * @param researchStaff the research staff for which csm users and groups to be created or updated
     */
    void createOrUpdateCSMUserAndGroupsForResearchStaff(gov.nih.nci.cabig.caaers.domain.ResearchStaff researchStaff);
    
    public User getUserByName(String userName);
    public void userChangePassword(String userName, String password, int maxHistorySize);
    public boolean userHasPassword(String userName, String password);
    public boolean userHadPassword(String userName, String password);
    public String userCreateToken(String userName);
}
