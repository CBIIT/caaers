<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="rd" tagdir="/WEB-INF/tags/report" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <tags:stylesheetLink name="ae"/>
    <tags:stylesheetLink name="extremecomponents"/>
    <style type="text/css">
    .eXtremeTable select {
		width: 150px;
	}
    </style>
    <tags:includeScriptaculous/>
    <tags:dwrJavascriptLink objects="reportDef"/>
    
    <title>Report Delivery Configuration</title>
    <script>
    	function fireDelete(index, trId){
    		//set the index to delete and submit the form.
    		frm = document.getElementById('command');
    		frm._target.name='_noname';
			frm._action.value='deleteDelivery';
			frm._selected.value=index;		
			frm.submit();
    	
    	}
    	Event.observe(window, "load", function() {

			//This is added for system Recipient button
            new ListEditor("rdd-sys-row", reportDef, "ReportDeliveryDefinition", {
            	addButton: "add-url-button",
            	addIndicator: "add-url-indicator",
            	addParameters: [1],
            	addFirstAfter: "system-table-head",
                addCallback: function(nextIndex) {
                	if($('empty-sys-row')){
                		Effect.Fade('empty-sys-row');
                	}
                }
            });
            
            
			//This is added for Email recipient (e-mail)
            new ListEditor("rdd-email-row", reportDef, "ReportDeliveryDefinition", {
            	addButton: "add-email-button",
            	addIndicator: "add-email-indicator",
            	addParameters: [2],
            	addFirstAfter: "emails-table-head",
                addCallback: function(nextIndex) {
                	if($('empty-email-row')){
                		Effect.Fade('empty-email-row');
                	}
                }
            });
            
            
            //This is added for add role recipient buttion
             new ListEditor("rdd-email-row", reportDef, "ReportDeliveryDefinition", {
                addButton: "add-role-button",
                addIndicator: "add-role-indicator",
            	addParameters: [3],
            	addFirstAfter: "emails-table-head",
                addCallback: function(nextIndex) {
                	if($('empty-email-row')){
                		Effect.Fade('empty-email-row');
                	}
                }
            });
        });
    </script>
</head>
<body>
    <chrome:division>
	    <tags:tabForm tab="${tab}" flow="${flow}" willSave="false" hideErrorDetails="true">
	     <jsp:attribute name="instructions">
        	You are entering final report delivery information for <b>${command.reportDefinition.name}</b>.
   		 </jsp:attribute>
			<jsp:attribute name="singleFields">
				<div id="rdd-tab-fields">
            	   <input type="hidden" name="lastPointOnScale" value=""/>
               	   <input type="hidden" name="notificationType" value="EMAIL_NOTIFICATION" />
               	   <input type="hidden" name="_action" value="">
				   <input type="hidden" name="_selected" value="">
            	</div>
			</jsp:attribute>
			<jsp:attribute name="repeatingFields">
			<tags:section title="Email Recipients">
				<jsp:attribute name="instructions">
					Click on Add eMail or Add Role button to add an email address or role respectively.A PDF version
					of the final report will be delivered to the email address entered. 
				</jsp:attribute>
				<jsp:attribute name="singleFields">
					<table width="100%" class="tablecontent">
						<tr id="emails-table-head" class="emails-table-head">
							<th width="35%" class="tableHeader">Name</th>
							<th width="60%" class="tableHeader"><tags:requiredIndicator /> Role/EmailAddress</th>
							<th width="5%" class="tableHeader">&nbsp;</th>
						</tr>
						<c:set var="pIndex">0</c:set>
						<c:forEach items="${command.reportDefinition.deliveryDefinitions}" var="rdd" varStatus="status">
							<c:if test="${rdd.entityType ne 1}">
							 <rd:oneReportDeliveryDefinition index="${pIndex}" rdd="${rdd}" originalIndex="${status.index}"  />
							 <c:set var="pIndex">${pIndex + 1}</c:set>
							</c:if>
						</c:forEach>
						<c:if test="${pIndex lt 1}">
							<tr id="empty-email-row" class="even" ><td colspan="3" align="center" >No eMail delivery definitions configured</td></tr>
						</c:if>
					</table>
					<br>
				</jsp:attribute>
				<jsp:attribute name="localButtons">
					<tags:listEditorAddButton divisionClass="email" label="Add eMail"/>
					<tags:listEditorAddButton divisionClass="role" label="Add Role"/>
				</jsp:attribute>
			</tags:section>
			<tags:section title="System Recipients">
				<jsp:attribute name="instructions">
					Click on Add URL button to add a system URL. An XML version of the final report will be posted to the system URL.
				</jsp:attribute>
				<jsp:attribute name="singleFields">
					<table width="100%" class="tablecontent">
						<tr id="system-table-head" class="system-table-head">
							<th width="35%" class="tableHeader"><tags:requiredIndicator /> Name</th>
							<th width="60%" class="tableHeader"><tags:requiredIndicator /> URL</th>
							<th width="5%" class="tableHeader">&nbsp;</th>
						</tr>
						<c:set var="sIndex">0</c:set>
						<c:forEach items="${command.reportDefinition.deliveryDefinitions}" var="rdd" varStatus="status">
							<c:if test="${rdd.entityType eq 1}">
							 <rd:oneReportDeliveryDefinition index="${sIndex}" rdd="${rdd}" originalIndex="${status.index}" />
							 <c:set var="sIndex">${sIndex + 1}</c:set>
							</c:if>
						</c:forEach>
						<c:if test="${sIndex lt 1}">
							<tr id="empty-sys-row" class="even"><td align="center" colspan="3">No system delivery definitions configured</td></tr>
						</c:if>
					</table>
					<br>
				</jsp:attribute>
				<jsp:attribute name="localButtons">
					<tags:listEditorAddButton divisionClass="url" label="Add URL"/>   
				</jsp:attribute>
			</tags:section>
			<br />
                                                                                                                                                                                                                                                           
	        </jsp:attribute>
		</tags:tabForm> 
	</chrome:division>
</body>
</html>