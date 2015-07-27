<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="manageReportsRepotingPeriodDTO" type="gov.nih.nci.cabig.caaers.domain.dto.ManageReportsRepotingPeriodDTO" required="true" description="The course that is being rendered" %>

<script>
	
	function executeReportingPeriodActions(id){
/*
		var sbox = $("actions-" + id);
		if(sbox.value == 'editReportingPeriod'){
*/
        if (confirm('Are you sure you want to take the action - Edit Adverse Events ?')) {
            var url = '<c:url value="/pages/ae/reviewResolver?participant=${command.participant.id}&study=${command.study.id}&adverseEventReportingPeriod=' + id + '"/>';
            window.location = url;
        } else {
            return false;
        }
//		}
	}

    function showAEMenuOptions(_element, _rpid) {
        _items = "<li><a class='submitter-blue' href='#' onclick='executeReportingPeriodActions(" + _rpid + ")'>Edit Adverse Events</a></li>";
        var html = "<div><ul style='font-family:tahoma;'>" + _items + "</ul></div>";
        jQuery(_element).menu({
                content: html,
                maxHeight: 180,
                width: 230,
                positionOpts: {
                    directionV: 'down',
                    posX: 'left',
                    posY: 'bottom',
                    offsetX: 0,
                    offsetY: 0
                },
                showSpeed: 300
            });
    }
	
</script>


<c:set var="currClass" value="${(index %2) eq 0 ? 'odd' : 'even'}" />
<c:set var="reportingPeriodPageURL" value="/pages/ae/captureRoutine?participant=${command.participant.id}&study=${command.study.id}&_page=0&adverseEventReportingPeriod=${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}&_target1=1&displayReportingPeriod=true&addReportingPeriodBinder=true" />

<tr align="center" id="${index}" class="${currClass}" onmouseout="this.className='${currClass}'" onmouseover="this.className='highlight'">
	<td><chrome:collapsableInputElement targetID="table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}" collapsed="true" id="collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}"/></td>
	<td width="15%" align="left" onclick="expandImageClick('collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}', 'table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}');">${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.name }</td>
	<td width="10%" onclick="expandImageClick('collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}', 'table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}');">${fn:length(manageReportsRepotingPeriodDTO.reports)}</td>
	<td width="10%" onclick="expandImageClick('collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}', 'table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}');">${fn:length(manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.evaluatedAdverseEvents)}</td>
	<td align="left" onclick="expandImageClick('collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}', 'table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}');"><span class="${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.reportStatus eq 'Reports Due' ? 'reportsDue' : manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.reportStatus eq 'Report Submission Failed' ? 'reportsFailed' : manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.reportStatus eq 'Reports Completed' ? 'reportsCompleted' : manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.reportStatus eq 'Reports Overdue' ? 'reportsOverdue' : 'reportsNone' }" >${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.reportStatus}</span></td>
	<td width="20%" align="center" onclick="expandImageClick('collapseElement${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}', 'table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}');">
        <img src='<c:url value="/images/orange-actions.gif" />?${requestScope.webCacheId}' border='0' onmouseover='showAEMenuOptions(this, ${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id})' style='cursor:pointer;'>
	</td>
</tr>

<tr id="table${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.id}" style="display:none;" class="${currClass}">
	<td></td>
	<td></td>
	<td colspan=5>
		<table width="100%" border="0" cellspacing="0"> <!-- This is the outer table -->
			<tr>
				<td width="100%">
					<div class="eXtremeTable">
						<table width="100%" border="0" cellspacing="0" class="rpTableRegion">
						  <c:choose>
							<c:when test="${fn:length(manageReportsRepotingPeriodDTO.adverseEventReportingPeriod.aeReports) gt 0}">
								<thead>
									<tr align="center" class="label">
										<td width="5%"/>
										<td class="tableHeader" width="15%">Report Type</td>
										<td class="tableHeader" width="15%">Report ID</td>
										<td class="centerTableHeader" width="10%">Amendment #</td>
										<td class="centerTableHeader" width="10%"># of AEs</td>
										<td class="tableHeader" width="20%">Data Entry Status</td>
										<td class="tableHeader" width="20%">Report Submission Status</td>
										<td class="tableHeader" width="20%">Options</td>
									</tr>
								</thead>
								<c:forEach items="${manageReportsRepotingPeriodDTO.reports}" var="report" varStatus="rStatus">
									<ae:oneListReportRow report="${report }" rpIndex="${rStatus.index }"/>
								</c:forEach>
							</c:when>					
							<c:otherwise>There are no reports for this course/cycle.</c:otherwise>
							</c:choose>
						</table>
					</div>
				</td>
			</tr>
			<%-- 
			<tr style="display:none">
				<td width="100%">
					<ae:listAllAeSection reportingPeriod="${manageReportsRepotingPeriodDTO.adverseEventReportingPeriod}"/>
				</td>
			</tr>
			--%>
		</table>			
	</td>
</tr>
