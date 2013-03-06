/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Saurbah Agrawal
 */
public class EditOrganizationController extends OrganizationController<Organization> {

    private static final Log log = LogFactory.getLog(EditOrganizationController.class);

    public EditOrganizationController() {
        setBindOnNewForm(true);
    }

    // /LOGIC

    @Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {
        request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));
        Organization organization = organizationDao.getById(Integer.parseInt(request.getParameter("organizationId")));

        if (log.isDebugEnabled()) {
            log.debug("Retrieved Organization :" + String.valueOf(organization));
        }

        return organization;
    }
    
    @Override
    protected Organization save(final Organization organization, final Errors errors) {
        if (errors.hasErrors()) {
            return organization;
        }
        organizationRepository.createOrUpdate(organization);
        return organization;
    }

    @Override
    protected boolean isSummaryEnabled() {
        return true;
    }

    @Override
    protected void layoutTabs(final Flow<Organization> flow) {
        flow.addTab(new OrganizationTab());
    }

    @SuppressWarnings("unchecked")
	@Override
    protected ModelAndView processFinish(final HttpServletRequest request,
                    final HttpServletResponse response, final Object command,
                    final BindException errors) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("admin/organization_confirmation");
    	if(!errors.hasErrors()){
    		Organization organization = (Organization) command;
    		if("saveRemoteOrg".equals(request.getParameter("_action")) && organization.getId() != null){
    			organizationDao.evict(organization);
    			Organization remoteOrganization = organization.getExternalOrganizations().get(Integer.parseInt(request.getParameter("_selected")));
    			organizationRepository.convertToRemote(organization,remoteOrganization);
    			organization.setName(remoteOrganization.getName());
    			organization.setNciInstituteCode(remoteOrganization.getNciInstituteCode());
    			organization.setDescriptionText(remoteOrganization.getDescriptionText());
    			modelAndView.addAllObjects(errors.getModel());
    			modelAndView.getModel().put("flashMessage", "Successfully synched the Organization");
    		}else{
    			organizationRepository.createOrUpdate(organization);
    			modelAndView.getModel().put("flashMessage", "Successfully updated the Organization");
                modelAndView.addAllObjects(errors.getModel());
    		}
    	}
        return modelAndView;
    }
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
    	Organization organization = (Organization) command;
    	if("syncOrganization".equals(request.getParameter("_action"))){
    		//TODO: searching only on nci-id , shud be able to search by name or nci-id
    		List<Organization> remoteOrgs = organizationRepository.searchRemoteOrganization(organization.getNciInstituteCode(), "nciInstituteCode");
    		if(remoteOrgs != null && remoteOrgs.size() > 0){
    			organization.setExternalOrganizations(remoteOrgs);
    			errors.reject("REMOTE_ORG_EXISTS","Organization with NCI Institute Code " +organization.getNciInstituteCode()+ " exisits in external system");
    		}
    	}
    }
}
