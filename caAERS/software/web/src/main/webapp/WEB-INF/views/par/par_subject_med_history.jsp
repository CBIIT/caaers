<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
  <head>
	<style type="text/css">
		.hidden {
			display: none;
		}
		.divOther{
			font-size:8pt;
	 		border-color:#6E81A6;
	 		border-style:solid;
	 		border-width:1px 0px 0px 0px;
		}
		 /*div.row div.label { width: 15em; } */
		 /*div.summary div.row div.label { width: 10em; } */
		 /*div.summary div.row div.value, div.summary div.row div.extra {margin-left:11em;}*/
		 /*div.row div.value, div.row div.extra { margin-left: 16em; }*/
		 .tablecontent td {
		 	border : 0px;
		 }

        div.row div.label { width: 12em; color:black; font-size: 10pt;}
        div.row div.value, div.row div.extra { margin-left: 13em; }

	</style>
 	<tags:dwrJavascriptLink objects="createAE"/>
	<script type="text/javascript">
		var mHistory = null;
 		var mHistoryClass = Class.create();
 		Object.extend(mHistoryClass.prototype, {
 	 		initialize: function(){ 
 	 		},
 	 		addDetails : function(itemType, src, val, loc, options){
 	 	 		var container = $(loc);
 	 	 		var paramHash = new Hash(); //parameters to post to server
 	 	 		paramHash.set('task', 'add');
 	 	 		paramHash.set('currentItem', itemType);
				//add extra options to the parameter list
 	 	 		if(options){
 	 	 			paramHash.set('parentIndex', options.parentIndex);
 	 	 		}
 	 	 		paramHash.set(itemType,val);
 	 	 		this.populateDeafultParameters(itemType, paramHash);
 	 	 		
 	 	 		var url = $('command').action;     //make the ajax request
                  if(url.indexOf('?') > 0){
                      url = url + "&subview";
                  } else {
                      url = url + "?subview";
                  }
				this.insertContent(container, url, paramHash, function() {}, false);
 	 		},

            removeAllAgents : function(itemType, src, val, loc, options) {
                 var container = $(loc);

                 var paramHash = new Hash(); //parameters to post to server
                    paramHash.set('task', 'remove');
                    paramHash.set('currentItem', "AllPriorTherapyAgents");

//                  alert(itemType);

                 //add extra options to the parameter list
                    if(options){
                         if(options.parentIndex >= 0) paramHash.set('parentIndex', options.parentIndex);
                    }
                    this.populateDeafultParameters(itemType, paramHash);

                    var url = $('command').action; //make the ajax request

                    var sectionHash = Form.serializeElements(this.formElementsInSection(container), true);
                    var newLoc = replaceHtml($(loc) , '');

                 this.insertContent(newLoc, url, paramHash.merge(sectionHash), function () {}.bind(this), true);

             },

 	 		removeDetails :function(itemType,index, loc, options){
 	 	 		if(index < 0) return;

				var confirmation = confirm("Do you really want to delete?");
				if(!confirmation) return; //return if not agreed.
								
				var container = $(loc);
				
				var paramHash = new Hash(); //parameters to post to server
 	 	 		paramHash.set('task', 'remove');
 	 	 		paramHash.set('currentItem', itemType);
 	 	 		paramHash.set('index', index);
 	 	 		//add extra options to the parameter list
 	 	 		if(options){
 	 	 	 		if(options.parentIndex >= 0) paramHash.set('parentIndex', options.parentIndex);
 	 	 		}
 	 	 		this.populateDeafultParameters(itemType, paramHash);
 	 	 		
 	 	 		var url = $('command').action; //make the ajax request
                if(url.indexOf('?') > 0) {
                  url = url + "&subview";
                }else {
                  url = url + "?subview";
                }
 	 	 		var sectionHash = Form.serializeElements(this.formElementsInSection(container), true);
 	 	 		$(loc).innerHTML = '';
				this.insertContent(container, url, paramHash.merge(sectionHash), function(){}, true);
				
 	 	 		
 	 		},
 	 		populateDeafultParameters : function(itemType, paramHash){
 				//will populate the default parameters, to support ajax communication
 				var page = ${tab.number};
 				var target = '_target' + ${tab.number}; 
 				paramHash.set('_page', page);
 				paramHash.set(target, page);
 				paramHash.set('_asynchronous', true);
 				paramHash.set('decorator', 'nullDecorator');
 			},

             insertContent : function(aContainer, url, params, onCompleteCallBack, replaceAllContent) {
                  new Ajax.Request(url, {
                         parameters : params.toQueryString(),
                         onSuccess: function(transport) {
                             if (replaceAllContent) {
                                 $(aContainer).update(transport.responseText);
                             }
                             else {
                                 Element.insert(aContainer, {'top' : transport.responseText});
                             }
                         }
                  });
              },
             
 			formElementsInSection : function(aContainer){
 	 			return aContainer.select('input', 'select', 'textarea');	
 			}
 		});

 		Event.observe(window, "load",setupPage);

        function addMetastaticDisease() {
            mHistory.addDetails('metastaticDiseaseSite', null, null, 'anchorMetastaticDiseases');
        }

        function addPreexistingCondition() {
            mHistory.addDetails('preExistingCondition', null, null, 'anchorPreExistingCondition');
        }

        function addConMeds() {
            mHistory.addDetails('concomitantMedication', null, null, 'anchorConcomitantMedication');
        }

        function addPriorTherapy() {
            mHistory.addDetails('priorTherapy', null, null, 'anchorPriorTherapy');
        }

        function setupPage(){
			mHistory = new mHistoryClass();//create a new mHistory object
		 	Event.observe('command', 'submit', function(e){

				/* Below is a very ugly tweak did for IE7 if priorTherapyAgents[i]-input='begin', the value of priorTherapyAgents[i], is assumed by spring as its value 
				  But only happens in IE7
				  */
				// AE.resetAutocompleter('metastaticDiseaseSite');
				var i = 0;
				for(i = 0; i < 15; i++){
					var el = 	$('priorTherapyAgents[' + i + ']');
					if(el) el.value = '';
					if(el) $('priorTherapyAgents[' + i + ']-input').value = '';
				}
			});
		}
		function fireAction(type, index, loc, options, id){
			mHistory.removeDetails(type, index, loc, options);
		}
		
		        function showShowAllTable(el, baseName) {

            var parameterMap = getParameterMap('command');
            if (baseName.search("metastaticDiseaseSite")>=0  || baseName == 'codedPrimaryDiseaseSite') {
                createAE.buildAnatomicSiteTable(el, parameterMap, baseName, function(table) {
                    $('showAllDropDownContent').innerHTML = table;
//                    $('showAllDropDown').style.position = 'absolute';
                    try {
                        var _top = Position.cumulativeOffset($(el))[1];
                        var _left = Position.cumulativeOffset($(el))[0];

                        $('showAllDropDown').style.top = (_top-190) + "px";
                        $('showAllDropDown').style.left = (_left - 120) + "px";
                    } catch (e) {
//                        alert('2');
                    }
                    $('showAllDropDown').show();
                });
            }
        }
        
        function fillDiseaseSiteAutoCompletor(val,baseName, text){
            if (baseName == 'codedPrimaryDiseaseSite') {
                baseName = 'assignment.diseaseHistory.codedPrimaryDiseaseSite'
            }

            if (baseName.indexOf('priorTherapyAgents') >= 0 || baseName.search("metastaticDiseaseSite")>=0) {
                baseName = baseName.replace(/DOT/g, ".") ;
                baseName = baseName.replace(/OPEN/g, "[") ;
                baseName = baseName.replace(/CLOSE/g, "]") ;
            }

            $(baseName).value = val;
		    $(baseName+ "-input").value = text;
		    $(baseName+ "-input").removeClassName('pending-search');
		   hideShowAllTable();
	   }

	   function fillChemoAgentAutoCompletor(val, baseName, text){
	   		if (baseName.indexOf('priorTherapyAgents') >= 0 || baseName.search("metastaticDiseaseSite") >= 0) {
                   baseName = baseName.replace(/DOT/g, ".") ;
                   baseName = baseName.replace(/OPEN/g, "[") ;
                   baseName = baseName.replace(/CLOSE/g, "]") ;
            }
	   		
	        $(baseName).value = val;
		    $(baseName+ "-input").value = text;
		    $(baseName+ "-input").removeClassName('pending-search');
		   hideShowAllTable();
		   
	   }
        
        function hideShowAllTable(){
		  $('showAllDropDown').hide();
	   }

        function setTitleMDS(_index) {
            var titleID = $('titleOf_assignment.diseaseHistory.metastaticDiseaseSites[' + _index + ']');
            var name = $("assignment.diseaseHistory.metastaticDiseaseSites[" + _index + "].codedSite-input");
            var value = name.value;
            if ($("assignment.diseaseHistory.metastaticDiseaseSites[" + _index + "].codedSite").value == 110) {
                $('assignment.diseaseHistory.metastaticDiseaseSites[' + _index + '].other').show();
                value += " - " + $("assignment.diseaseHistory.metastaticDiseaseSites[" + _index + "].otherSite").value + "";
                $('showALL' + _index).hide();
            } else {
                $('assignment.diseaseHistory.metastaticDiseaseSites[' + _index + '].other').hide();
                $('showALL' + _index).show();
            }
            $(titleID).innerHTML = value;
        }

	</script>
  </head>
  <body>

  <%--(${empties})--%>
      <div class="summary">
      <div class="row">
          <div class="label">Subject</div>
          <div class="value">${ command.assignment.studySubjectIdentifier }</div>
	  </div>
	  <div class="row">
          <div class="label">Study</div>
          <div class="value">${command.study.shortTitle}</div>
      </div>
	  </div>
  <p><tags:instructions code="instruction_subject_enter.medhist.top"/></p>
   <form:form id="command">
   	<input type="hidden" name="CSRF_TOKEN" value="${CSRF_TOKEN }"/>
   <div id="showAllDropDown" style="position: absolute; display: none; left: 300px; width:300px; z-index:99; top:0px;">
   <table width="100%" class="eXtremeTable" frame="border" border-color="blue" bgcolor="white">
     <tbody>
       <tr class="titleRow">
         <td align="left" class="title">Select :</td>
         <td align="right"><a href="javascript:hideShowAllTable()"><img src="<c:url value="/images/rule/window-close.gif" />" id="close-image"/></a></td>
       </tr>
       <tr>
         <td colspan="2"><div id="showAllDropDownContent"/></td>
       </tr>
     </tbody>
   </table>
 </div>

   <tags:hasErrorsMessage path="*" />
   <tags:jsErrorsMessage/>

    <tags:tabFields tab="${tab}" />

	<chrome:box id="assignment.general" title="General" collapsable="true">
	   <p><tags:instructions code="instruction_subject_enter.medhist.gen"/></p>
		<%--<tags:hasErrorsMessage path="assignment.baselinePerformance" />--%>
		<a name="anchorGeneral"></a>
		<div id="anchorGeneral">
			<ui:row path="assignment.baselinePerformance">
				<jsp:attribute name="label">
					<ui:label text="Baseline performance" path="assignment.baselinePerformance" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:select options="${baselinePerformanceOptions}" path="assignment.baselinePerformance" />
				</jsp:attribute>
			</ui:row>
		</div>
	</chrome:box>

   <c:if test="${empty command.assignment.studySite.study.studyPurpose or command.assignment.studySite.study.studyPurpose eq 'Treatment'}">
	<chrome:box id="assignment.diseaseHistory" title="Disease Information" collapsable="true">
		
     <p><tags:instructions code="instruction_subject_enter.medhist.disease"/></p>
		<%--<tags:hasErrorsMessage path="assignment.diseaseHistory.*" />--%>
		<div id="anchorDiseaseInfo">

			<ui:row path="assignment.diseaseHistory.abstractStudyDisease">
				<jsp:attribute name="label">
					<ui:label path="assignment.diseaseHistory.abstractStudyDisease" text="Disease name" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:select options="${studyDiseasesOptions}" path="assignment.diseaseHistory.abstractStudyDisease">
						<jsp:attribute name="embededJS">
							<%--
							  Note :- If disease is Disease Name is  'Solid tumor, NOS' or 'Hematopoietic malignancy, NOS', other disease should be provided
							--%>
							$('assignment.diseaseHistory.abstractStudyDisease').observe('change', function(evt){
								var el = $(evt.element());
								var optionText = el.options[el.selectedIndex].text;
								if(optionText == 'Solid tumor, NOS' || optionText == 'Hematopoietic malignancy, NOS'){
									AE.slideAndShow("assignment.diseaseHistory.otherPrimaryDisease-row")
								}else{
									$('assignment.diseaseHistory.otherPrimaryDisease').value = ''
       		 	 					AE.slideAndHide("assignment.diseaseHistory.otherPrimaryDisease-row")
								}
							});
						</jsp:attribute>
					</ui:select>
				</jsp:attribute>
			</ui:row>

			<ui:row path="assignment.diseaseHistory.otherPrimaryDisease" style="display:none;">
				<jsp:attribute name="label">
					<ui:label path="assignment.diseaseHistory.otherPrimaryDisease" text="Other (disease)" required="true" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:text path="assignment.diseaseHistory.otherPrimaryDisease"  />
				</jsp:attribute>
			</ui:row>
