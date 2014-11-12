<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>
<html>
 <head>
 
 <style type="text/css">

  .withdraw{
	color:gray;  
  }
  
  .amend{
  	color:gray; 
  }
  
  .retired{
  	color:gray; 
  }
  
  td.withdraw{
	color:gray;  
  }
  
  td.amend{
  	color:gray; 
  }
  
  td.retired{
  	color:gray; 
  }
  
  div.serious-aes{
  	padding-top: 0.3em;
  	padding-bottom: 0.8em;
  }
  div.recommended-reports{
  	padding-top: 0.2em;
  	padding-bottom: 1.3em;
  }
  div.dcdebug{
      text-align: left;
  }

  div.even{
      background-color: #f5f5dc;
  }
  div.rsuggestion{
      background-color: #bc8f8f;
      font-weight: bold;
  }
  div.rengine{
      background-color: #adff2f;
  }
  div.rset{
      background-color: #e6e6fa;
  }
  div.submittedae{
     color: blue;
     font-style: italic;
  }
  span.dlink{
      color: #f5f5f5;
  }
  	 
 </style>
 
 <script type="text/javascript"><!--

 var continueReportingMsg = "To continue reporting at least one of the following must be selected:";

 AE.checkForModification = false; 
 //to store the recommended options (aeReportId - {reportDefinitionId} 
 AE.recommendedOptions = new Hash();
 //to store the actual recomendations (so that we could reconcile recommended options) (aeReportId  -{reportDefinitionId})
 AE.referenceRecomendedOptions = new Hash();
 
 //to store group report definitions (aeReportId -{GroupId - {reportDefinitionId}}
 AE.groupDefinitions = new Hash();
 
 //to store all the report definitionIds
 AE.reportDefinitions = new Array();
 
 //to  reportDefinitionId - groupId
 AE.reportDefinitionGroupHash = new Hash();

 //to store the reportDefinitionId - ReportDefinition object.
 AE.reportDefinitionHash = new Hash();

 //to store the details of applicable report definition
 //[aeReportId - {reportDefinitionId - reportDefintion}]
 AE.applicableReportDefinitionHash = new Hash();
//same as applicable report definition, but will keep for getting 
//rules suggestion at a later point. 
 AE.referenceReportDefinitionHash = new Hash();

