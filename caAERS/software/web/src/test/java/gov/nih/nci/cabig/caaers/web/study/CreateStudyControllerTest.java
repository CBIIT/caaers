package gov.nih.nci.cabig.caaers.web.study;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.CREATE_STUDY;
import static org.easymock.EasyMock.expect;

import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.repository.ConfigPropertyRepositoryImpl;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.security.SecurityTestUtils;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.validation.validator.WebControllerValidator;
import gov.nih.nci.cabig.caaers.web.WebTestCase;
import gov.nih.nci.cabig.ctms.web.tabs.StaticTabConfigurer;
import org.easymock.classextension.EasyMock;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * @author Kulasekaran
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
@CaaersUseCases({CREATE_STUDY})
public class CreateStudyControllerTest extends WebTestCase {
    MessageSource messageSource;

    private CreateStudyController controller;
    private StudyCommand command;

    protected StudyDao studyDao;
    private OrganizationDao organizationDao;
    private AgentDao agentDao;
    private SiteInvestigatorDao siteInvestigatorDao;
    private ResearchStaffDao researchStaffDao;
    private SiteResearchStaffDao siteResearchStaffDao;
    private CtcDao ctcDao;
    protected InvestigationalNewDrugDao investigationalNewDrugDao;
    private MeddraVersionDao meddraVersionDao;
    private ConditionDao conditionDao;
    private StudyRepository studyRepository;
    private LowLevelTermDao lowLevelTermDao;
    private DeviceDao deviceDao;

    private Configuration configuration;
    protected ConfigPropertyRepositoryImpl configPropertyRepository;
    private ConfigProperty configProperty;
    private Map<String, List<Lov>> map;
    protected WebControllerValidator webControllerValidator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SecurityTestUtils.switchToSuperuser();
        studyDao = registerDaoMockFor(StudyDao.class);
        investigationalNewDrugDao = registerDaoMockFor(InvestigationalNewDrugDao.class);
        configuration = registerMockFor(Configuration.class);
        configPropertyRepository = registerMockFor(ConfigPropertyRepositoryImpl.class);
        organizationDao = registerDaoMockFor(OrganizationDao.class);
        agentDao = registerDaoMockFor(AgentDao.class);
        siteInvestigatorDao = registerDaoMockFor(SiteInvestigatorDao.class);
        researchStaffDao = registerDaoMockFor(ResearchStaffDao.class);
        siteResearchStaffDao = registerDaoMockFor(SiteResearchStaffDao.class);
        ctcDao = registerDaoMockFor(CtcDao.class);
        meddraVersionDao = registerDaoMockFor(MeddraVersionDao.class);
        conditionDao = registerDaoMockFor(ConditionDao.class);
        studyRepository = registerMockFor(StudyRepository.class);
        lowLevelTermDao = registerDaoMockFor(LowLevelTermDao.class);
        deviceDao = registerDaoMockFor(DeviceDao.class);
        configProperty = registerMockFor(ConfigProperty.class);
        map = registerMockFor(Map.class);
        webControllerValidator = registerMockFor(WebControllerValidator.class);
        messageSource = registerMockFor(MessageSource.class);
        

        //create the command
        command = new StudyCommand(studyDao, investigationalNewDrugDao);
        Organization o = Fixtures.createOrganization("test");
        Study study = new LocalStudy();
        study.addStudyOrganization(Fixtures.createStudyCoordinatingCenter(o));
        study.addStudyOrganization(Fixtures.createStudyFundingSponsor(o));
        study.addStudyOrganization(Fixtures.createStudySite(o,1));
        study.setDataEntryStatus(false);
        command.setStudy(study);
        command.setStudyRepository(studyRepository);

