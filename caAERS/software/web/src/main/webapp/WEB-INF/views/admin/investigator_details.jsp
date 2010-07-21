<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<jsp:useBean id="today" class="java.util.Date" scope="request" />

<html>
<head>
<title><caaers:message code="investigator.details.pageTitle"/></title>


<tags:dwrJavascriptLink objects="createInvestigator,createIND" />
<script language="JavaScript" type="text/JavaScript">
AE.PAGE_HELP_LINK = 'investigatorDetails';
var today = '<tags:formatDate value="${today}"/>'

var associatedSite = null;
var associatedSiteClass = Class.create();
Object.extend(associatedSiteClass.prototype, {
    
		initialize: function(){
		},

        addDetails : function(methodName,index,loc, options) {
			this.index=index;
	 		var container = $(loc);
	 		var paramHash = new Hash(); //parameters to post to server
			//add extra options to the parameter list
	 		if(options){
	 			paramHash.set('parentIndex', options.parentIndex);
	 		}
	 		this.populateDeafultParameters(methodName, paramHash);
	 		
	 		var url = $('command').action + "?subview"; //make the ajax request
	 		$('ajax_wait').removeClassName('indicator');
			this.insertContent(container, url, paramHash, function(methodName) { new jsInvestigator(this.index); $('ajax_wait').addClassName('indicator'); }.bind(this));
		},
    
		removeDetails :function(methodName,index, loc, options) {
	 		if(index < 0) return;

			var confirmation = confirm("Do you really want to delete?");
			if(!confirmation) return; //return if not agreed.
					
			var container = $(loc);
	
			var paramHash = new Hash(); //parameters to post to server
	 		paramHash.set('index', index);
	 		
	 		this.populateDeafultParameters(methodName, paramHash);
	 		
	 		var url = $('command').action + "?subview"; //make the ajax request
	 		var sectionHash = Form.serializeElements(this.formElementsInSection(container), true);
	 		$(loc).innerHTML = '';
	 		$('ajax_wait').removeClassName('indicator');
			this.insertContent(container, url, paramHash.merge(sectionHash), function(){$('ajax_wait').addClassName('indicator');} );				
		},

		populateDeafultParameters : function(methodName, paramHash) {
            //will populate the default parameters, to support ajax communication
            var page = ${tab.number};
            var target = '_target' + ${tab.number};
            paramHash.set('_page', page);
            paramHash.set(target, page);
            paramHash.set('_asynchronous', true);
            paramHash.set('_asyncMethodName' , methodName);
            paramHash.set('decorator', 'nullDecorator');
		},

		insertContent : function(aContainer, url, params, onCompleteCallBack) {
			//helper method to insert content in a DIV
			new Ajax.Updater(aContainer, url, {
				parameters: params.toQueryString() , onComplete: onCompleteCallBack ,insertion: Insertion.Bottom, evalScripts : true
			});
		},

		formElementsInSection : function(aContainer) {
			return aContainer.select('input', 'select', 'textarea');	
		}
});

function fireAction(index){
	associatedSite.removeDetails("removeSiteInvestigator", index, 'anchorSiteInvestigators', {});
}

function addSiteInvestigator(){
	associatedSite.addDetails('addSiteInvestigator',$$(".site-investigator-row").length ,'siteInvestigatorTable-body', {});
}

var jsInvestigator = Class.create();
Object.extend(jsInvestigator.prototype, {
		
	initialize: function(index, orgName) {
	 this.index = index;
	 this.orgField = 'siteInvestigators['+ index + '].organization';
	 this.orgInputField = this.orgField + '-input';
		
	 //initialze the date fields.	
	 AE.registerCalendarPopups();
	 //initialze the auto completer field.
	 if($(this.orgInputField)){
		 AE.createStandardAutocompleter(this.orgField, this.sitePopulator.bind(this),this.siteSelector.bind(this));
		 if(orgName) $(this.orgInputField).value = orgName;
	 }
	},
	sitePopulator: function(autocompleter, text) {
		createInvestigator.restrictOrganization(text, function(values) {
      	 autocompleter.setChoices(values)
      })
    },
    siteSelector: function(organization) { 
        var image;            	
    	if(organization.externalId != null){
                  image = '&nbsp;<img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/>&nbsp;';
        } else {
                  image = '';
        }
    	return image + organization.name + " (" + organization.nciInstituteCode + ")";
    }
	
   });

	function clearField(field){
		field.value="";
	};
	  