//store all aes and the reports in which they are submitted.
//[adverseEventId : {reportDefinitionId}]
 AE.reportedAEHash = new Hash();

 var checkImgSrc = '<chrome:imageUrl name="../check.png" />';
 var checkNoImgSrc ='<chrome:imageUrl name="../checkno.gif" />';
 var manualSelectMessage='<spring:message code="instruction_ae_override_confirmation" text="Not found key: instruction_ae_override_confirmation " />';
 var reportsWithdawnMessage = '<spring:message code="instruction_ae_report_withdrawn" text="Not found key: instruction_ae_report_withdrawn " />';
 var allCssClassNames = ['create', 'edit', 'withdraw', 'amend'];

 var theAccordion;
 
 //create an object to hold report definition details.
 var jsReportDefinition = Class.create();
 Object.extend(jsReportDefinition.prototype, {
   initialize: function(id, aeReportId, name, group , typeCode, status, grpStatus, otherStatus, due, grpDue, otherDue, action, grpAction, otherAction, childReport, inActive, notStringent) {
	   this.id = id;
	   this.aeReportId = aeReportId;
	   this.name = name;
	   this.group = group;
	   this.typeCode = typeCode;

	   this.status = status;
	   this.grpStatus = grpStatus;
	   this.otherStatus = otherStatus;

	   this.due = due;
	   this.grpDue = grpDue;
	   this.otherDue = otherDue;

	   this.action = action;
	   this.grpAction = grpAction;
	   this.otherAction = otherAction;
       this.childReport = childReport;
       this.inActive = inActive;
       this.notStringent = notStringent;

	   this.trTemplate = '<tr class="#{cssClass}">' + 
	   '<td style="text-align:center;"><input type="checkbox" #{checked} disabled="disabled" /></td>' + 
	   '<td><span class="action-cell"><img src="<chrome:imageUrl name="../blue/#{action}-icon.png" />" alt="" /> #{action}</span></td>' +
  	   '<td><span><b>#{name}</b></span></td>' +
  	   '<td><span>#{status}</span></td>' +
  	   '<td><span>#{due}</span></td>' +
       '</tr>';
	   
   },
   toString : function(){
	   return "#{id},#{aeReportId},#{name}".interpolate({id:this.id, aeReportId:this.aeReportId, name:this.name});
   },
   select: function(){
	   $("rd_"+this.aeReportId+"_"+this.id).checked = true;
   },
   deSelect: function(){
	   $("rd_"+this.aeReportId+"_"+this.id).checked = false;
   },
   getActualAction : function(){
	   return $("rd_"+this.aeReportId+"_"+this.id + "_actualaction").value;
   },
   /*Returns true, if the current report definition is checked*/
   isChecked:function(){
	  return $("rd_"+this.aeReportId+"_"+this.id).checked
   },
   /* Returns true if any report definition in the group is selected, including the current one.*/
   isAnyInGroupChecked : function(){
	   var retVal = false;
	   $('applicable-reports-dc-' + this.aeReportId).select('.' + this.group).each(function(chkBox){
			if(chkBox.checked){
				retVal = true;
			}
	   });
	   
	   return retVal;
   },
   isInvisible : function(){
       return this.childReport || this.inActive || this.notStringent;
   },
   /* function will create and insert a recomended row, for this report definition.*/
   insertRecommendedRow : function(){
	   	var _status = "";
		var _due = "";
		var _action = "";
		var _checked = this.isChecked();

		if(_checked){
			_status = this.status;
			_action = this.action;
			_due = this.due;
		 }else{
			if(this.isAnyInGroupChecked()){
				_status = this.grpStatus;
				_action = this.grpAction;
				_due = this.grpDue;
			}else {
				_status = this.otherStatus;
				_action = this.otherAction;
				_due = this.otherDue;
			}
		}
		
		//insert a row 
		if(_action.length > 0){
			var content = "";
			if(_action == 'Amend'){
				if(_checked){
					content = this.trTemplate.interpolate({cssClass: 'recommended-tr', checked: '', name : this.name, status : this.grpStatus, due : this.grpDue, action : this.grpAction});
					content = content + this.trTemplate.interpolate({cssClass: 'recommended-tr', checked: 'checked="checked"', name : this.name, status : 'Not Started', due : _due, action : 'Create'})
				}else{
					content = this.trTemplate.interpolate({cssClass: 'recommended-tr', checked: '', name : this.name, status : _status, due : _due, action : _action})
				}
				
			}else if(_action == 'Withdraw'){
				content = this.trTemplate.interpolate({cssClass: 'recommended-tr', checked: '', name : this.name, status : _status, due : _due, action : _action})
			}else {
				content = this.trTemplate.interpolate({cssClass: 'recommended-tr', checked: 'checked="checked"', name : this.name, status : _status, due : _due, action : _action})
			}

			var recommendedTRs = $('recommended-reports-dc-' + this.aeReportId).select('.recommended-tr');
			if(recommendedTRs.length > 0){
				recommendedTRs.last().insert({after:content});
			}else{
				$('tr-header-'+ this.aeReportId).insert({after:content});	
			}
			
			
		}
		
		 
   },
   /*function will change the status & due dates of a single report definition row.*/
   updateDisplayTextOfRow: function(){
	   	 var rowIdPrefix = "rd_"+this.aeReportId+"_"+this.id;

		 var _status = "";
		 var _due = "";
		 var _action = "";
		 
		 if(this.isChecked()){
			_status = this.status;
			_action = this.action;
			_due = this.due;
		 }else{
			if(this.isAnyInGroupChecked()){
				_status = this.grpStatus;
				_action = this.grpAction;
				_due = this.grpDue;
			}else {
				_status = this.otherStatus;
				_action = this.otherAction;
				_due = this.otherDue;
			}
		 } 
		 $(rowIdPrefix+"-reportStatus").innerHTML = _status;
		 $(rowIdPrefix+"-reportDue").innerHTML = _due;
		 $(rowIdPrefix+"-reportAction").innerHTML = "<img src='<chrome:imageUrl name='../blue/" + _action + "-icon.png' />' alt='' /> " + _action;
	
		 $(rowIdPrefix+"_actualstatus").value = _status;
		 $(rowIdPrefix+"_actualdue").value = _due;
		 $(rowIdPrefix+"_actualaction").value = _action;
	 
		 //update the CSS classes
		 var newCssClass = '';
		 if(_action){
			 newCssClass = _action.toLowerCase();
		 }
		 var tr = $($(rowIdPrefix+"_actualaction").parentNode.parentNode);
		 allCssClassNames.each(function(cssClass){
			 tr.removeClassName(cssClass);
		 });
	
		 tr.childElements().each(function(td){
			 allCssClassNames.each(function(cssClass){
				 td.removeClassName(cssClass);
			 });
		 });
	
		 
		 if(newCssClass){
			 tr.addClassName(newCssClass);
			 tr.childElements().each(function(td){
				 td.addClassName(newCssClass);
			 });
		 }
   },
   /*This method will set the object attributes to the values it should have when it is getting amended.*/
   forceAmend : function(){
	   this.status = "Being amended";
	   this.grpStatus = "Being amended";
	   this.otherStatus = "";

	   this.grpDue = "Submitted";
	   this.otherDue = "";
	   
	   this.action = "Amend";
	   this.grpAction ="Amend";
   },
   /*This method will set the manual selection flag*/
   setManualSelectionFlag:function(){
	   $('rd_' + this.aeReportId + '_' + this.id +'_manual').value=1;
	   //caaersLog("setManualSelectionFlag [aeReportId :" + this.aeReportId + ", rdId : " + this.id  );
   },
   /*This method will unset the manual selection flag*/
   unsetManualSelectionFlag:function(){
	   $('rd_' + this.aeReportId + '_' + this.id +'_manual').value=0;
	   //caaersLog("unsetManualSelectionFlag [aeReportId :" + this.aeReportId + ", rdId : " + this.id  );
   },
   deepCopy:function(){
	   return new jsReportDefinition(this.id, this.aeReportId,  this.name, this.group, this.typeCode, 
			   this.status, this.grpStatus, this.otherStatus, 
			   this.due, this.grpDue, this.otherDue, 
			   this.action, this.grpAction, this.otherAction, this.isChecked, this.inActive, this.notStringent);
   }
 });

 //=================================================================================
 //will show manually select, only applicable when there is no rules recomendation.
 function showManualSelectOptions(applicableSectionId, aeReportId){
	 var answer = confirm(manualSelectMessage);
	 if(!answer) return;
	 $(applicableSectionId).appear();
	 $('no-recommended-reports-dc-' + aeReportId).hide();
 }
 
 //=================================================================================
 //will show manually select display and hide recommended DIV
 function overrideRecommendedActions(recommendedSectionId, applicableSectionId, aeReportId){
	 var answer = confirm(manualSelectMessage);
	 if(!answer) return;
	 
	 selectRecommendedReports(aeReportId);
	 updateDisplayTexts(aeReportId);
	 
	 $(recommendedSectionId).fade();
	 $(applicableSectionId).appear();
	
	 $('dc-' + aeReportId + '-override').hide();
	 $('dc-' + aeReportId + '-restore').show();
	 
 }
 //=================================================================================
 //will unset the manual indicatior flag.
 function clearManualIndicatorFlag(aeReportId){
	 AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
	   rdObj.unsetManualSelectionFlag();
	 });
 }
 //=================================================================================
 //will show recommended options and will hide manually select DIV
 function restoreRecommended(recommendedSectionId, applicableSectionId, aeReportId){
	selectRecommendedReports(aeReportId);
	updateDisplayTexts(aeReportId);
	
	$(applicableSectionId).fade();
	$(recommendedSectionId).appear();

	clearManualIndicatorFlag(aeReportId);
	
	$('dc-' + aeReportId + '-override').show();
	$('dc-' + aeReportId + '-restore').hide();
 }
 //=================================================================================
 //function will check the recommended items
 function selectRecommendedReports(aeReportId){
	var rpHash = AE.applicableReportDefinitionHash.get(aeReportId);
	//deselect all
	rpHash.values().each(function(rdObj){
		 rdObj.deSelect();
	});

	 //check the recommended ones
	 AE.recommendedOptions.get(aeReportId).each(function(rdId){
		 rpHash.get(rdId).select();
	 });
     
 }