<%--problem begins here--%>
			<ui:row path="assignment.diseaseHistory.codedPrimaryDiseaseSite">
				<jsp:attribute name="label">
					<ui:label path="assignment.diseaseHistory.codedPrimaryDiseaseSite" text="Primary site of disease" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:autocompleter path="assignment.diseaseHistory.codedPrimaryDiseaseSite"
					  initialDisplayValue="${empty command.assignment.diseaseHistory.codedPrimaryDiseaseSite ? 'Begin typing here' : command.assignment.diseaseHistory.codedPrimaryDiseaseSite.name}" enableClearButton="true">
						<jsp:attribute name="populatorJS">
							function(autocompleter, text) {
                				createAE.matchAnatomicSite(text, function(values) {
                    				autocompleter.setChoices(values)
                				})
            				}
						</jsp:attribute>
						<jsp:attribute name="selectorJS">
							function(obj){
								return obj.name;
							}
						</jsp:attribute>
						<jsp:attribute name="optionsJS">
							{
								afterUpdateElement: function(inputElement, selectedElement, selectedChoice) {
									//show the otherPrimaryDiseaseSite box below, using javascript
									$('assignment.diseaseHistory.codedPrimaryDiseaseSite').value = selectedChoice.id
									if(selectedChoice.id == '110'){
										AE.slideAndShow("assignment.diseaseHistory.otherPrimaryDiseaseSite-row");
									}else{
										$("assignment.diseaseHistory.otherPrimaryDiseaseSite").value=""
        								AE.slideAndHide("assignment.diseaseHistory.otherPrimaryDiseaseSite-row")
									}
								}
							}
						</jsp:attribute>
					</ui:autocompleter>
					<%-- The line below used to be an <a> tag but it was causing some bizzare issue in IE causing part of the chrome box to not render.
							I (David) have changed it to a span as a workaround. I tried making it a button, but that caused another bizzare issue in IE.
							In the future if you want to make this a button AND the 'Show All' in the box below this one a button also, you will need to resolve that IE issue--hovering on this button will cause the below button to dissappear.
							Good luck.  --%>
                    <span style="cursor:pointer; color:blue; text-decoration:underline;" onclick="javascript:showShowAllTable('_c1', 'codedPrimaryDiseaseSite')" id="_c1">Show All</span>
                </jsp:attribute>
			</ui:row>
