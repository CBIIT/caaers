package gov.nih.nci.cabig.caaers.web.admin;

import java.util.List;

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.domain.LocalOrganization;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.tools.spring.tabbedflow.AutomaticSaveAjaxableFormController;
import gov.nih.nci.cabig.caaers.web.validation.validator.WebControllerValidator;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base Controller class to handle the basic work flow in the Creation / Updation of a Organization
 * Design This uses AbstractTabbedFlowFormController to implement tabbed workflow
 * 
 * @author Saurabh
 */
// TODO: this "flow" only has one tab in all its forms. It shouldn't use the complexity of a flow
// controller
public abstract class OrganizationController<C extends Organization> extends
                AutomaticSaveAjaxableFormController<C, Organization, OrganizationDao> {

    private static final Log log = LogFactory.getLog(OrganizationController.class);

    protected OrganizationDao organizationDao;

    protected OrganizationRepository organizationRepository;

    protected WebControllerValidator webControllerValidator;
    
    protected EventFactory eventFactory;
    
    public OrganizationController() {
        setCommandClass(Organization.class);
        Flow<C> flow = new Flow<C>("Create Organization");
        layoutTabs(flow);
        setFlow(flow);
        setAllowDirtyBack(false);
        setAllowDirtyForward(false);
    }

    // /LOGIC
    @Override
    protected Organization getPrimaryDomainObject(final C command) {
        return command;
    }

    @Required
    public void setOrganizationDao(final OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    @Override
    protected OrganizationDao getDao() {
        return organizationDao;
    }

    /**
     * Template method to let the subclass decide the order of tab
     */
    protected abstract void layoutTabs(Flow<C> flow);

    /**
     * Override this in sub controller if summary is needed
     * 
     * @return
     */
    protected boolean isSummaryEnabled() {
        return false;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected ModelAndView processFinish(final HttpServletRequest request,
                    final HttpServletResponse response, final Object command,
                    final BindException errors) throws Exception {

    	ModelAndView modelAndView = new ModelAndView("admin/organization_confirmation");
    	if(!errors.hasErrors()){
    		Organization organization = (Organization) command;
    		if("saveRemoteOrg".equals(request.getParameter("_action"))){
    			Organization remoteOrgToSave = organization.getExternalOrganizations().get(Integer.parseInt(request.getParameter("_selected")));
    			organizationRepository.createOrUpdate(remoteOrgToSave);
    			organization.setName(remoteOrgToSave.getName());
    			organization.setNciInstituteCode(remoteOrgToSave.getNciInstituteCode());
    			organization.setDescriptionText(remoteOrgToSave.getDescriptionText());
    			modelAndView.getModel().put("flashMessage", "Successfully created the Organization");
    			modelAndView.addAllObjects(errors.getModel());
    			return modelAndView;
    		}
            organizationRepository.createOrUpdate(organization);
            modelAndView.getModel().put("flashMessage", "Successfully created the Organization");
            modelAndView.addAllObjects(errors.getModel());
    	}
    	return modelAndView;
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command,
                    BindException errors, int page) throws Exception {
    	
    	super.onBindAndValidate(request, command, errors, page);
    	//webControllerValidator.validate(request, command, errors);
    	Organization organization = (Organization) command;
    	if(organization.getId() == null){
    		if(!"saveRemoteOrg".equals(request.getParameter("_action"))){
    			OrganizationQuery organizationQuery = new OrganizationQuery();
    			organizationQuery.filterByNciCodeExactMatch(organization.getNciInstituteCode());
    			List<Organization> localOrgs = organizationDao.getLocalOrganizations(organizationQuery);
    			if(localOrgs != null && localOrgs.size() > 0){
    				errors.reject("LOCAL_ORG_EXISTS","Organization with NCI Institute Code " +organization.getNciInstituteCode()+ " already exisits");
    				return;
    			}
    			List<Organization> remoteOrgs = organizationRepository.searchRemoteOrganization(organization.getNciInstituteCode(), "nciInstituteCode");
       		if(remoteOrgs != null && remoteOrgs.size() > 0){
        			organization.setExternalOrganizations(remoteOrgs);
        			errors.reject("REMOTE_ORG_EXISTS","Organization with NCI Institute Code " +organization.getNciInstituteCode()+ " exisits in external system");
        		}
        	}
        }
    }

    @Required
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Required
    public void setWebControllerValidator(WebControllerValidator webControllerValidator) {
        this.webControllerValidator = webControllerValidator;
    }
    
	public void setEventFactory(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}
}
