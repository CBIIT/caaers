<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="study" tagdir="/WEB-INF/tags/study"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>${tab.longTitle}</title>
<style type="text/css">
  .label { 
      width: 12em; 
      text-align: right; 
      padding: 4px; 
  }
</style>

<tags:includeScriptaculous/>
<tags:dwrJavascriptLink objects="createStudy"/>

<script language="JavaScript" type="text/JavaScript">
  var invListEditor;
  function fireDelete(selected, trClass){
	fireAction('removeInv',selected);
  }
	
  function fireAction(action, selectedInvestigator){
	if(action == 'addInv'){
		
	}else{
		ValidationManager.validate = false;
		var form = document.getElementById('command')
		form._target.name='_noname';
		form._action.value=action;
		form._selectedInvestigator.value=selectedInvestigator;
		form.submit();
	}
  }

  var jsInvestigator = Class.create();
  Object.extend(jsInvestigator.prototype, {
           initialize: function(index, siteInvestigatorName) {
            	this.index = index;
            	this.siteIndex = $F('studySiteIndex');
            	this.siteInvestigatorName = siteInvestigatorName;
            	this.siteInvestigatorPropertyName = "studyOrganizations["  + this.siteIndex + "].studyInvestigators[" + index + "].siteInvestigator";
            	this.siteInvestigatorInputId = this.siteInvestigatorPropertyName + "-input";
            	if(siteInvestigatorName) $(this.siteInvestigatorInputId).value = siteInvestigatorName;
            	AE.createStandardAutocompleter(this.siteInvestigatorPropertyName, 
            		this.siteInvestigatorPopulator.bind(this),
            		this.siteInvestigagorSelector.bind(this)
            	);
            },            
            siteInvestigatorPopulator: function(autocompleter, text) {
         		createStudy.matchSiteInvestigator(text,this.siteIndex, function(values) {
         			autocompleter.setChoices(values)
         		})
        	},
        	
        	siteInvestigagorSelector: function(sInvestigator) { 
        		return sInvestigator.investigator.fullName
        	}
        	
  });

  Event.observe(window, "load", function() {
                  
	//observe on the change event on study site dropdown.
	Event.observe('studySiteIndex',"change", function(event){
   	    selIndex = $F('studySiteIndex');
		fireAction('changeSite', selIndex);
	 });

	 //init the list editor
	 invListEditor = new ListEditor('ssi-table-row',createStudy, "Investigator",{
             addParameters: [],
             addFirstAfter: "ssi-table-head",
             addCallback: function(nextIndex) {
          	   new jsInvestigator(nextIndex);
          	   if($('ssi-empty-row')){
                	Effect.Fade('ssi-empty-row');
               }
             }
         
    	});  
	 
  });

  function chooseSitesfromSummary(indx){
	var siteSelBox = $('studySiteIndex')
	siteSelBox.selectedIndex = indx + 1;
	fireAction('changeSite', indx);
  }
</script>
</head>
<body>
<study:summary />
<tags:tabForm tab="${tab}" flow="${flow}" formName="studyInvestigatorForm" hideErrorDetails="true">    
<jsp:attribute name="singleFields">
 <input type="hidden" name="_action" value="">
 <input type="hidden" name="_prevSite" value="${command.studySiteIndex}">
 <input type="hidden" name="_selectedInvestigator" value="">

 <table border="0" id="table1" cellspacing="1" cellpadding="0" width="100%">
	<tr>
		<td width="70%" valign="top" >
		<p> Please choose a study site and link investigators to that study site</p>
		<div class="value"><tags:renderInputs field="${fieldGroups.site.fields[0]}"/><tags:indicator id="ss-chg-indicator"/></div>
		<br />
		<hr>
		<div id="content-section">
			<c:if test="${command.studySiteIndex > -1 }">
				<study:oneStudySiteInvestigator index="${command.studySiteIndex}"/>
			</c:if>
			<span id="ss-bookmark" />
		</div>
	    </td>
      	<td valign="top" width="25%">
			<chrome:box title="Summary" id="participant-entry2" autopad="true">
 				<c:forEach var="studySite" varStatus="status" items="${command.studyOrganizations}">
 					<div class =""><a href="#" onclick="javascript:chooseSitesfromSummary(${status.index});" 
						title="click here to edit investigator assigned to study"> <font size="2"> <b>  ${studySite.organization.name} </b> </font> </a>
 					</div>
 					<div class="">Investigators Assigned: <b> ${fn:length(studySite.studyInvestigators)} </b>
 					</div>
 				
 				</c:forEach>
 				<div>
 				   <img src="/caaers/images/chrome/spacer.gif" width="1" height="150" />
 				</div>
 			</chrome:box>
		</td>
	  </tr>
	</table>
 </jsp:attribute>	
 <jsp:attribute name="localButtons">
  <div id="addInvBtn" style="${command.studySiteIndex > -1 ? '' : 'display:none'}"><tags:listEditorAddButton divisionClass="ssi-table-row" label="Add Investigator" /></div>
 </jsp:attribute> 
</tags:tabForm>
</body>
</html>
