package gov.nih.nci.cabig.caaers.web.search;

import gov.nih.nci.cabig.caaers.web.study.SearchStudyCommand;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestDataBinder;

public class SearchInvestigatorController extends SearchController {
	private static final Log log = LogFactory.getLog(SearchInvestigatorController.class);

	public SearchInvestigatorController() {
		setCommandClass(SearchStudyCommand.class);
		setFormView("search/investigator_search");
		setSuccessView("search/investigator_search");
	}

	@Override
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		log.debug(" In initBinder " + isFormSubmission(request));
		if (!isFormSubmission(request)) {
			super.buildSearchResultTable(request, null, null, 9);
		}
	}

	@Override
	protected void onBind(final HttpServletRequest request, final Object command) throws Exception {
		log.debug(" onBind ");
		String prop = request.getParameter("_prop");
		String value = request.getParameter("_value");
		log.debug(prop + "||" + value);
		super.buildSearchResultTable(request, prop, value, 9);

	}
}