Event.observe(window, "load", function() {
		
	  	<c:forEach varStatus="status" items="${command.siteInvestigators}" var="si">
	  		new jsInvestigator(${status.index}, "${si.organization.fullName}");
	  	</c:forEach>

	  	associatedSite = new associatedSiteClass();
		
		if(${fn:length(command.externalInvestigators) gt 0}){
				 displayRemoteInvestigator();
		}


		toggelUserName(${command.allowedToLogin});

		$$('input#allowedToLogin').each(function(allowedLoginChkBox) {
			allowedLoginChkBox.observe('click',function(evt){
				var element = Event.element(evt);
				toggelUserName(element.checked);
			});			
		});

		/*
		var allowedLoginChkBox = $('allowedToLogin');
		if(allowedLoginChkBox){
			allowedLoginChkBox.observe('click',function(evt){
				var element = Event.element(evt);
				toggelUserName(element.checked);
			});
		}
		*/
	
		
	});

	function displayRemoteInvestigator(){
		var contentWin = new Window({className:"alphacube", destroyOnClose:true, id:"remoteInv-popup-id", width:550,  height:200, top: 30, left: 300});
		contentWin.setContent( 'display_remote_inv' );
	    contentWin.showCenter(true);
	   popupObserver = {
	  			onDestroy: function(eventName, win) {
	  				if (win == contentWin) {
	  					$('display_remote_inv').style.display='none';
	  					
	  					contentWin = null;
	  					Windows.removeObserver(this);
	  				}
	  			}
	  		}
	    Windows.addObserver(popupObserver);
	}

	function submitRemoteInvForSave(){
		var form = document.getElementById('command');
		form._action.value="saveRemoteInv";
		form.submit();
	}

	function selectInvestigator(selectedIndex){
		var form = document.getElementById('command')
		form._selected.value=selectedIndex;
		document.getElementById('save-yes').disabled = false;
	}

	function syncInvestigator(){
		var form = document.getElementById('command');
		form._action.value="syncInvestigator";
		form.submit();
	}

	function toggleDate(action, selected){

		if(action == 'Deactivate'){
			var dConfirmation = confirm("Do you really want to Deactivate?");
			if(!dConfirmation) return; //return if not agreed.
			$('siteInvestigators['+ selected + '].endDate').value=today;
		}
		if(action == 'Activate'){
			var aConfirmation = confirm("Do you really want to Activate?");
			if(!aConfirmation) return; //return if not agreed.
			$('siteInvestigators['+ selected + '].startDate').value=today;
			$('siteInvestigators['+ selected + '].endDate').value="";
		}
	}

//----------------------------------------------------------------------------------------------------------------------

