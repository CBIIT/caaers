<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="reportingPeriod" type="gov.nih.nci.cabig.caaers.domain.dto.AdverseEventReportingPeriodDTO" required="true" description="The course that is being rendered" %>

<c:set var="currClass" value="${(index %2) eq 0 ? 'odd' : 'even'}" />
<%-- <c:set var="reportingPeriodPageURL" 
	value="/pages/ae/captureRoutine?participant=${reportingPeriod.participant.id}&study=${reportingPeriod.study.id}&_page=0&adverseEventReportingPeriod=${reportingPeriod.id}&_target1=1&displayReportingPeriod=true&addReportingPeriodBinder=true" /> --%>
<c:set var="reportingPeriodPageURL"
	value="/pages/ae/reviewResolver?participant=${reportingPeriod.participant.id}&study=${reportingPeriod.study.id}&adverseEventReportingPeriod=${reportingPeriod.id}" />

<tr align="center" id="${index}" class="${currClass}">
	<td>
		<chrome:collapsableElement targetID="rptable${reportingPeriod.id}" collapsed="true" id="rpID_${reportingPeriod.id}"/>
	</td>
	<td width="18%" align="left">
		<a href="<c:url value="${reportingPeriodPageURL}"/>">${reportingPeriod.evaluationPeriodName}</a>
		<%-- ${reportingPeriod.evaluationPeriodName} --%>
	</td>
	<td width="22%">${reportingPeriod.evaluationPeriodTypeName}</td>
	<c:if test="${reportingPeriod.workflowId != null}">
	<td width="25%" id="reportingPeriod-${reportingPeriod.id}-status">${reportingPeriod.reviewStatus.displayName}</td>
	<td><a href="#" onClick="displayPopup('reportingPeriod', ${reportingPeriod.id})"><img src="<chrome:imageUrl name="../editComment.png" />" /></a></td>
	<td width="25%">
		<select onChange="advanceWorkflow(this,${reportingPeriod.workflowId }, ${reportingPeriod.id }, 'reportingPeriod')" class="wf${reportingPeriod.workflowId }" style="width: 150px">
			<option value="Please Select">Please select</option>
			<c:forEach items="${reportingPeriod.possibleActions}" var="aStatus">
				<option value="${aStatus }">${aStatus}</option>
			</c:forEach>
		</select>
		<img id="reportingPeriod-${reportingPeriod.id}-indicator" src="<c:url value="/images/indicator.white.gif"/>" alt="activity indicator" style="display:none;"/>
	</td>
	</c:if>
	<c:if test="${reportingPeriod.workflowId == null}">
		<td width="25%" id="reportingPeriod-${reportingPeriod.id}-status">N/A</td>
		<td>N/A</td>
		<td width="25%">N/A</td>
	</c:if>
</tr>
<tr id="rptable${reportingPeriod.id}" style="display:none;" class="${currClass}">
	<td></td>
	<td></td>
	<td colspan=6>
		<table width="100%" border="0" cellspacing="0"> <!-- This is the outer table -->
			<tr>
				<td width="100%">
					<c:choose>
						<c:when test="${not empty reportingPeriod.activeAeReports}">
							
								<table width="100%" border="0" cellspacing="0" class="rpTableRegionOuter">
									<tr>
										<td>
											<table width="100%" border="0" cellspacing="0" class="rpTableRegionOuter eXtremeTable">
												<thead>
													<tr class="label">
														<td class="tableHeader" width="36%">Report Name</td>
														<td class="centerTableHeader" width="24%">Report Submission Status</td>
														<td class="centerTableHeader" width="10%">Review Status</td>
														<td class="centerTableHeader" width="5%">Comments</td>
														<td class="centerTableHeader" width="25%">Action</td>
													</tr>						
												</thead>
												<c:forEach items="${reportingPeriod.activeAeReports}" var="aeReport" varStatus="rStatus">
													<ae:oneRoutingExpeditedReportRow aeReport="${aeReport}" index="${rStatus.index}" />
												</c:forEach>
											</table>
										</td>
									</tr>
								</table>
						</c:when>
						<c:otherwise>
							There are no reports for this course/cycle.
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
            <tr>
                <td width="100%">
                     <c:choose>
                         <c:when test="${not empty reportingPeriod.reconciliationReports}">
                             <table width="100%" border="0" cellspacing="0" class="rpTableRegionOuter">
                                 <tr>
                                     <td>
                                         <table width="100%" border="0" cellspacing="0" class="rpTableRegionOuter eXtremeTable">
                                             <thead>
                                             <tr class="label">
                                                 <td class="tableHeader" width="100%">Reconciliation Reports</td>
                                             </tr>
                                             </thead>
                                             <c:forEach items="${reportingPeriod.reconciliationReports}" var="rr" varStatus="rrStatus">
                                              <tr><td>
                                                  <c:url var="rrURL" value="/ae/reconciliationReport"/>
                                                  <a href="${rrURL}?rrId=${rr.id}">Report # ${rrStatus.index + 1}</a> -  by ${rr.reviewedBy} on <tags:formatDate value="${rr.createdDate}" />
                                              </td></tr>
                                             </c:forEach>
                                         </table>
                                     </td>
                                 </tr>
                             </table>
                         </c:when>
                         <c:otherwise>
                             There are no reconciliation reports for this course/cycle.
                         </c:otherwise>
                     </c:choose>
                </td>
            </tr>
			<tr style="display:none">
				<td width="100%">
					<ae:routingAndReviewListAllAeSection reportingPeriod="${reportingPeriod.adverseEventReportingPeriod}" isDCPStudy="${reportingPeriod.dcpSponsoredStudy}"/>
				</td>
			</tr>
		</table>			
	</td>
</tr>