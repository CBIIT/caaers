<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<html>
<head>
<tags:stylesheetLink name="extremecomponents"/>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<link rel="stylesheet" type="text/css" href="/caaers/css/ae.css" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>${tab.longTitle}</title>
<tags:includeScriptaculous/>
  <tags:includePrototypeWindow />
<script type="text/javascript">

	Event.observe(window, "load", function() {
	
		Event.observe('flow-next', 'click', displayOptionsPopup);
		
		$('create-new-report').observe("click", function(){ createNewReport(); });
		
		
		if($('manualselect2')){
			 
		
      		 Event.observe('manualselect2', "click", function() {
      	 		var answer = confirm('Are you sure you want to bypass the caAERS-based report selection above and instead manually select from the list of all reports defined for this study?');
      	 	 	if(answer){
      	 	 		$('manualselect2').disabled=true
      	 	  	 	$('report-list').hide();
      		   		$('report-list').innerHTML = $('report-list-full').innerHTML;
      		   		$('report-list-full').innerHTML='';
 			   		AE.slideAndShow($('report-list'));  
 			  		// setUpEventObserving();	
      	 	 	}	
      	 	 });
		}

	});
	
	function createNewReport(){
			forwardControl('createNew', '');
		}
		
		function editReport(reportId){
			forwardControl('editReport', reportId);
		}
		
		function amendReport(reportId){
			forwardControl('amendReport', reportId);
		}
		
		function forwardControl(task, reportId){
			var form = document.getElementById('command')
			form._action.value=task;
			form._reportId.value=reportId;
			form.submit();
		}
		
		function enableReportsInPopup(){
			var chkboxElements = $('report-list').select('[type="checkbox"]');
			for(var i=0; i < chkboxElements.length; i++)
				if(chkboxElements[i].checked)
					$(chkboxElements[i].name).show();
				else
					$(chkboxElements[i].name).hide();
		}
		
		function checkIfReportSelected(){
			var reportElements = $('report-list').select('[type="checkbox"]');
			var selected = false;
			for(var i = 0; i < reportElements.length; i++)
				if(reportElements[i].checked)
					selected = true;
			if(!selected){
				alert('At least one report should be selected');
			}
			return selected;
		}
		
		function checkIfAeSelected(){
			var aeElements = $('div-aes').select('[type="checkbox"]');
			var selected = false;
			for(var i = 0; i < aeElements.length; i++)
				if(aeElements[i].checked)
					selected = true;
			if(!selected){
				alert('At least one adverse event should be selected');
			}
			return selected;
		}
		
		function displayOptionsPopup(event){
			Event.stop(event);
			enableReportsInPopup();
			var reportSelected = checkIfReportSelected();
			if(!reportSelected)
				return false;
			var aeSelected = checkIfAeSelected();
			if(!aeSelected)
				return false;
			var contentWin = new Window({className:"alphacube", 
 	 	 			destroyOnClose:true, 
 	 	 			width:700,  height:530, 
 					top: 30, left: 300});
     		contentWin.setContent( 'display_options_popup' );
      		contentWin.showCenter(true);
      		popupObserver = {
      			onDestroy: function(eventName, win) {
      				if (win == contentWin) {
      					$('display_options_popup').style.display='none';
      					contentWin = null;
      					Windows.removeObserver(this);
      				}
      			}
      		}
      		Windows.addObserver(popupObserver);
	}

		
</script>
<style type="text/css">
	.divNotes,.divOtherMeddra{
		font-size:8pt;
	 	border-style:none;
	}

	.centerTableHeader {
		background-color:#308DBB;
		border-color:white;
		border-right:1px solid white;
		color:white;
		font-family:Arial,verdana,helvetica,sans-serif;
		font-size:11px;
		font-weight:bold;
		margin:0px;
		padding:4px 3px;
		text-align:center;
	}

</style>
</head>
<body>
<div id="report-list-full" style="display:none; padding-bottom:5px;" align="center">
	<tags:noform>
		<table class="tablecontent">
		<tr>
			<th>Required</th>
			<th>Report</th>
			<th>Status</th>
		</tr>
		<c:forEach items="${rpdAllTable}"  var="rdTable" varStatus="rdStatus">
			<tr>
				<td align="center">${rdTable.value.required ? 'Yes' : 'No' }</td>
				<td align="left"><tags:renderInputs field="${rdTable.value.field}" cssClass="rpdChk"/> <tags:renderLabel field="${rdTable.value.field}"/></td>
				<td>${rdTable.value.status}</td>
			</tr>
		</c:forEach>
		</table>
	
	</tags:noform>			
