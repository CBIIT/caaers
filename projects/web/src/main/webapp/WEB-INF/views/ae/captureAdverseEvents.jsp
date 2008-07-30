<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<!-- <link rel="stylesheet" type="text/css" href="<c:url value="/css/extremecomponents.css"/>"> -->
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<script type="text/javascript" src="/caaers/js/dropdown_menu.js"></script>
<html>
 <head>
 <tags:includeScriptaculous />
 <tags:includePrototypeWindow />
 <tags:stylesheetLink name="ae"/>
 <tags:dwrJavascriptLink objects="createAE,createStudy"/>
 <tags:stylesheetLink name="aeTermQuery_box" />

<style type="text/css"> 
 .selectbox
{	
	width:200px;
	behavior:expression(window.dropdown_menu_hack!=null?window.dropdown_menu_hack(this):0);
}
.divNotes,.divOtherMeddra{
	font-size:8pt;
	 border-color:#6E81A6;
	 border-style:solid;
	 border-width:1px 0px 0px 0px;
}
/* Override basic styles */
div.row div.value {
	font-weight:normal;
	white-space: normal;
	margin-left: 13em;
}

 div.row div.label { width: 12em; } 
		 
/* division where reporting period combo box is shown */
.reportingPeriodSelector{

}

</style>
 
 <script>

 	var RPCreatorClass = Class.create();
 	Object.extend(RPCreatorClass.prototype, {
 	 	/*
 	 		rpCtrl - ID of the reporting period control. The option 'Create New' will be added to this control.
 	 		rpDetailsDiv - The DIV element where the content of selected reporting period is shown.
 	 	*/
 	 	initialize : function(rpCtrl,rpDetailsDiv,rpEditCtrl,rpCtrlValue){
 	 	
 	 		this.win = null;
 	 		this.rpCtrl = $(rpCtrl);
 	 		this.rpCtrl.value = rpCtrlValue;
 	 		this.rpEditCtrl = $(rpEditCtrl);
 	 		this.rpDetailsDiv = $(rpDetailsDiv);
 	 		
			this.showOrHideEditRPCtrl(); //determine edit-button visiblility 
			
 	 	 	this.addOptionToSelectBox(this.rpCtrl, 'Create New' , '-1');//add Create New option.
 	 		Event.observe(this.rpCtrl, 'change', this.rpCtrlOnChange.bindAsEventListener(this));
 	 		Event.observe(this.rpEditCtrl, 'click', this.rpEditCtrlClick.bindAsEventListener(this));
 		},
 		displayRPPopup:function(){
 			//will show the reporting period creation popup
 	 		rpId = this.rpCtrl.value;
 	 		url = "createReportingPeriod?assignmentId=#{assignmentId}&id=#{id}&subview".interpolate({assignmentId:"${command.assignment.id}" , id:rpId});
 	 		this.win = new Window({className:"alphacube", 
 	 	 		destroyOnClose:true, 
 	 	 		title:"Reporting Period Information",  
 	 	 		width:700,  height:530, 
 				url: url, 
 				top: 30, left: 300});
 			this.win.show(true);
 		},
 		addOptionToSelectBox:function(selBox, optLabel, optValue){
 			//adds the option to specified select box.
 	 		opt = new Option(optLabel, optValue);
			selBox.options.add(opt);
 		},
 		rpCtrlOnChange : function(){
 	 		this.clearRPDetails(); //clear existing reporting period details
 	 		if(this.rpCtrl.value == -1){
 	 	 		this.displayRPPopup(); //create reporting period flow
 	 		}else if(this.rpCtrl.value){
				this.refreshRPCrlOptionsAndShowDetails(this.rpCtrl.value, false); //show the reporting period details and AEs	 	 	 		
 	 		}
 	 		
 		},
 		rpEditCtrlClick:function(){
 	 		if(this.rpCtrl.value > 0) this.displayRPPopup();
 	 	 			
 		},
 		showRPDetails:function(rpDetails){
 	 		//shows reporting period details , solicited and observed adverse events
 	 		Element.insert(this.rpDetailsDiv, rpDetails);
 	 		Effect.Appear(this.rpDetailsDiv);
 	 		this.showOrHideEditRPCtrl();
 	 		
 		},
 		clearRPDetails :function() {
 	 		//will clear the content of details section & properly unregister events
 	 		this.rpDetailsDiv.hide();
 	 		this.rpDetailsDiv.innerHTML="";
 	 		this.showOrHideEditRPCtrl();
 		},
 		showOrHideEditRPCtrl:function(){
 			//the edit reporting period button show/hide based on select box value
 	 		if(this.rpCtrl.value > 0){
 	 	 		 this.rpEditCtrl.show();
 	 	 	}else{
 	 	 	 	this.rpEditCtrl.hide();
 	 	 	}
 		},
 		refreshRPCrlOptionsAndShowDetails:function(newRPId, fetchOnlyDetails){
 	 		//will refresh the options of reporting period.
 	 		createAE.refreshReportingPeriodAndGetDetails(newRPId, fetchOnlyDetails, function(ajaxOutput){
 	 	 		this.rpCtrl.options.length = 1;
 	 	 		ajaxOutput.objectContent.each(function(rp){
 	 	 	 		 this.addOptionToSelectBox(this.rpCtrl,rp.name, rp.id);
 	 	 	 		}.bind(this));
	 	 	 	if(fetchOnlyDetails) this.clearRPDetails();
 	 	 		this.addOptionToSelectBox(this.rpCtrl,'Create New', '-1');
 	 	 		this.rpCtrl.value = newRPId;
 	 	 		this.showRPDetails(ajaxOutput.htmlContent);
 	 		}.bind(this));
 		},
 		addAdverseEvents:function(selectedTerms){
 	 		//find the terms that are not already added in the page
 			var listOfTermIDs = new Array();
 		  	$H(selectedTerms).keys().each( function(termID) {
 		  		var term = $H( selectedTerms ).get(termID);
 		  		if( !this.isTermAgainAdded(termID)){
 		  		  listOfTermIDs.push( termID );
 		        }
 		  	}.bind(this));
 		  	//get the HTML to add from server   
 		  	createAE.addObservedAE(listOfTermIDs, function(responseStr){
				$('observedBlankRow').insert({after:responseStr});
				if( $('observedEmptyRow')) $('observedEmptyRow').remove();
				this.initializeOtherMeddraAutoCompleters(listOfTermIDs);
 		  	}.bind(this));
 		},
 		isTermAgainAdded:function(termID){
 	 		//will tell wheter the term is already present
 			$$('.eachRowTermID').each(function(aTerm){
 	 			if(termID == aTerm.value()) return true;
 			});
 			return false;
 		},
 		initializeOtherMeddraAutoCompleters: function(listOfTermIDs){
 	 		listOfTermIDs.each(function(aTermId){
 	 	 		var acEls = $$('om'+aTermId);
 	 		}.bind(this));
 		},
 		deleteAdverseEvent:function(indx){
 	 		createAE.deleteAdverseEvent(indx, function(ajaxOutput){
 	 	 		$('ae-section-' + indx).remove();
 	 		}.bind(this));
 	 	
 		}
 		 		
 	});

 	
 	/*
 		Create an instance of the RPCreatorClass, by passing 'adverseEventReportingPeriod' which is the ID of Reporting Period select element.
 	*/
 	var rpCreator = null; 
 	Event.observe(window, "load", function(){
 		rpCreator = new RPCreatorClass('adverseEventReportingPeriod','detailSection','edit_button', '${command.adverseEventReportingPeriod.id}');
 		
 	});

 </script>
 
</head>
 <body>
	 <tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="section2enteraes" formName="addRoutineAeForm">
        <jsp:attribute name="instructions">
    	    <tags:instructions code="instruction_ae_enterBasics" />
        </jsp:attribute>
      	
      	<jsp:attribute name="singleFields">
      		<input type="hidden" name="_action" id="_action" value="">
			<div id="reportingPeriodSelector">      	
      				<tags:renderRow field="${fieldGroups.reportingPeriodFG.fields[0]}">
						<jsp:attribute name="value">
								<tags:renderInputs field="${fieldGroups.reportingPeriodFG.fields[0]}" />
    							<input id="edit_button" type="button" value="Edit" style="display:none;"/>
						</jsp:attribute>
					</tags:renderRow>
      		</div>
      		
      		<div id="detailSection">
				<c:if test="${not empty command.adverseEventReportingPeriod}">
					<ae:reportingPeriodAEDetails />
				</c:if>
       		</div>
       </jsp:attribute>
    </tags:tabForm>
 </body>
</html>