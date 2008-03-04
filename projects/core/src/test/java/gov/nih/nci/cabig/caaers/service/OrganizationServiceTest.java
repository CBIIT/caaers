package gov.nih.nci.cabig.caaers.service;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import gov.nih.nci.cabig.caaers.CaaersTestCase;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.acegi.csm.authorization.CSMObjectIdGenerator;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author Rhett Sutphin
 */
public class OrganizationServiceTest extends CaaersTestCase {
    private static final String APP_NAME = "ZAMO";

    private static final String SITE_ROLE_ID = "ARB";

    private static final String SITE_PG_ID = "LAMBDA";

    private OrganizationServiceImpl service;

    private OrganizationDao organizationDao;

    private CSMObjectIdGenerator idGenerator;

    private UserProvisioningManager userProvisioningManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        organizationDao = registerDaoMockFor(OrganizationDao.class);
        idGenerator = registerMockFor(CSMObjectIdGenerator.class);
        userProvisioningManager = registerMockFor(UserProvisioningManager.class);

        service = new OrganizationServiceImpl();
        service.setOrganizationDao(organizationDao);
        service.setCsmApplicationContextName(APP_NAME);
        service.setSiteAccessRoleId(SITE_ROLE_ID);
        service.setSiteProtectionGroupId(SITE_PG_ID);
        service.setSiteObjectIdGenerator(idGenerator);
        service.setUserProvisioningManager(userProvisioningManager);
    }

    public void testCreate() throws Exception {
        Organization toCreate = new Organization();
        expectCreate(toCreate);

        replayMocks();
        service.create(toCreate);
        verifyMocks();
    }

    public void testCreateOrUpdateWhenCreating() throws Exception {
        Organization toCreate = new Organization();
        expectCreate(toCreate);

        replayMocks();
        service.createOrUpdate(toCreate);
        verifyMocks();
    }

    public void testCreateOrUpdateWhenUpdating() throws Exception {
        Organization toUpdate = Fixtures.setId(5, new Organization());
        organizationDao.save(toUpdate);

        replayMocks();
        service.createOrUpdate(toUpdate);
        verifyMocks();
    }

    private Organization expectCreate(Organization toCreate) throws CSObjectNotFoundException,
                    CSTransactionException {
        String expectedGeneratedId = "a great ID";

        expect(idGenerator.generateId(toCreate)).andReturn(expectedGeneratedId);
        expect(userProvisioningManager.getApplication(APP_NAME)).andReturn(new Application());
        expect(userProvisioningManager.getProtectionGroupById(SITE_PG_ID)).andReturn(
                        new ProtectionGroup());

        // TODO: Ideally these would test the properties of the created CSM elts, but I'm in a
        // hurry. RMS20071012.
        userProvisioningManager.createGroup(saveableGroup(4));
        userProvisioningManager.createProtectionGroup(saveablePG(8));
        userProvisioningManager.createProtectionElement(saveablePE(9));
        userProvisioningManager.assignGroupRoleToProtectionGroup(eq("8"), eq("4"),
                        aryEq(new String[] { SITE_ROLE_ID }));

        organizationDao.save(toCreate);
        return toCreate;
    }

    private static Group saveableGroup(final long id) {
        EasyMock.reportMatcher(new NotNullSetCsmIdMatcher(id, Group.class));
        return null;
    }

    private static ProtectionGroup saveablePG(final long id) {
        EasyMock.reportMatcher(new NotNullSetCsmIdMatcher(id, ProtectionGroup.class));
        return null;
    }

    private static ProtectionElement saveablePE(final long id) {
        EasyMock.reportMatcher(new NotNullSetCsmIdMatcher(id, ProtectionElement.class));
        return null;
    }

    private static final class NotNullSetCsmIdMatcher implements IArgumentMatcher {
        private Class<?> csmObjectClass;

        private long desiredId;

        public NotNullSetCsmIdMatcher(long desiredId, Class<?> csmObjectClass) {
            this.csmObjectClass = csmObjectClass;
            this.desiredId = desiredId;
        }

        protected String getIdPropertyName() {
            StringBuilder sb = new StringBuilder(csmObjectClass.getSimpleName()).append("Id");
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            return sb.toString();
        }

        public boolean matches(Object argument) {
            if (argument == null) return false;
            if (csmObjectClass.isAssignableFrom(argument.getClass())) {
                BeanWrapperImpl wrapper = new BeanWrapperImpl(argument);
                wrapper.setPropertyValue(getIdPropertyName(), desiredId);
                return true;
            }
            return false;
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("An unsaved ").append(csmObjectClass.getName());
        }
    }
}