//=================================================================================
 //deselect reports of the same group.
 function deselectOtherReportsOfSameGroup(aeReportId, rdId, group){
	 AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
		if((rdId != rdObj.id) && (rdObj.group == group)){
			rdObj.deSelect();
			rdObj.unsetManualSelectionFlag();
		}
	});
 }
//=================================================================================
 /*This function handles, when someone clicks on  a report definition */	
 function handleReportSelection(aeReportId, rdId){
	 var curRdObject = AE.applicableReportDefinitionHash.get(aeReportId).get(rdId);
	 
	 //check if there is at least one ae. 
	 var selectedAEs = findSelectedAdverseEvents(aeReportId);
	 if(selectedAEs.length == 0){
		 curRdObject.deSelect();
		//update the display text.
		 updateDisplayTexts(aeReportId);
         alert('Select at least one adverse event before selecting report');
		 return;
	 }
	 //caaersLog("handleReportSelection [aeReportId :" + aeReportId + ", rdId : " + rdId  + ", curRdObject : " + curRdObject.id);
	//deselect reports of the same group.
	curRdObject.setManualSelectionFlag();
	deselectOtherReportsOfSameGroup(aeReportId,rdId, curRdObject.group);
	//update the display text.
	updateDisplayTexts(aeReportId);

     warnUserAboutRulesRecomendation(aeReportId);

 }

 //=================================================================================
 /* Will warn the user that at least one report must be selected */
function warnUserAboutRulesRecomendation(aeReportId){
    var recommendedRdIds = AE.referenceRecomendedOptions.get(aeReportId);
    if(recommendedRdIds.length == 0) return false;

    var rdNames = "";
    var groupList = findOptedOutRecommendedGroups(aeReportId);
    groupList.each(function(grpName){
        var rdIds = AE.groupDefinitions.get(aeReportId).get(grpName);
        if(rdIds){
            for(var i = 0; i <rdIds.length ; i++){
                var _rdObj =  AE.referenceReportDefinitionHash.get(aeReportId).get(rdIds[i]);
                if(_rdObj.isInvisible()) continue;
                rdNames = rdNames + "\r\n ";
                rdNames = rdNames + _rdObj.name;
            }
        }
    });

    if(rdNames.length > 0){
        alert(continueReportingMsg + rdNames);
        return true;
    }
    return false;

}


//=================================================================================
/*This function return an array group names that are recommended by rules but has not selected*/
function findOptedOutRecommendedGroups(aeReportId){
    var rdObjects =   new Array();
    var rdIds = AE.referenceRecomendedOptions.get(aeReportId);
    rdIds.each(function(rdId){
        rdObjects.push(AE.referenceReportDefinitionHash.get(aeReportId).get(rdId));
    });
    var groupList = new Array();
    for(var i = 0; i< rdObjects.length ;i++){
        if(rdObjects[i].isChecked() || rdObjects[i].isAnyInGroupChecked()) continue;
        if(groupList.indexOf(rdObjects[i].group) > -1) continue;
        groupList.push(rdObjects[i].group)
    }
    return groupList;
}


//=================================================================================
/*This function return an array of all the aes that unselected currently , under a datacollection*/
function findDeselectedAdverseEvents(aeReportId){
	var aes = new Array();
	$('adverseEvents-dc-' + aeReportId).select('.ae_'+aeReportId).each(function(chkBox){
		if(!chkBox.checked){
			aes.push(chkBox.value);
		}
	});
	return aes;
}


//=================================================================================
/*This function return an array of all the aes that selected , under a datacollection*/
function findSelectedAdverseEvents(aeReportId){
	var aes = new Array();
	$('adverseEvents-dc-' + aeReportId).select('.ae_'+aeReportId).each(function(chkBox){
		if(chkBox.checked){
			aes.push(chkBox.value);
		}
	});
	return aes;
}
 