function toggelUserName(checkBoxChecked) {
    if (checkBoxChecked) {
        $('loginId-row').show();
        $('loginId').addClassName("required");
        $('loginId').addClassName("validate-NOTEMPTY&&MAXLENGTH2000");
    } else {    	
        $('loginId-row').hide();
        if ($('loginId')!=null) {
        	$('loginId').value="";
        	$('loginId').removeClassName("required");
        	$('loginId').removeClassName("validate-NOTEMPTY&&MAXLENGTH2000");
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------

</script>

</head>
<body>

<csmauthz:accesscontrol var="siteInvestigatorUpdate" objectPrivilege="gov.nih.nci.cabig.caaers.domain.SiteInvestigatorRole:UPDATE"/>
<csmauthz:accesscontrol var="investigatorCreate" objectPrivilege="gov.nih.nci.cabig.caaers.domain.Investigator:CREATE"/>
<csmauthz:accesscontrol var="investigatorUpdate" objectPrivilege="gov.nih.nci.cabig.caaers.domain.Investigator:UPDATE"/>
<c:set var="createUpdateAllowed" value="${investigatorCreate || investigatorUpdate}"></c:set>

<div id="display_remote_inv" style="display:none;text-align:left" >
	<chrome:box title="Please select a Investigator to be saved in caAERS" id="popupId">
		<div class="eXtremeTable">
          <table width="100%" border="0" cellspacing="0"  class="tableRegion">
            <thead>
              <tr align="center" class="label">
              	<td/>
                <td class="tableHeader">First Name</td>
                <td class="tableHeader">Last Name</td>
                <td class="tableHeader">Email Address</td>
              </tr>
            </thead>
            <c:forEach items="${command.externalInvestigators}"  var="remInv" varStatus="rdStatus">
              <tr>
              	<td><input type="radio" name="remotersradio" value=${rdStatus.index} id="remoters-radio" onClick="javascript:selectInvestigator('${rdStatus.index}');"/></td>
                <td align="left">${remInv.firstName}</td>
                <td align="left">${remInv.lastName}</td>
                <td align="left">${remInv.emailAddress}</td>
              </tr>
            </c:forEach>
          </table>
		</div>
		<br><br>
   		<table width="100%">	
   			<tr>
   				<td align="left">
   					<input type="submit" value="Cancel" id="save-no" onClick="javascript:window.parent.Windows.close('remoteInv-popup-id');"/>
   				</td>
   				<td align="right">
    				<input type="submit" disabled value="Ok" id="save-yes" onClick="javascript:window.parent.submitRemoteInvForSave();"/>
   				</td>
   			<tr>	
   		</table>
	</chrome:box>
</div>



<div class="tabpane">
    <div class="workflow-tabs2">
        <ul id="" class="tabs autoclear">
            <li id="thirdlevelnav" class="tab0 selected">
            	<c:choose>
            		<c:when test="${createUpdateAllowed}">
                		<div>
	                  	  	<a href="createInvestigator"><caaers:message code="investigator.menu.createEditInvestigator"/></a>
                		</div>            		
            		</c:when>
            		<c:otherwise>
                		<div>
	                    	<a href="#"><caaers:message code="investigator.menu.viewInvestigator"/></a>
                		</div>             		
            		</c:otherwise>
            	</c:choose>
            </li>            
            <li id="thirdlevelnav" class="tab1">
            	<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.Investigator:READ">
                <div>
                    <a href="searchInvestigator"><caaers:message code="investigator.menu.searchInvestigator"/></a>
                </div>
                </csmauthz:accesscontrol>
            </li>            
        </ul>
    </div>



<tags:tabForm tab="${tab}" flow="${flow}" formName="createInvestigatorForm"
	 willSave="false" hideErrorDetails="false">

	<jsp:attribute name="singleFields">
	<div>
	<c:if test="${(command.externalId != null)}"> <img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/> </c:if>
	
	<input type="hidden" name="_action" value="">
    <input type="hidden" name="_selected" value="">
	<input type="hidden" name="_finish" value="true"/>
	</div>

    </jsp:attribute>
	<jsp:attribute name="repeatingFields">

        <tags:instructions code="investigatordetails" />

		<caaers:message code="investigator.details.detailsSection" var="detailsSectionTitle"/>
    	<chrome:division title="${detailsSectionTitle}" id="investigator">
    	
		<div class="leftpanel">

			<c:forEach begin="0" end="3" items="${fieldGroups.investigator.fields}" var="field">
               <tags:renderRow readOnly="${!investigatorCreate}" field="${field}"/>
            </c:forEach>
            
            <c:if test="${command.wasLoginDisallowed}">
            	<tags:renderRow readOnly="${!siteInvestigatorUpdate}" field="${fieldGroups.investigator.fields[4]}"/>
            </c:if>
            <c:if test="${!command.wasLoginDisallowed}">
           	 <tags:renderRow field="${fieldGroups.investigator.fields[4]}">
           	 	<jsp:attribute name="value">Yes</jsp:attribute>
           	 </tags:renderRow>
            </c:if>
			
		</div>
		<div class="rightpanel">
		    <c:forEach begin="5" end="7" items="${fieldGroups.investigator.fields}" var="field">
              <tags:renderRow readOnly="${!investigatorCreate}" field="${field}" />
            </c:forEach>
            <tags:renderRow readOnly="${!siteInvestigatorUpdate}" field="${fieldGroups.investigator.fields[8]}" style="display:none;"/>
		</div>
		<br>
		<br>
	</chrome:division>
	
	<caaers:message code="investigator.details.associateSitesSection" var="associateSitesSectionTitle"/>
	<chrome:division title="${associateSitesSectionTitle}">
	  <br>
	  <div>
			<tags:indicator id="ajax_wait"/>
	 </div>
	  <div id="_anchorSiteInvestigators">
	  <div id="anchorSiteInvestigators">
	  <table id="siteInvestigatorTable" class="tablecontent" width="100%">
	  	<tbody id="siteInvestigatorTable-body">
  			<tr id="site-investigator">
  				<th class="tableHeader" ><tags:requiredIndicator />Organization</th>
  				<th class="tableHeader" ><tags:requiredIndicator />Start date</th>
  				<th class="tableHeader" >End date</th>
  				<th class="tableHeader" >Status</th>
  				<th class="tableHeader" >Action</th>
  			</tr>
    				
		            	<c:forEach var="siteInvestigator" varStatus="status" items="${command.siteInvestigators}">
							<investigator:siteInvestigator 	
								title="Associated Sites ${status.index + 1}" 
								enableDelete="${empty siteInvestigator.id}"
								enableActivateDeactivate="${siteInvestigatorUpdate}"
								enableAutocompleter="${investigatorCreate==true}"
								readOnly="${!siteInvestigatorUpdate}"
								sectionClass="site-investigator-row" 
								index="${status.index}" 
								active="${siteInvestigator.active}"
								orgName="${siteInvestigator.organization.name}"/>
						</c:forEach>
				
            	<c:if test="${empty command.siteInvestigators}">
            		<tr id="empty-inv-row">
            			<td colspan="2" align="center">The investigator is not assigned to any organization</td>
            		</tr>
            	</c:if>
           </tbody>
       </table>
		</div>
	</div>
	</chrome:division>
        
    <br>
    <c:if test="${investigatorCreate}">
    	<tags:button cssClass="foo" id="addSiteInvestigator_btn" color="blue" value="Add Organization" icon="Add" type="button" onclick="addSiteInvestigator();" size="small"/>
	</c:if>
     </jsp:attribute>
     
	<jsp:attribute name="tabControls">
		<c:if test="${investigatorUpdate}">
	 		<tags:tabControls tab="${tab}" flow="${flow}" willSave="false" saveButtonLabel="Save">
	 		</tags:tabControls>
	 	</c:if>
	 </jsp:attribute>	
</tags:tabForm>
</div>
</body>
</html>