<%--problem ends here--%>
			<ui:row path="assignment.diseaseHistory.otherPrimaryDiseaseSite" style="display:none;">
				<jsp:attribute name="label">
					<ui:label path="assignment.diseaseHistory.otherPrimaryDiseaseSite" text="Other (site of primary disease)" required="true" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:text path="assignment.diseaseHistory.otherPrimaryDiseaseSite" />
				</jsp:attribute>
			</ui:row>

			<ui:row path="assignment.diseaseHistory.diagnosisDate">
				<jsp:attribute name="label">
					<ui:label path="assignment.diseaseHistory.diagnosisDate" text="Date of initial diagnosis" />
				</jsp:attribute>
				<jsp:attribute name="value">
					<ui:splitDate path="assignment.diseaseHistory.diagnosisDate" />
				</jsp:attribute>
			</ui:row>
		</div>
	</chrome:box>
   </c:if>
   <%-- BOX --%>

    <chrome:box id="assignment.diseaseHistory.metastaticDiseaseSites" title="Metastatic Disease Site" collapsable="true">
            <p><tags:instructions code="instruction_subject_enter.medhist.meta"/></p>
            <%--<tags:hasErrorsMessage path="assignment.diseaseHistory.metastaticDiseaseSites.*" />--%>
            <%--<p><tags:instructions code="instruction_ae_patientdetails_metadiseasesite"/></p>--%>

            <div style="padding-left:20px;">
            <div>
                <tags:button cssClass="foo" id="metastatic-diseases-btn" color="blue" value="Add" icon="Add" type="button" onclick="addMetastaticDisease();" size="small"/>
                <%--<tags:indicator id="metastatic-diseases-btn-indicator" />--%>
                <%--<tags:indicator id="metastaticDiseaseSite-indicator"/>--%>
                <div id="anchorMetastaticDiseases">
                    <c:set var="size" value="${fn:length(command.assignment.diseaseHistory.metastaticDiseaseSites)}" />
                    <c:forEach items="${command.assignment.diseaseHistory.metastaticDiseaseSites}" var="mds" varStatus="status">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <c:set var="mSite" value="${command.assignment.diseaseHistory.metastaticDiseaseSites[newIndex]}" />
                        <par:oneMetastaticDiseaseSite index="${newIndex}" anatomicSite="${mSite.codedSite}" metastaticSite="${mSite}"/>
                    </c:forEach>
                </div>
            </div>
            </div>
	</chrome:box>

   <%-- BOX --%>

    <chrome:box id="assignment.preExistingConditions" title="Pre-Existing Conditions" collapsable="true">
            <p><tags:instructions code="instruction_subject_enter.medhist.pre"/></p>
            <%--<tags:hasErrorsMessage path="assignment.preExistingConditions.*" />--%>

            <div style="padding-left:20px;">
            <div>
                <tags:button cssClass="foo" id="pre-cond-btn" color="blue" value="Add" icon="Add" type="button" onclick="addPreexistingCondition();" size="small"/>
                <div id="anchorPreExistingCondition">
                    <c:set var="size" value="${fn:length(command.assignment.preExistingConditions)}" />
                    <c:forEach items="${command.assignment.preExistingConditions}" varStatus="status">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <c:set var="pCond" value="${command.assignment.preExistingConditions[newIndex]}" />
                        <c:if test="${!pCond.preExistingCondition.retiredIndicator}">
                            <par:onePreExistingCondition index="${newIndex}" preExistingCondition="${pCond.preExistingCondition}" otherValue="${pCond.other}"/>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
            </div>
	</chrome:box>

   <%-- BOX --%>
   
    <chrome:box id="assignment.concomitantMedications" title="Concomitant Medications" collapsable="true">
                <p><tags:instructions code="instruction_subject_enter.medhist.conmeds"/></p>