//=================================================================================
 /*This function will handle the click associated to ae*/
 function handleAdverseEventSelection(aeReportId, aeId, reportedFlag){

	  //enable /disable primary ae radio button
	  if(!$("ae-" + aeReportId + "-" + aeId).checked){
		 var primaryField = $("ae-" + aeReportId + "-" + aeId + "-primary")
		 primaryField.checked = false;
		 primaryField.disabled = true;
	  }else{
		  var primaryField = $("ae-" + aeReportId + "-" + aeId + "-primary")
	      primaryField.disabled = false;
	  }
			   
	 
	 //find all selected adverse events. 
	 var selectedAEs = findSelectedAdverseEvents(aeReportId);
     var hasActualAction = hasActualActionOnReports(aeReportId);
	 $('report-btn-' + aeReportId).disabled = (selectedAEs.length < 1) || !hasActualAction;
	 if(selectedAEs.length < 1){

	
		 //none of the AEs are selected,so deselect all the report definitions.
		 AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
			rdObj.deSelect();
		});	 
			
		//clear off all manual selection flag
		clearManualIndicatorFlag(aeReportId);

		 //update the display.
		 updateDisplayTexts(aeReportId);
		 
		//enable/disable report button based on the action
		if(aeReportId != 0) $('report-btn-' + aeReportId).disabled = !isOnlyActionWithdraw(aeReportId)  ; 	
				 
	 }else if(reportedFlag){

		 //reset every thing, so that we are on the orignal state. 
		 resetRecommendedOptions(aeReportId);
		 resetApplicableReportDefinitions(aeReportId);

			
		//find the deselected adverse events
		 var deselectedAEs = findDeselectedAdverseEvents(aeReportId);
		 var forceAmendList = new Array();
		 if(deselectedAEs.length > 0){
			//see if the action of that rd is amend or withdraw. If not then...force amend it.
			 deselectedAEs.each(function (deselectedAeId){
				 AE.reportedAEHash.get(deselectedAeId).each(function(rdId){
					 var rdObj = AE.applicableReportDefinitionHash.get(aeReportId).get(rdId);
					 var actualAction = rdObj.getActualAction();
					 if(actualAction != 'Amend' ){
					 	forceAmendList.push(rdObj);
					 }
				 });
			 });

		 }

		 forceAmendList.each(function(rdObj){
			 //force amend
			 rdObj.forceAmend();
			 //push it to recomended options.
			 AE.recommendedOptions.get(aeReportId).push(rdObj.id);
			 //deselect others from the same group.
			 deselectOtherReportsOfSameGroup(aeReportId,rdObj.id, rdObj.group);
		 });

		
		 
		 //select recomended reports.
		 selectRecommendedReports(aeReportId);

		 //clear off all manual selection flag
		 clearManualIndicatorFlag(aeReportId);

		 //update the display.
		 updateDisplayTexts(aeReportId);

		 var applicableDiv = $('applicable-reports-dc-' + aeReportId);
		 var headerDiv = $('reports-header-dc-' + aeReportId);
		 var noRecomendationDiv = $('no-recommended-reports-dc-' + aeReportId);
		 var recomendationDiv =  $('recommended-reports-dc-' + aeReportId);
		 var recomendedOptions = AE.recommendedOptions.get(aeReportId);
		 var reqMsgDiv =  $('rulesMessage-'+ aeReportId + '-required');
		 var notReqMsgDiv =  $('rulesMessage-'+ aeReportId + '-not-required')

		 //if there are recomended options, header should be visible & noRecomendation should be hidden
		 if(recomendedOptions.length > 0){
			 noRecomendationDiv.hide();
			 notReqMsgDiv.hide();
			 reqMsgDiv.appear();
			 headerDiv.appear();
			 //if applicableDiv is hidden, then only show recomendationDiv
			 if(!applicableDiv.visible()){
				 recomendationDiv.appear();
				 $('dc-' + aeReportId + '-override').show();
				 $('dc-' + aeReportId + '-restore').hide();
			 }else{
				 $('dc-' + aeReportId + '-override').hide();
				 $('dc-' + aeReportId + '-restore').show();
			 }
		 }else{
			 if(!applicableDiv.visible()){
			 	noRecomendationDiv.appear();
			 }
			 notReqMsgDiv.appear();
			 reqMsgDiv.hide();
			 headerDiv.hide();
			 recomendationDiv.hide();
		 }
	 }
	
 }


 
//=================================================================================
/*This function will update the header for primary AE*/	
function handlePrimaryAdverseEvent(aeReportId, aeId, aeTerm, grade){
	$('dc-section-' + aeReportId).innerHTML= aeTerm + ", " + grade;
	$("ae-" + aeReportId + "-" + aeId ).checked = true;
}
 
//=================================================================================
 /*This function will reset the recommended options*/
 function resetRecommendedOptions(aeReportId){
	 AE.recommendedOptions.unset(aeReportId);
	 AE.recommendedOptions.set(aeReportId, AE.referenceRecomendedOptions.get(aeReportId).clone());
 }
//=================================================================================
  /*This method will reset the recomended report defs display*/
 function clearRecommendedReportsDisplay(aeReportId){
	 //remove every tr having .forced-tr class
	$('recommended-reports-dc-' + aeReportId).select('.recommended-tr').each(function(tr){
		tr.remove();
	}); 
 }
 //=================================================================================
 /*Will reset the applicable report definition hash, from the reference. */
 function resetApplicableReportDefinitions(aeReportId){
	var refHash =  AE.referenceReportDefinitionHash.get(aeReportId);
	var newHash = new Hash();
	refHash.each(function(pair){
		newHash.set(pair.key, pair.value.deepCopy());
	});
	 
	 AE.applicableReportDefinitionHash.unset(aeReportId);
	 AE.applicableReportDefinitionHash.set(aeReportId,newHash);
 }