</div>
<tags:tabForm tab="${tab}" flow="${flow}" formName="review" saveButtonLabel="Create Report">
	
		<jsp:attribute name="instructions">
		<input type="hidden" name="_finish"/>
		<input type="hidden" name="_action" value="">
		<input type="hidden" name="_reportId" value="">
		<c:set var="reportingPeriodType" value="${command.adverseEventReportingPeriod.epoch.name}" />
	 		<c:if test="${reportingPeriodType != 'Baseline'}">
	 			<tags:instructions code="instruction_ae_checkpoint" />
	 		</c:if>
		</jsp:attribute>
		<jsp:attribute name="singleFields">
		<c:if test="${reportingPeriodType != 'Baseline'}">
	  	 	<c:choose>
  		 	 <c:when test="${not empty rpdSelectedTable}">
  		 	 	<p><strong>Reports Identified by caAERS</strong></p>
    	        <tags:instructions code="instruction_ae_checkpointReports" heading=" "/>
				<div align="center">
              	<div id="report-list" align="center" style="padding-bottom:5px;">
            	  <!-- required reports -->
				<table class="tablecontent">
					<tr>
						<th>Required</th>
						<th>Report</th>
						<th>Status</th>
					</tr>
				<c:forEach items="${rpdSelectedTable}"  var="rdTable" varStatus="rdStatus">
					<tr>
						<td align="center"> ${rdTable.value.required ? 'Yes' : 'No' }</td>
						<td align="left"><tags:renderInputs field="${rdTable.value.field}" cssClass="rpdChk"/> <tags:renderLabel field="${rdTable.value.field}"/></td>
						<td>${rdTable.value.status}</td>
					</tr>
				</c:forEach>
				</table>
				<div class="autoclear" align="center" ><input type="button" id="manualselect2" value="Manually Select Report(s)"  class="manualSelectBtn"/></div>
				</div>
        		</div> 
        		
        		<p>
        		At your discretion, you may elect to bypass the caAERS-based report selection above and 
        		instead manually select from the list of all reports defined for this study the expedited 
        		reports you wish to complete and submit. To do so, click the Manually Select Reports button above.

        		</p>
				
  	 	 	</c:when>
  	 	 	<c:otherwise>
  	 	    	<p>The AEs you have entered <strong>do not</strong> seem to require any expedited reporting. 
            	If you wish to override this decision, please choose the notification and reporting schedule below.</p>
            	<div align="center" style="padding-bottom:5px;" id="report-list">
            	<!-- optional reports -->
				<table class="tablecontent">
					<tr>
						<th>Required</th>
						<th>Report</th>
						<th>Status</th>
					</tr>
					<c:forEach items="${rpdAllTable}"  var="rdTable" varStatus="rdStatus">
					<tr>
						<td align="center">${rdTable.value.required ? 'Yes' : 'No' }</td>
						<td align="left"><tags:renderInputs field="${rdTable.value.field}" cssClass="rpdChk"/> <tags:renderLabel field="${rdTable.value.field}"/></td>
						<td>${rdTable.value.status}</td>
					</tr>
					</c:forEach>
				</table>
        		</div>   
  	 		 </c:otherwise>
  	 		</c:choose>
  	 	
	  	 	<div id="div-aes">
		  	<chrome:division id="div-saes" title="Adverse Event(s) Requiring Reporting" collapsable="true">
  				<c:if test='${command.adverseEventReportingPeriod != null && displaySeriousTable}'>
  					<table id="seriousTable" width="100%" class="tablecontent">
  						<tr>
    						<th scope="col" align="left"><b>Select</b></th>
    						<th scope="col" align="left" width="30%"><b>Term</b> </th>
    						<th scope="col" align="left"><b>Grade</b> </th>
    						<th scope="col" align="left"><b>Attribution</b> </th>
    						<th scope="col" align="left"><b>Hospitalization</b> </th>
    						<th scope="col" align="left"><b>Expected</b> </th>
    						<th scope="col" align="left"><b>Serious</b> </th>
							<th scope="col" align="left"><b>Is primary?</b></th>
    					</tr>
    					<tr id="seriousBlankRow" />
    					<c:forEach items="${command.adverseEventReportingPeriod.reportableAdverseEvents}" varStatus="status" var="ae">
    						<c:if test="${ae.requiresReporting}">
    							<ae:oneSaeRow index="${status.index}" isSolicitedAE="${ae.solicited}" isAETermOtherSpecify="${ae.adverseEventTerm.otherRequired}" adverseEvent="${ae}" aeTermIndex="1" hideDeleteCtrl="true"/>
    						</c:if>
    					</c:forEach>
  					</table>
  				</c:if>
  				<c:if test='${!displaySeriousTable}'>
  					None of the Adverse Events requires reporting.
  				</c:if>
  			</chrome:division>
  	
			<chrome:division id="div-oaes" title="Observed Adverse Event(s)" collapsed="true" collapsable="true">
				<c:if test='${command.adverseEventReportingPeriod != null && displayObservedTable}'>
        			<table id="observedTable" width="100%" class="tablecontent">
    					<tr>
    						<th scope="col" align="left"><b>Select</b></th>
    						<th scope="col" align="left" width="30%"><b><tags:requiredIndicator/>Term</b> </th>
    						<th scope="col" align="left"><b><tags:requiredIndicator/>Grade</b> </th>
    						<th scope="col" align="left"><b>Attribution</b> </th>
    						<th scope="col" align="left"><b>Hospitalization</b> </th>
    						<th scope="col" align="left"><b>Expected</b> </th>
    						<th scope="col" align="left"><b>Serious</b> </th>
							<th scope="col" align="left"><b>Is primary?</b></th>
    					</tr>
    					<tr id="observedBlankRow" />
    					<c:forEach items="${command.adverseEventReportingPeriod.reportableAdverseEvents}" varStatus="status" var="ae">
            				<c:if test="${(not ae.solicited) and (not ae.requiresReporting)}">
	            				<ae:oneSaeRow index="${status.index}" isSolicitedAE="false" isAETermOtherSpecify="${ae.adverseEventTerm.otherRequired}" adverseEvent="${ae}" aeTermIndex="1" hideDeleteCtrl="true"/>
	            			</c:if>
            			</c:forEach>
            		</table>
        		</c:if>
        		<c:if test='${!displayObservedTable}'>
        			No reportable observed adverse events.
        		</c:if> 
			</chrome:division>
	
			<chrome:division title="Solicited Adverse Event(s)" id="div-soaes" collapsed="true" collapsable="true">
				<c:if test='${command.adverseEventReportingPeriod != null && displaySolicitedTable}'>
    				<table id="solicitedTable" width="100%" class="tablecontent">
    					<tr>
    						<th scope="col" align="left"><b>Select</b></th>
    						<th scope="col" align="left" width="30%"><b>Term</b> </th>
    						<th scope="col" align="left"><b>Grade</b> </th>
    						<th scope="col" align="left"><b>Attribution</b> </th>
   							<th scope="col" align="left"><b>Hospitalization</b> </th>
    						<th scope="col" align="left"><b>Expected</b> </th>
    						<th scope="col" align="left"><b>Serious</b> </th>
							<th scope="col" align="left"><b>Is primary?</b></th>
    					</tr>
    					<tr id="solicitedBlankRow" />
       					<c:forEach items="${command.adverseEventReportingPeriod.reportableAdverseEvents}" varStatus="status" var="ae">
       						<c:if test="${(ae.solicited) and (not ae.requiresReporting)}">
	       						<ae:oneSaeRow index="${status.index}" isAETermOtherSpecify="false" isSolicitedAE="true" adverseEvent="${ae}" aeTermIndex="1" hideDeleteCtrl="true"/>
	       					</c:if>
       					</c:forEach>
       				</table>
       			</c:if>
       			<c:if test='${!displaySolicitedTable}'>
       				No reportable solicited adverse events.
       			</c:if>
			</chrome:division>
			</div>
		</c:if>
		<%-- Till this point was for non-baseline reporting period --%>
		
		
		<%-- This is for baseline reporting period --%> 
		<c:if test="${reportingPeriodType == 'Baseline'}">
			<chrome:division title="Observed Adverse Event(s)" id="div-oaes" collapsable="true">
				<c:if test='${command.adverseEventReportingPeriod != null}'>
        			<table id="observedTable" width="100%" class="tablecontent">
    					<tr>
    						<th scope="col" align="left" width="30%"><b>Term</b> </th>
    						<th scope="col" align="left"><b>Grade</b> </th>
    						<th scope="col" align="left"><b>Attribution</b> </th>
    						<th scope="col" align="left"><b>Hospitalization</b> </th>
    						<th scope="col" align="left"><b>Expected</b> </th>
    						<th scope="col" align="left"><b>Serious</b> </th>
						</tr>
    					<tr id="observedBlankRow" />
    					<c:forEach items="${command.adverseEventReportingPeriod.reportableAdverseEvents}" varStatus="status" var="ae">
            				<c:if test="${not ae.solicited}">
	            				<ae:oneSaeRow index="${status.index}" isSolicitedAE="false" isAETermOtherSpecify="${ae.adverseEventTerm.otherRequired}" adverseEvent="${ae}" aeTermIndex="0" hideDeleteCtrl="true"/>
	            			</c:if>
            			</c:forEach>
            		</table>
        
        		</c:if> 
			</chrome:division>
	
			<chrome:division title="Solicited Adverse Event(s)" id="div-soaes" collapsable="true">
				<c:if test='${command.adverseEventReportingPeriod != null}'>
    				<table id="solicitedTable" width="100%" class="tablecontent">
    					<tr>
    						<th scope="col" align="left" width="30%"><b>Term</b> </th>
    						<th scope="col" align="left"><b>Grade</b> </th>
    						<th scope="col" align="left"><b>Attribution</b> </th>
   							<th scope="col" align="left"><b>Hospitalization</b> </th>
    						<th scope="col" align="left"><b>Expected</b> </th>
    						<th scope="col" align="left"><b>Serious</b> </th>
						</tr>
    					<tr id="solicitedBlankRow" />
       					<c:forEach items="${command.adverseEventReportingPeriod.reportableAdverseEvents}" varStatus="status" var="ae">
       						<c:if test="${ae.solicited}">
	       						<ae:oneSaeRow index="${status.index}" isAETermOtherSpecify="false" isSolicitedAE="true" adverseEvent="${ae}" aeTermIndex="0" hideDeleteCtrl="true"/>
	       					</c:if>
       					</c:forEach>
       				</table>
       			</c:if>	
			</chrome:division>
		</c:if> 
		
  	</jsp:attribute>
  	<jsp:attribute name="tabControls">
  		<div class="content buttons autoclear">
    	<div class="flow-buttons">
        <span class="prev">
                <input type="submit" value="� Back" class="tab1" id="flow-prev"/>
        </span>
        <span class="next">
        	<input type="submit" value="Report" id="flow-next"/>
        </span>
    	</div>
		</div>
  	</jsp:attribute>

	