        controller = new CreateStudyController();
        controller.setConfiguration(configuration);
        controller.setConfigPropertyRepository(configPropertyRepository);
        controller.setOrganizationDao(organizationDao);
        controller.setAgentDao(agentDao);
        controller.setSiteInvestigatorDao(siteInvestigatorDao);
        controller.setSiteResearchStaffDao(siteResearchStaffDao);
        controller.setResearchStaffDao(researchStaffDao);
        controller.setCtcDao(ctcDao);
        controller.setMeddraVersionDao(meddraVersionDao);
        controller.setConditionDao(conditionDao);
        controller.setLowLevelTermDao(lowLevelTermDao);
        controller.setStudyRepository(studyRepository);
        controller.setStudyDao(studyDao);
        controller.setDeviceDao(deviceDao);
        controller.setInvestigationalNewDrugDao(investigationalNewDrugDao);
        controller.setWebControllerValidator(webControllerValidator);
        controller.setMessageSource(messageSource);

        StaticTabConfigurer tabConfigurer = new StaticTabConfigurer(ctcDao, organizationDao, studyDao, agentDao, researchStaffDao, siteInvestigatorDao, meddraVersionDao);
        tabConfigurer.addBean("configurationProperty", configProperty);

        controller.setTabConfigurer(tabConfigurer);

        expect(configProperty.getMap()).andReturn(map).anyTimes();
        expect(map.get(EasyMock.anyObject())).andReturn(new ArrayList<Lov>()).anyTimes();
        expect(messageSource.getMessage((String)EasyMock.anyObject(),
                    (Object[])EasyMock.anyObject(),
                    (String)EasyMock.anyObject(),
                    (Locale)EasyMock.anyObject() )).andReturn("Hello").anyTimes();
    }

    /*
     * Will test the first request to create flow.
     * - invoke form backing
     * - command should be kept in session.
     */

    public void testCreateFlow_GET() throws Exception {
        request.setMethod("GET");
        expect(ctcDao.getAll()).andReturn(new ArrayList<Ctc>()).anyTimes();
        expect(meddraVersionDao.getAll()).andReturn(new ArrayList<MeddraVersion>()).anyTimes();
        expect(configPropertyRepository.getByType(ConfigPropertyType.RESEARCH_STAFF_ROLE_TYPE)).andReturn(new ArrayList<gov.nih.nci.cabig.caaers.domain.ConfigProperty>());
        expect(configPropertyRepository.getByType(ConfigPropertyType.INVESTIGATOR_ROLE_TYPE)).andReturn(new ArrayList<gov.nih.nci.cabig.caaers.domain.ConfigProperty>());
        expect(configuration.get(Configuration.ENABLE_WORKFLOW)).andReturn(false);

        replayMocks();

        ModelAndView mv = controller.handleRequest(request, response);
        assertEquals("study/study_reviewsummary", mv.getViewName());
        assertNotNull(mv.getModel().get("fieldGroups"));
        assertNotNull(mv.getModel().get("command"));
        assertNotNull(session.getAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.FORM.command"));
        assertTrue(session.getAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.FORM.command") instanceof StudyCommand);

        verifyMocks();
    }

    /*
     * Will make a second request to the page
     *  - Current page is 'Therapies'
     *  - Target page is 'Therapies'
     *
     */
    public void testSaveInCreateFlow() throws Exception {
        Study newStudy = new LocalStudy();
        command.setStudy(newStudy);
        newStudy.addStudyOrganization(Fixtures.createStudyCoordinatingCenter(null));

        assertNull(session.getAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.FORM.command"));
        session.setAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.FORM.command", command);
        session.setAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.PAGE.command", command);

        request.addParameter("_page", "0");
        request.setAttribute("_page", "0");

        request.addParameter("_target0", "1");
        request.setAttribute("_target0", "1");
        request.setMethod("POST");

        studyRepository.save(command.getStudy());
        webControllerValidator.validate(EasyMock.eq(request), EasyMock.eq(command), (BindException) EasyMock.anyObject());
        EasyMock.expectLastCall().anyTimes();

        replayMocks();

        ModelAndView mv = controller.handleRequest(request, response);
        assertSame(command, session.getAttribute("gov.nih.nci.cabig.caaers.web.study.CreateStudyController.FORM.command"));
        assertSame(newStudy, command.getStudy());
        assertEquals("study/study_reviewsummary", mv.getViewName());

        verifyMocks();
    }
}
