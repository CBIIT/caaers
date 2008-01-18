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
   <tags:includeScriptaculous/>
   <tags:dwrJavascriptLink objects="createStudy"/>
   <script language="JavaScript" type="text/JavaScript">
     var personnelListEditor;
 	 function fireDelete(selected, trClass){
		fireAction('removeStudyPersonnel',selected);
  	 }
     function fireAction(action, selectedPersonnel){
	    if(action == 'addStudyPersonnel'){
		  
	    }else{
	       ValidationManager.validate = false; //dont validate in delete
		   var form = document.getElementById('command')
		   form._target.name='_noname';
		   form._action.value=action;
		   form._selectedPersonnel.value=selectedPersonnel;
		   form.submit();
	    }
     }

     var jsPersonnel = Class.create();
     Object.extend(jsPersonnel.prototype, {
           initialize: function(index, sitePersonnelName) {
            	this.index = index;
            	this.siteIndex = $F('studySiteIndex');
            	this.sitePersonnelName = sitePersonnelName;
            	this.sitePersonnelPropertyName = "studyOrganizations["  + this.siteIndex + "].studyPersonnels[" + index + "].researchStaff";
            	this.sitePersonnelInputId = this.sitePersonnelPropertyName + "-input";
            	if(sitePersonnelName) $(this.sitePersonnelInputId).value = sitePersonnelName;
            	AE.createStandardAutocompleter(this.sitePersonnelPropertyName, 
            		this.sitePersonnelPopulator.bind(this),
            		this.sitePersonnelSelector.bind(this)
            	);
            },            
            sitePersonnelPopulator: function(autocompleter, text) {
         		createStudy.matchResearch(text,this.siteIndex, function(values) {
         			autocompleter.setChoices(values)
         		})
        	},
        	
        	sitePersonnelSelector: function(sPersonnel) { 
        		return sPersonnel.fullName
        	}
        	
     });

     Event.observe(window, "load", function() {
                  
	    //observe on the change event on study site dropdown.
	    Event.observe('studySiteIndex',"change", function(event){
   	      selIndex = $F('studySiteIndex');
		  fireAction('changeSite', selIndex);
	    });
	    
	    //initialize the list editor
	     personnelListEditor = new ListEditor('ssi-table-row',createStudy, "StudyPersonnel",{
             addParameters: [],
             addFirstAfter: "ssi-table-head",
             addCallback: function(nextIndex) {
          	   new jsPersonnel(nextIndex);
          	   if($('ssi-empty-row')){
                	Effect.Fade('ssi-empty-row');
               }
             }
         
    	   });  
     })
     
 function chooseSitesfromSummary(indx){
	var siteSelBox = $('studySiteIndex')
	siteSelBox.selectedIndex = indx + 1;
	fireAction('changeSite', indx);
 }
</script>
</head>

<body>
<study:summary />
<tags:tabForm tab="${tab}" flow="${flow}" formName="form">
  <jsp:attribute name="singleFields">
	<input type="hidden" name="_action" value="">
	<input type="hidden" name="_selectedPersonnel" value="">
	 <input type="hidden" name="_prevSite" value="${command.studySiteIndex}">
	<table border="0" id="table1" cellspacing="1" cellpadding="0" width="100%">
	<tr>
		<td width="70%" valign="top" >
			<p id="instructions">Please choose a study site and link research staff to that study site</p>
			<div class="value"><tags:renderInputs field="${fieldGroups.site.fields[0]}"/><tags:indicator id="ss-chg-indicator"/></div>
			<br />
			<hr>
			<div id="content-section">
				<c:if test="${command.studySiteIndex > -1 }">
					<study:oneStudySitePersonnel index="${command.studySiteIndex}"/>
				</c:if>
				<span id="ss-bookmark" />
			</div>
	    </td>
      	<td valign="top" width="25%">
			<chrome:box title="Summary" id="participant-entry2"  autopad="true">
 				<c:forEach var="studySite" varStatus="status" items="${command.studyOrganizations}">
 					<div class =""><a href="#" onclick="javascript:chooseSitesfromSummary(${status.index});" 
						title="click here to edit research staff assigned to study"> <font size="2"> <b>  ${studySite.organization.name} </b> </font> </a>
 					</div>
 					<div class="">Personnels Assigned: <b> ${fn:length(studySite.studyPersonnels)} </b>
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
  <div id="addStaffBtn" style="${command.studySiteIndex > -1 ? '' : 'display:none'}"><tags:listEditorAddButton divisionClass="ssi-table-row" label="Add Research Staff" /></div>
 </jsp:attribute> 
  
</tags:tabForm>
</body>
</html>