//=================================================================================
 //function will change the status , recommended display & due dates of report definitions.
 function updateDisplayTexts(aeReportId){

	 //remove the recomended display options. 
	 clearRecommendedReportsDisplay(aeReportId);
	 
	 AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
		 rdObj.updateDisplayTextOfRow();
		 rdObj.insertRecommendedRow();
	 });

	//update the rules engine messages area.
	var msgs = generateMessages(aeReportId);
	var htmlMsg = "";
	msgs.each(function(msg){
		htmlMsg = htmlMsg + "<li>" + msg + "</li>";
	});
	$('rulesMessageList-' + aeReportId).innerHTML = htmlMsg;

     toggleReportingButton(aeReportId);

 }
//=================================================================================
function showNewDataCollection(){
	$('new-dc-section-0').show();
	$('add-dc-btn-row').remove();
	theAccordion.expand($('dc-section-0'));
}
//=================================================================================
/*This function will generate the summary display*/	
function generateMessages(aeReportId){
	var processed = new Array();
	var messages = new Array();

	AE.reportDefinitions.each(function(rdId){
		
		if(processed.indexOf(rdId) > 0) return;
		
		var rdObj = AE.applicableReportDefinitionHash.get(aeReportId).get(rdId);
		var actualAction = $('rd_'+aeReportId+'_'+rdId+'_actualaction').value;
		var grpRdArray = AE.groupDefinitions.get(aeReportId).get(rdObj.group);
		
		processed.push(rdId);
		
		var msg = "";
		var connector = "";
		if(actualAction){
			if(actualAction == 'Create'){
				var otherActionAmendOrWithdraw = false;
				if(grpRdArray){
					grpRdArray.each(function(otherRdId){
						var otherAction = $('rd_'+aeReportId+'_'+otherRdId+'_actualaction').value;
						if(otherAction == 'Withdraw' || otherAction == 'Amend'){
							otherActionAmendOrWithdraw = true;
						}
					});
				}
				if(!otherActionAmendOrWithdraw){
					msg = "<img src='<chrome:imageUrl name='../blue/" + actualAction + "-icon.png' />' alt='' class='action-icon' /> <span style='font-weight:bold'>" + actualAction + "</span> " + rdObj.name;
				}
			}else if(actualAction == 'Edit'){
				msg = "<img src='<chrome:imageUrl name='../blue/" + actualAction + "-icon.png' />' alt='' class='action-icon' /> <span style='font-weight:bold'>" + actualAction + "</span> " + rdObj.name;	
			}else if(actualAction == 'Withdraw'){
				msg = "<img src='<chrome:imageUrl name='../blue/" + actualAction + "-icon.png' />' alt='' class='action-icon' /> <span style='font-weight:bold'>" + actualAction + "</span> " + rdObj.name;
				connector = " and replace with";
			}else if(actualAction == 'Amend'){
				msg = "<img src='<chrome:imageUrl name='../blue/" + actualAction + "-icon.png' />' alt='' class='action-icon' /> <span style='font-weight:bold'>" + actualAction + "</span> " + rdObj.name;
				connector = " with";
			}

			if(actualAction == 'Withdraw' || actualAction == 'Amend'){			
				var otherSelected = selectedReportDefinitionsFromGroup(aeReportId,rdObj.group);
				if(otherSelected.length > 0){
					msg = msg + connector;
					otherSelected.each(function(otherId){
						var rdOther = AE.applicableReportDefinitionHash.get(aeReportId).get(otherId);
						msg = msg + " " + rdOther.name;
						processed.push(otherId);
					});
				}
			}

			if(msg) messages.push(msg);
			
		}
		
	});

	return messages;
}
//=================================================================================
//function will return true if the only action is withdraw.
function isOnlyActionWithdraw(aeReportId){
	var _onlyWithdrawAction = true;
	//check for actual action.
	AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
		if(rdObj.getActualAction() && rdObj.getActualAction() != 'Withdraw'){
			_onlyWithdrawAction = false;
		}
	});
	return _onlyWithdrawAction;
}

 //=================================================================================
//function will return true if the only action is withdraw.
function isOnlyActionAmend(aeReportId){

    var _amendCnt = 0;
    var _createCnt = 0;
    
	//check for actual action.
	AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
        var actualAction   = rdObj.getActualAction();
        if(actualAction == 'Amend') _amendCnt = _amendCnt + 1;
        if(actualAction == 'Create') _createCnt = _createCnt + 1;
	});
    
	return (_amendCnt > 0) && (_amendCnt == _createCnt) ;
}

//=================================================================================
//function will return the ids, of the report definitions checked from the same group.
function selectedReportDefinitionsFromGroup(aeReportId, groupName){
	var reportDefs = new Array();
	var grpRdArray = AE.groupDefinitions.get(aeReportId).get(groupName);
	grpRdArray.each(function(rd){
		if($("rd_"+aeReportId+"_"+rd).checked){
			reportDefs.push(rd);
		 }
	 });
	return reportDefs;
}

//=================================================================================
//function will submit the report to server. 
function forwardToReport(aeReportId, frm){
	
	if(!validate(aeReportId)){
		return;
	}

	if(AE.SUBMISSION_INPROGRESS){
		 return;
	}
	
	$('activeAeReportId').value = aeReportId;

	//validations
	
	//confirm withdrawls
	 var withdrawnReports = "";
	 AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){
		 if(rdObj.getActualAction() == 'Withdraw'){
			 if (withdrawnReports.length > 0){
				 withdrawnReports = withdrawnReports + "\n";
			 }
			 withdrawnReports = withdrawnReports +  rdObj.name;
		 }
	 });

	 if(withdrawnReports.length > 0){
		if(!confirm(reportsWithdawnMessage + "\n\n" + withdrawnReports)){
			return;
		} 
	
	 }
	 
	 AE.SUBMISSION_INPROGRESS = true;
	 //submit the form
	 frm.submit();
	
	
	
}