</tags:tabForm>


<div id="display_options_popup" style="display:none;text-align:left" >
<chrome:box title="Report Create New/ Edit" id="popupId">
	<c:if test="${not empty command.participant}">
		<div align="left"> 
			<div class="row">
			    <div class="summarylabel">Subject</div>
			    <div class="summaryvalue">${command.participant.fullName}</div>
			</div>
			<div class="row">
			    <div class="summarylabel">Study</div>
			    <div class="summaryvalue">${command.study.longTitle}</div>
			</div>
			<div class="row">
				<div class="summarylabel">Evaluation Period</div>
				<div class="summaryvalue">${command.adverseEventReportingPeriod.name}</div>
			</div>
		</div>
	</c:if>
	<chrome:division title="Selected Reports" id="div-selected-reports" collapsable="false">
		<div class="eXtremeTable">
			<table width="60%" border="0" cellspacing="0" align="center" class="tableRegion">
				<thead>
					<tr align="center" class="label">
						<td class="tableHeader">Report</td>
						<td class="tableHeader">Status</td>
					</tr>
				</thead>	
				<c:forEach items="${command.allReportDefinitions}"  var="repDefn" varStatus="rdStatus">
					<tr id="reportDefinitionMap[${repDefn.id}]" style="display:none">
						<td align="left">${repDefn.name}</td>
						<td align="left">${command.reportStatusMap[repDefn.id]}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</chrome:division>
	<div align="left">
		<b>Choose to:</b><br><br><br>
		<input type="button" value="Create New Report(s)" id="create-new-report"/><br><br><br>
		<b>OR</b>
	</div>
	<chrome:division title="Edit In-progress Reports" id="div-report-summary" collapsable="false">
		<div class="eXtremeTable" >
			<table width="100%" border="0" cellspacing="0" class="tableRegion">
				<c:choose>
					<c:when test="${fn:length(command.adverseEventReportingPeriod.aeReports) gt 0}">
						<thead>
							<tr align="center" class="label">
								<td width="5%"/>
								<td class="tableHeader" width="15%">Report Type</td>
								<td class="centerTableHeader" width="20%"># of AEs</td>
								<td class="tableHeader" width="20%">Data Entry Status</td>
								<td class="tableHeader" width="20%">Submission Status</td>
								<td class="centerTableHeader" width="20%">Options</td>
							</tr>
						</thead>
						<c:forEach items="${command.adverseEventReportingPeriod.aeReports}" var="aeReport" varStatus="statusAeReport">
							<ae:oneReviewExpeditedReportRow aeReport="${aeReport}" index="${statusAeReport.index}" />
						</c:forEach>
					</c:when>
					<c:otherwise>
						Reports not present.
					</c:otherwise>	
				</c:choose>					
			</table>
		</div>
	</chrome:division>
	</chrome:box>	
</div>

</body>
</html>