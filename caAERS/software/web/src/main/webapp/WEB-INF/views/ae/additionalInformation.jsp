<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
    <title>${tab.longTitle}</title>
    <style type="text/css">
        div.row div.label { width: 17em; } 
		div.row div.value, div.row div.extra { margin-left: 18em; }
    </style>
    <tags:dwrJavascriptLink objects="createAE"/>
	<tags:slider renderComments="${command.associatedToWorkflow }" renderAlerts="${command.associatedToLabAlerts}" reports="${command.selectedReportsAssociatedToWorkflow}" 
		display="${(command.associatedToWorkflow or command.associatedToLabAlerts) ? '' : 'none'}" workflowType="report">
    	<jsp:attribute name="labs">
    		<div id="labs-id" style="display:none;">
    			<tags:labs labs="${command.assignment.labLoads}"/>
    		</div>
    	</jsp:attribute>
    </tags:slider>
    <script type="text/javascript">
    	var routingHelper = new RoutingAndReviewHelper(createAE, 'aeReport');
    	
    	Event.observe(window, "load", function() {
            
             //only show the workflow tab, if it is associated to workflow
            var associatedToWorkflow = ${command.associatedToWorkflow};
            if(associatedToWorkflow){
            	<c:forEach items="${command.selectedReportsAssociatedToWorkflow}" var="report" varStatus="status">
	 	          	routingHelper.retrieveReviewCommentsAndActions('${report.id}');
 	          	</c:forEach>
            }
        })
    </script>
</head>
<body>
<tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="section17attachments">
    <jsp:attribute name="instructions">
        <tags:instructions code="instruction_ae_additionalInfo" />
    </jsp:attribute>
    <jsp:attribute name="singleFields">
    	<div class="leftpanel">
    	<c:forEach items="${fieldGroups.desc.fields}" var="field" begin="0" end="5">
            <tags:renderRow field="${field}"/>
        </c:forEach>
    	
    	</div>
    	<div class="rightpanel">
    	<c:forEach items="${fieldGroups.desc.fields}" var="field" begin="6" end="11">
            <tags:renderRow field="${field}"/>
        </c:forEach>
    	
    	</div>
        <div id="spacer" style="clear: both;"> </div>
        <tags:renderRow field="${fieldGroups.desc.fields[12]}" />
        <ae:reportingContext allReportDefinitions="${command.applicableReportDefinitions}" selectedReportDefinitions="${command.selectedReportDefinitions}" />
        <c:if test="${empty requestScope.cntRF or (requestScope.cntRF lt 1)}">
            <caaers:message code="LBL_aeReport.additionalInformation.noFields" text="Additional information is not applicable for this report." var="na" scope="request"/>
            <chrome:warningMessage key="na"/>
        </c:if>
    </jsp:attribute>
</tags:tabForm>
</body>
</html>
