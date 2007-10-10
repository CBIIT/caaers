package gov.nih.nci.cabig.caaers.web.rule.author;

import gov.nih.nci.cabig.caaers.rules.business.service.RulesEngineService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class RuleUtilController extends AbstractCommandController {

	public RuleUtilController() {
		setCommandClass(RuleUtilCommand.class);
    }
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object arg2, BindException arg3) throws Exception {
		String ruleSetName = request.getParameter("ruleSetName");
		RulesEngineService rulesEngineService = (RulesEngineService) getApplicationContext().getBean("ruleEngineService");
		rulesEngineService.deleteRuleSet(ruleSetName);
		
		return new ModelAndView("redirect:/pages/rule/list");
	}
}