//=================================================================================
// will check if the report is selected or not.
function hasActualActionOnReports(aeReportId){
    var hasActualAction = false;
	//check for actual action.
	AE.applicableReportDefinitionHash.get(aeReportId).values().each(function(rdObj){

		if(rdObj.getActualAction()){
			hasActualAction = true;
		}
	});
    return hasActualAction;
}



//==================================================================================
// will check if the report is selected or not, then will enable disable the report.
 
 function toggleReportingButton(aeReportId) {
     var hasOptedOutGroups = findOptedOutRecommendedGroups(aeReportId).length > 0;

     var hasActualAction = hasActualActionOnReports(aeReportId);
     var enableReporting = hasActualAction && (!hasOptedOutGroups);


     // change the SUBMIT button and the message
     if (enableReporting) {
         jQuery('#report-btn-' + aeReportId).removeAttr('disabled');
         if (jQuery('#rulesMessage-' + aeReportId)) jQuery('#rulesMessage-' + aeReportId).show();
         if (jQuery('#rulesMessageNone-' + aeReportId)) jQuery('#rulesMessageNone-' + aeReportId).hide();

     } else {
         jQuery('#report-btn-' + aeReportId).attr('disabled', 'false');
         if (jQuery('#rulesMessage-' + aeReportId)) jQuery('#rulesMessage-' + aeReportId).hide();
         if (jQuery('#rulesMessageNone-' + aeReportId)) jQuery('#rulesMessageNone-' + aeReportId).show();
     }

     //update the report button
     var onlyWithdraw = isOnlyActionWithdraw(aeReportId);
     var onlyAmend = isOnlyActionAmend(aeReportId);
     var _reportButtonText = "Report";
     if(onlyWithdraw && hasActualAction) _reportButtonText = 'Withdraw';
     if(onlyAmend) _reportButtonText = 'Amend';

     jQuery('#report-btn-' + aeReportId + '-value').text(_reportButtonText);
    
 }

