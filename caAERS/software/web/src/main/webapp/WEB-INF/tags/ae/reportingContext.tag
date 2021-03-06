<%--
 Flow : EditAdverseEventController
 All the JSPs in the flow. to put the content. 
--%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@attribute name="checkBoxMode" type="java.lang.Boolean" %>
<%@attribute name="allReportDefinitions" required="true" type="java.util.List" description="All report definitions to show" %>
<%@attribute name="selectedReportDefinitions" required="true" type="java.util.List" description="Selected report definitions to show" %>
<c:if test="${checkBoxMode}">
<div id="contextMenuOuter">
<form action="dummy">
<input type="hidden" name="CSRF_TOKEN" value="${CSRF_TOKEN }"/>
<div id="contextMenuContent">
	<c:forEach var="rd" items="${allReportDefinitions}" >
	 	<c:set var="_rdChecked" value="${false}" scope="page" />
	 	<c:forEach var="rdS" items="${selectedReportDefinitions}">
			<c:set var="_rdChecked" value="${_rdChecked or (rd.id eq rdS.id)}" scope="page" />
	 	</c:forEach>
		<div>
		<input id="rdContextChk-${rd.id}" class="rdCheckbox" type="checkbox" name="reportingContextRdId" value="${rd.id}" onclick="toggleApplyBtn()"  ${_rdChecked ? 'checked' : '' }/> ${rd.label}
		</div>
	</c:forEach>
	<div style="clear:both; margin-top:5px;">
	<tags:button id="btn-apply-now" disabled="disabled" color="blue" value="Apply Now" onclick="updateReportingContext(this, true)" size="small" icon="check" type="button"/>
	</div>
</div>
<script>
  <c:forEach var="rd" items="${selectedReportDefinitions}">
	 AE.checkedReports.push(${rd.id});
  </c:forEach>
</script>
</form>
</div>
</c:if>
<c:if test="${not checkBoxMode}">
<div id="contextMenuContent-hidden" style="display:none;">
<c:forEach var="rd" items="${allReportDefinitions}" >
	<c:set var="_rdChecked" value="${false}" scope="page" />
	<c:forEach var="rdS" items="${selectedReportDefinitions}">
		<c:set var="_rdChecked" value="${_rdChecked or (rd.id eq rdS.id)}" scope="page" />
	</c:forEach>
	<input id="rdContext-${rd.id}" class="rdCheckbox" type="checkbox" name="reportingContextRdId" value="${rd.id}" ${_rdChecked ? 'checked' : '' } />
</c:forEach>
</div>
</c:if>