<%--
                <tags:hasErrorsMessage path="assignment.concomitantMedications.*" />
                <tags:hasErrorsMessage path="concomitantMedication" />
--%>

                <div style="padding-left:20px;">
                <div>
                  <tags:button cssClass="foo" id="concomitantMedication-btn" color="blue" value="Add" icon="Add" type="button" onclick="addConMeds();" size="small"/>
                            <div id="anchorConcomitantMedication">
                                <c:set var="size" value="${fn:length(command.assignment.concomitantMedications)}" />
                                <c:forEach items="${command.assignment.concomitantMedications}" varStatus="status">
                                    <c:set var="newIndex" value="${size - (status.index + 1)}" />
                                    <c:set var="conMed" value="${command.assignment.concomitantMedications[newIndex]}" />
                                    <par:oneConcomitantMedication index="${newIndex}" concomitantMedication="${conMed}" collapsed="true" />
                                </c:forEach>
                            </div>
                </div>
                </div>
	</chrome:box>

   <%-- BOX --%>
   
    <chrome:box id="assignment.priorTherapies" title="Prior Therapies" collapsable="true">
            <p><tags:instructions code="instruction_subject_enter.medhist.pt"/></p>
            <tags:hasErrorsMessage path="assignment.priorTherapies.*" />

            <div style="padding-left:20px;">
            <div>
             <c:set var="size" value="${fn:length(command.assignment.priorTherapies)}" />
                <tags:button id="priortherapy-btn" 
                       	color="blue" 
                       	value="Add" 
                       	icon="Add" 
                       	type="button" 
                       	onclick="addPriorTherapy();" 
                       	size="small"
                       	disabled="${(size gt 0 and command.assignment.priorTherapies[0].priorTherapy.id eq _priorTherapy_nopriortherapy_id) ? 'true' : ''}" />
                        <div id="anchorPriorTherapy">
                           
                            <c:forEach items="${command.assignment.priorTherapies}" varStatus="status">
                                <c:set var="newIndex" value="${size - (status.index + 1)}" />
                                <c:set var="ptherapy" value="${command.assignment.priorTherapies[newIndex]}" />
                                <c:if test="${!ptherapy.priorTherapy.retiredIndicator}">
                                    <par:onePriorTherapy index="${newIndex}"
                                        priorTherapy="${ptherapy}"
                                        collapsed="true"
                                        showNoPriorTherapy="${(size eq 1 and newIndex eq 0) and (empty ptherapy.priorTherapy or empty ptherapy.priorTherapy.text or ptherapy.priorTherapy.id eq _priorTherapy_nopriortherapy_id)}"/>
                                </c:if>
                            </c:forEach>
                        </div>
            </div>
            </div>
	</chrome:box>

    <tags:tabControls flow="${flow}" tab="${tab}" />
    <c:if test="${_finish}"><input type="hidden" name="_finish"/></c:if>
   </form:form>
  </body>
</html>