//=================================================================================
// will validate the input
function validate(aeReportId){
	//determine if there is a create-edit action?
	var createOrEditAction = false;
	var noActualAction = true;
	var onlyWithdrawAction = true;
	var noPrimaryAE = true;

    //no actual action
    noActualAction = !hasActualActionOnReports(aeReportId);

    //onlyWithdraw?
	onlyWithdrawAction = isOnlyActionWithdraw(aeReportId);
	
	var selectedAEs = findSelectedAdverseEvents(aeReportId);
	if(selectedAEs.length < 1 && !onlyWithdrawAction){
		alert("At least one adverse event should be selected");
		return false;
	}
	
	//make sure, atleast one actual action is there. 
	if(noActualAction){
		alert("At least one report should be selected");
		return false;
	}

	$$('.ae_' + aeReportId + '_primary').each(function (el){
		if(el.checked){
			noPrimaryAE = false;
		}
	});
	
	if(noPrimaryAE && !onlyWithdrawAction){
		alert('At least one primary adverse event should be selected');
		return false;
	}


	return true;
}

 --></script>
 
 </head>
 <body>
  <tags:tabForm tab="${tab}" flow="${flow}" formName="review" hideBox="true">
   <jsp:attribute name="singleFields">
   <caaers:message code="continueReportingMsg" text="In order to continue reporting, select one of the below mentioned reports :" var="_continueReportingMsg"/>
   <script type="text/javascript">
   continueReportingMsg = '${_continueReportingMsg}';

   Event.observe(window, "load", function() {
        var rdObject = null;
        var rdObjectCopy = null;

		//remove the query string from form url
		removeQueryStringFromForm('command');
	   
		//initialize accordion
		theAccordion = new Accordion("review-content", 1);
		//initialize the submitted ae datastructure
		<c:forEach var="aeEntry" items="${command.evaluationResult.reportedAEIndexMap}">
			var rdIdArr = new Array();
			<c:forEach var="rd" items="${aeEntry.value}" >
			 rdIdArr.push(${rd.id});
			</c:forEach>
			AE.reportedAEHash.set(${aeEntry.key}, rdIdArr);
		</c:forEach>
	   //initialize the datastructure. 
	   <c:forEach var="entry" items="${command.applicableReportTableMap}" >
	    AE.recommendedOptions.set(${entry.key}, new Array());
	    AE.referenceRecomendedOptions.set(${entry.key}, new Array());
	    AE.groupDefinitions.set(${entry.key}, new Hash());
	    
	    AE.applicableReportDefinitionHash.set(${entry.key}, new Hash());
	    AE.referenceReportDefinitionHash.set(${entry.key}, new Hash());
	    
	    var grp_${entry.key} = null;
	    
	    <c:forEach var="row" items="${entry.value}">
	     <c:if test="${row.preSelected}">
	      AE.recommendedOptions.get(${entry.key}).push(${row.reportDefinition.id});
	      AE.referenceRecomendedOptions.get(${entry.key}).push(${row.reportDefinition.id});
	     </c:if>

	     //add the group info
	     grp_${entry.key} = AE.groupDefinitions.get(${entry.key})
	     if( !(grp_${entry.key}.get('${row.group}')) ){
	    	 grp_${entry.key}.set('${row.group}', new Array());
	     }
	     grp_${entry.key}.get('${row.group}').push(${row.reportDefinition.id});

	     //add the report definition IDs
	     AE.reportDefinitions.push(${row.reportDefinition.id});

	     //put the reportdefintion-id : group
	     AE.reportDefinitionGroupHash.set(${row.reportDefinition.id}, '${row.group}');

	     //create and store applicableReportDefinitions.
		 rdObject =  new jsReportDefinition(${row.reportDefinition.id}, ${entry.key},
			     "${row.reportDefinition.label}" , "${row.group}" , "${row.reportDefinition.reportType.code}",
			     "${row.status}", "${row.grpStatus}", "${row.otherStatus}",
			     "${row.due}", "${row.grpDue}", "${row.otherDue}",
			     "${row.action.displayName}", "${row.grpAction.displayName}", "${row.otherAction.displayName}",
			      ${not empty row.reportDefinition.parent},
			      ${not row.reportDefinition.enabled},
			      ${not row.stringent});
         rdObjectCopy =  new jsReportDefinition(${row.reportDefinition.id}, ${entry.key},
                        "${row.reportDefinition.label}" , "${row.group}" , "${row.reportDefinition.reportType.code}",
                        "${row.status}", "${row.grpStatus}", "${row.otherStatus}",
                        "${row.due}", "${row.grpDue}", "${row.otherDue}",
                        "${row.action.displayName}", "${row.grpAction.displayName}", "${row.otherAction.displayName}",
                         ${not empty row.reportDefinition.parent},
                         ${not row.reportDefinition.enabled},
                         ${not row.stringent});


		 AE.applicableReportDefinitionHash.get(${entry.key}).set(rdObject.id, rdObject);

		 AE.referenceReportDefinitionHash.get(${entry.key}).set(rdObject.id, rdObjectCopy);
		 
	    </c:forEach>

	    AE.reportDefinitions = AE.reportDefinitions.uniq();
	    
	    //default select the recommended report definitions
	    selectRecommendedReports(${entry.key});

		//update the statuses
		updateDisplayTexts(${entry.key});
	    
	  </c:forEach>
		if (${fn:length(command.adverseEventReportingPeriod.aeReports) > 0} && isOnlyActionWithdraw(${command.adverseEventReportingPeriod.aeReports[0].id}) && ! ${command.evaluationResult.alertRecommended}) {
			// show stop sign when only withdraw action is suggested
			$('alertBox').style.display = '';
		}
   });

   </script>


	<!--  for processing -->
	<input type="hidden" name="activeAeReportId" value="" id="activeAeReportId" />
	<input type="hidden" name="_finish"/>
	<!--  ============== -->
  
   <div id="alertBox" style="border:1px solid #f00; height:100px; padding:9px; margin-bottom:10px; margin-left:150px; width:600px;display:none">
		<img src="<chrome:imageUrl name="stop_sign.png" />" alt="Stop!" style="float:left; margin-right:30px; margin-left:40px;" />
		<div style="font-size:20px; margin-bottom:5px; margin-top:35px;"><tags:message key="instruction_ae_action_recommended" /></div>
    </div>
  
   <!--  ALERT -->
   <c:if test="${command.evaluationResult.alertRecommended}">
    <div style="border:1px solid #f00; height:100px; padding:9px; margin-bottom:10px; margin-left:150px; width:600px;">
		<img src="<chrome:imageUrl name="stop_sign.png" />" alt="Stop!" style="float:left; margin-right:30px; margin-left:40px;" />
		<div style="font-size:20px; margin-bottom:5px; margin-top:35px;"><tags:message key="instruction_ae_action_recommended" /></div>
    </div>
   </c:if>
   <!--  ALERT END -->
  <div id="review-content"> 
   <c:set var="noOfAEReports" value="${fn:length(command.adverseEventReportingPeriod.aeReports)}" />
   <c:set var="noOfNewAe" value="${fn:length(command.adverseEventReportingPeriod.nonExpeditedAdverseEvents)}" />
   
	<!--  HAS Expedited Reports -->
	<c:if test="${noOfAEReports gt 0}">
		<c:forEach var="aeReport" items="${command.adverseEventReportingPeriod.aeReports}" varStatus="aeReportStatus">
		<c:set var="_aeReportId" value="${aeReport.id}" />
		<c:set var ="_primaryAE" value="${fn:length(aeReport.adverseEvents) gt 0 ? aeReport.adverseEvents[0] : null}" />
		<c:set var="_rulesMsgs" value="${command.rulesEngineMessageMap[_aeReportId]}" />
		<chrome:accordion id="dc-section-${_aeReportId}"  >
			<jsp:attribute name="title">
                ${empty _primaryAE ? '' : _primaryAE.adverseEventTerm.universalTerm}, Grade ${empty _primaryAE ? '' : _primaryAE.grade.code}: ${empty _primaryAE ? '' : _primaryAE.grade.displayName}
			</jsp:attribute>
            <jsp:body>
            <c:set var="_rulesMsgs" value="${command.rulesEngineMessageMap[_aeReportId]}" />

            <caaers:message code="section.header.recomended.actions" var="_recomendedActionsHeader" text="Recommended Actions" />
			<caaers:message code="section.header.available.actions" var="_availableActionsHeader" text="Available Actions" />
			<chrome:division title="${command.evaluationResult.aeReportAlertMap[_aeReportId] ? _recomendedActionsHeader : _availableActionsHeader}">
			<!--  Rules Message Top -->
			 <ae:rulesMessageTop rulesMessages="${_rulesMsgs}" aeReportId="${_aeReportId}"  alertShown="${command.evaluationResult.aeReportAlertMap[_aeReportId]}"/>
			
			<!--  Listing the reports -->
			<ae:recommendedReportRow applicableTableRows="${command.applicableReportTableMap[_aeReportId]}" 
				recommendedTableRows="${command.recommendedReportTableMap[_aeReportId]}"
			    aeReportId="${_aeReportId}" />
			</chrome:division>
            <%-- Check the report defs for all AE inclusion --%>
            <c:set var="includeAllAes" value="false" />
            <c:forEach var="aRRow" items="${command.recommendedReportTableMap[_aeReportId]}">
                <c:set var="includeAllAes" value="${includeAllAes or aRRow.includeNonSeriousAes}" />
            </c:forEach>
			<!--  Listing of adverse events -->
			<ae:seriousAdverseEvents adverseEvents="${command.evaluationResult.allAeMap[_aeReportId]}" aeReportId="${_aeReportId}" 
				primaryAeId="${empty _primaryAE ? 0 : _primaryAE.id}"  selectNonSerious="${includeAllAes}" />
			<!--  Rules Message Bottom -->
			<div class="rulesMessageBottom">
			 	<ae:rulesMessageBottom rulesMessages="${_rulesMsgs}" aeReportId="${_aeReportId}" />
				<div class="row" style="text-align:right;">
			 		<tags:button id="report-btn-${_aeReportId}" type="button" onclick="forwardToReport(${_aeReportId}, this.form);" value="Report" color="green" icon="continue" />
                    <span onclick="showMessagePopup('dc-eval-debug-${_aeReportId}',{width:800, height:335, resizable:true, draggable:true, closable:true, maximizable:true})" class="dlink"> .</span>
				</div>

			</div>
           </jsp:body>
		</chrome:accordion>
			
		</c:forEach>
	</c:if>
	
	<!--  New data collection -->
	<c:set var="_aeReportId" value="${command.zero}" />
	<c:set var ="_primaryAE" value="${command.evaluationResult.allAeMap[_aeReportId][0]}" />
	<c:set var="_rulesMsgs" value="${command.rulesEngineMessageMap[_aeReportId]}" />
	<div id="new-dc-section-0" style="${noOfAEReports gt 0 ? 'display:none;' : ''}">
    <c:set var="gradeText">
        <jsp:attribute name="value">
            <c:if test="${_primaryAE.grade != null}">
                ,&nbsp;Grade:  ${_primaryAE.grade.code}: ${_primaryAE.grade.displayName}
            </c:if>
        </jsp:attribute>
    </c:set>
	<chrome:accordion  id="dc-section-0" >
        <jsp:attribute name="title">${_primaryAE.adverseEventTerm.universalTerm} ${gradeText}</jsp:attribute>
        <jsp:body>
        <caaers:message code="section.header.recomended.actions" var="_recomendedActionsHeader" text="Recommended Actions" />
        <caaers:message code="section.header.available.actions" var="_availableActionsHeader" text="Available Actions" />

		<chrome:division title="${command.evaluationResult.aeReportAlertMap[_aeReportId] ? _recomendedActionsHeader : _availableActionsHeader}">
		<!--  Rules Message Top -->
		 <ae:rulesMessageTop rulesMessages="${_rulesMsgs}" aeReportId="${_aeReportId}" alertShown="${command.evaluationResult.aeReportAlertMap[_aeReportId]}"/>
		
		<!--  Listing the reports -->
		<ae:recommendedReportRow applicableTableRows="${command.applicableReportTableMap[_aeReportId]}" recommendedTableRows="${command.recommendedReportTableMap[_aeReportId]}" aeReportId="${_aeReportId}" />
		</chrome:division>
        <%-- Check the report defs for all AE inclusion --%>
        <c:set var="includeAllAes" value="false" />
        <c:forEach var="aRRow" items="${command.recommendedReportTableMap[_aeReportId]}">
            <c:set var="includeAllAes" value="${includeAllAes or aRRow.includeNonSeriousAes}" />
        </c:forEach>
        <!--  Listing of adverse events -->
		<ae:seriousAdverseEvents adverseEvents="${command.evaluationResult.allAeMap[_aeReportId]}" aeReportId="${_aeReportId}" primaryAeId="${_primaryAE.id}" selectNonSerious="${includeAllAes}" />

        <!--  Rules Message Bottom -->
		<div class="rulesMessageBottom">
			<ae:rulesMessageBottom rulesMessages="${_rulesMsgs}" aeReportId="0" />
			<div class="row" style="text-align:right;">
			 	<tags:button id="report-btn-0" type="button" onclick="forwardToReport(0, this.form);" value="Report" color="green" icon="continue" /> <span onclick="showMessagePopup('dc-eval-debug-0', {width:800, height:335, resizable:true, draggable:true, closable:true, maximizable:true})"  class="dlink"> .</span>
			</div>

		</div>
       </jsp:body>
	</chrome:accordion>	
	</div>
	
   </div>
	
   <!--  Add new DC Button -->		
   <c:if test="${(noOfAEReports gt 0) and (noOfNewAe gt 0)}">
    <div id="add-dc-btn-row">
   	 <tags:button type="button" icon="add" id="add-dc-btn" value="Add Report Collection" color="green"  size="small" onclick="showNewDataCollection()" />
    </div>
   </c:if>
          <ae:evaluationDebug result="${command.evaluationResult}" />
   </jsp:attribute>
   
   <jsp:attribute name="tabControls">
      <div class="content buttons autoclear">
          <div class="flow-buttons">
            <span class="prev">
              	<tags:button type="submit" value="Back" cssClass="tab1" color="blue" icon="back" id="flow-prev"/>
			</span>
          </div>
      </div>
  </jsp:attribute>
  </tags:tabForm>
 </body>
</html>
