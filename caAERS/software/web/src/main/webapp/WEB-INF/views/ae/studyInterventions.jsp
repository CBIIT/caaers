<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:dwrJavascriptLink objects="createAE"/>

<tags:slider renderComments="${command.associatedToWorkflow }" renderAlerts="${command.associatedToLabAlerts}" reports="${command.selectedReportsAssociatedToWorkflow}" 
		display="${(command.associatedToWorkflow or command.associatedToLabAlerts) ? '' : 'none'}" workflowType="report">
    	<jsp:attribute name="labs">
    		<div id="labs-id" style="display:none;">
    			<tags:labs labs="${command.assignment.labLoads}"/>
    		</div>
    	</jsp:attribute>
    </tags:slider>

    <style type="text/css">
        div.row div.label { width: 16em; }
        div.row div.value { margin-left: 17em;}
        textarea { width: 20em; height: 5em; }
        img._boxImage_ { border : 1px blue dotted;}
    </style>
</head>
<body>

<script language="JavaScript">
var divisions = new Hash();
var routingHelper = new RoutingAndReviewHelper(createAE, 'aeReport');

function refreshBoxes() {
    registerAll();
    closeAll();
}

function registerAll() {
    var list = $$('div.division');
    divisions = new Hash();
    for (i=0; i<list.length; i++) {
        divisions.set(list[i].id, true);
    }
}

function closeAll() {
    divisions.each(function(pair) {

        var _id = pair.key;
        panelDiv = $("contentOf-" + _id);
        imageId= 'image-' + _id;
        imageSource = $(imageId).src;
        
        CloseDown(panelDiv, arguments[1] || {});
        document.getElementById(imageId).src = imageSource.replace('down','right');

//        alert(pair.key + ' = "' + pair.value + '"');
    });
}

//==================== Will get the description of other intervention ================
function updateOtherInterventionDescription(selbox, dSpanId){
    var v = selbox.getValue();
    if(v){
        createAE.retrieveOtherInterventionDescription(v, function(ajaxOutput){
            $(dSpanId).innerHTML = ajaxOutput.htmlContent;
        });
    }
}
//====================================================================================

//==================== Will get the study device ================
function updateMedicalDevice(i, studyDeviceId){
   var baseName = 'aeReport.medicalDevices[' + i + '].';
   createAE.retrieveStudyDevice(studyDeviceId, function(ajaxOutput){
      var d = ajaxOutput.objectContent;

      ["brandName",
       "commonName",
       "deviceType",
       "manufacturerName" ,
       "manufacturerCity",
       "manufacturerState",
       "catalogNumber",
       "modelNumber"
      ].each(function(n){
         var e = $(baseName + n);
         if(e)e.value = d[n];
      });
      
   });
}
//====================================================================================

    Event.observe(window, "load", setupPage);
    divisions = new Hash(); 

    function setupPage(){
         //only show the workflow tab, if it is associated to workflow
            var associatedToWorkflow = ${command.associatedToWorkflow};
            if(associatedToWorkflow){
            	<c:forEach items="${command.selectedReportsAssociatedToWorkflow}" var="report" varStatus="status">
	 	          	routingHelper.retrieveReviewCommentsAndActions('${report.id}');
 	          	</c:forEach>
            }

        interventionInstance = new InterventionClass();
        <c:if test="${command.investigationalAgentAdministeredForPreviousReports}">
            if($('aeReport.treatmentInformation.investigationalAgentAdministered').value == '') {
                $('aeReport.treatmentInformation.investigationalAgentAdministered').value = 'true';
            }
         </c:if>
    }

    function addAgent() {
        interventionInstance._addItem('agent', null, null, '_agents');
    }
    function addRadiation() {
        interventionInstance._addItem('radiation', null, null, '_radiations');
    }
    function addDevice() {
        interventionInstance._addItem('device', null, null, '_devices');
    }
    function addSurgery() {
        interventionInstance._addItem('surgery', null, null, '_surgeries');
    }
    function addBehavioral() {
        interventionInstance._addItem('behavioral', null, null, '_behaviorals');
    }

    function fireAction(itemType, index, location, elementId, css) {
        interventionInstance._deleteItem(itemType, index, location);
    }

    var interventionInstance = null;
 	var InterventionClass = Class.create();
    Object.extend(InterventionClass.prototype, {
        initialize: function() {
        },

        _populateDeafultParameters : function(itemType, paramHash) {
            var page = ${tab.number};
            var target = '_target' + ${tab.number};
            paramHash.set('_page', page);
            paramHash.set(target, page);
            paramHash.set('_asynchronous', true);
            paramHash.set('decorator', 'nullDecorator');
        },

        _addItem: function(itemType, src, val, location, options) {
            refreshBoxes();
            var container = $(location);
            var paramHash = new Hash(); 
            paramHash.set('task', 'add');
            paramHash.set('currentItem', itemType);
            paramHash.set(itemType, val);

            this._populateDeafultParameters(itemType, paramHash);

            var url = $('command').action + "?subview"; 
            this._insertContent(container, url, paramHash, function() {}.bind(this));
        },

        formElementsInSection : function(container) {
            return container.select('input', 'select', 'textarea');
        },

        _deleteItem: function(itemType, index, location) {
            if (index < 0) return;
            var confirmation = confirm("Do you really want to delete?");
            if (!confirmation) return;

            // this.showIndicator(itemType+"-indicator");
            var container = $(location);

            var paramHash = new Hash();
            paramHash.set('task', 'remove');
            paramHash.set('currentItem', itemType);
            paramHash.set('index', index);
            this._populateDeafultParameters(itemType, paramHash);
            var url = $('command').action + "?subview";
            var sectionHash = Form.serializeElements(this.formElementsInSection(container), true);
            this._updateContent(container, url, paramHash.merge(sectionHash), function (transport) {
            }.bind(this));

            if (itemType == 'agent') $('btn-add-agent').show();
        },

        _updateContent: function(container, url, params, onSuccessCallBack) {
            new Ajax.Request(url, {
                parameters: params.toQueryString(),
                onSuccess: function(transport) {
                    container.innerHTML = transport.responseText;
                    AE.registerCalendarPopups();
//                    refreshBoxes();
                }
            });

        },

        _insertContent: function(container, url, params, onCompleteCallBack) {
            new Ajax.Updater(container, url, {
                parameters: params.toQueryString(), onComplete: onCompleteCallBack, insertion: Insertion.Top, evalScripts: true
            });
        }
    })
</script>

<c:set var="hasSurgery" value="${command.study.surgeryPresent}" />
<c:set var="hasDevice" value="${command.study.devicePresent}" />
<c:set var="hasRadiation" value="${command.study.radiationPresent}" />
<c:set var="hasAgent" value="${command.study.drugAdministrationPresent}" />
<c:set var="hasBehavioral" value="${command.study.behavioralInterventionPresent}" />
<c:set var="hasBiological" value="${command.study.biologicalInterventionPresent}" />
<c:set var="hasGenetic" value="${command.study.geneticInterventionPresent}" />
<c:set var="hasDietary" value="${command.study.dietaryInterventionPresent}" />
<c:set var="hasOther" value="${command.study.otherInterventionPresent}" />

<div class="row">
    <div class="summarylabel">Treatment</div>
    <div class="summaryvalue">${command.aeReport.treatmentInformation.treatmentDescription != null ? command.aeReport.treatmentInformation.treatmentDescription : command.aeReport.treatmentInformation.treatmentAssignment.description}</div>
</div>

<form:form id="command">
        <chrome:flashMessage/>
        <tags:hasErrorsMessage />
        <tags:jsErrorsMessage/>
    
    <c:if test="${hasAgent}">
        <chrome:box title="Agents" collapsable="true">
            <jsp:attribute name="additionalTitle" />
            <jsp:body>
            	<tags:renderRow field="${fieldGroups.agentAdministered.fields[0]}"/>
                <div style="padding-left:20px;">
                    <tags:button cssClass="foo" id="btn-add-agent" color="blue" value="Add" icon="Add" type="button" onclick="addAgent();" size="small"/>
                    <tags:indicator id="agent_AjaxIndicator" />
                <div id="_agents">
					
                    <c:set var="size" value="${fn:length(command.aeReport.treatmentInformation.courseAgents)}" />
                    <c:forEach items="${command.aeReport.treatmentInformation.courseAgents}" varStatus="status" var="agent">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <c:set var="collapsed" value="${agent.studyAgent != null}" />
                        <c:if test="${!agent.studyAgent.retiredIndicator}">
                        	<ae:oneCourseAgent index="${newIndex}" agent="${agent}" collapsed="${collapsed}"/>
                        </c:if>	
                    </c:forEach>
                </div>
                </div>
            </jsp:body>
        </chrome:box>
    </c:if>

    <c:if test="${hasDevice}">
        <chrome:box title="Devices" collapsable="true">
            <jsp:attribute name="additionalTitle" />
            <jsp:body>
                <div style="padding-left:20px;">
                   <tags:button cssClass="foo" id="btn-add-device" color="blue" value="Add" icon="Add" type="button" onclick="addDevice();" size="small"/>
                    <tags:indicator id="device_AjaxIndicator" />
                <div id="_devices">
                <c:set var="size" value="${fn:length(command.aeReport.medicalDevices)}" />
                <c:forEach items="${command.aeReport.medicalDevices}" varStatus="status" var="device">
                    <c:set var="newIndex" value="${size - (status.index + 1)}" />
                    <ae:oneMedicalDevice index="${newIndex}" device="${device}" collapsed="true"/>
                </c:forEach>
            </div>
            </div>
            </jsp:body>
        </chrome:box>
    </c:if>

    <c:if test="${hasRadiation}">
        <chrome:box title="Radiation" collapsable="true">
            <jsp:attribute name="additionalTitle"/>
            <jsp:body>
                <div style="padding-left:20px;">
                    <tags:button cssClass="foo" id="btn-add-radiation" color="blue" value="Add" icon="Add" type="button" onclick="addRadiation();" size="small"/>
                    <tags:indicator id="radiation_AjaxIndicator" />
                <div id="_radiations">
                    <c:set var="size" value="${fn:length(command.aeReport.radiationInterventions)}" />
                    <c:forEach items="${command.aeReport.radiationInterventions}" varStatus="status" var="radiation">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <ae:oneRadiationIntervention index="${newIndex}" radiation="${radiation}" collapsed="true"/>
                    </c:forEach>
                </div>
                </div>
            </jsp:body>
        </chrome:box>
    </c:if>

    <c:if test="${hasSurgery}">
        <chrome:box title="Surgeries" collapsable="true">
            <jsp:attribute name="additionalTitle"/>
            <jsp:body>
                <div style="padding-left:20px;">
                    <tags:button cssClass="foo" id="btn-add-surgery" color="blue" value="Add" icon="Add" type="button" onclick="addSurgery();" size="small"/>
                    <tags:indicator id="surgery_AjaxIndicator" />
                <div id="_surgeries">
                    <c:set var="size" value="${fn:length(command.aeReport.surgeryInterventions)}" />
                    <c:forEach items="${command.aeReport.surgeryInterventions}" varStatus="status" var="surgery">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <ae:oneSurgeryIntervention index="${newIndex}" surgery="${surgery}" collapsed="true"/>
                    </c:forEach>
                </div>
                </div>
            </jsp:body>
        </chrome:box>
    </c:if>
	
    <c:if test="${hasBehavioral}">
        <chrome:box title="Behaviorals" collapsable="true">
            <jsp:attribute name="additionalTitle"/>
            <jsp:body>
                <div style="padding-left:20px;">
                    <tags:button cssClass="foo" id="btn-add-behavioral" color="blue" value="Add" icon="Add" type="button" onclick="addBehavioral();" size="small"/>
                    <tags:indicator id="behavioral_AjaxIndicator" />
                <div id="_behaviorals">
                    <c:set var="size" value="${fn:length(command.aeReport.behavioralInterventions)}" />
                    <c:forEach items="${command.aeReport.behavioralInterventions}" varStatus="status" var="behavioral">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <ae:oneBehavioralIntervention index="${newIndex}" behavioral="${behavioral}" collapsed="true"/>
                    </c:forEach>
                </div>
                </div>
            </jsp:body>
        </chrome:box>
    </c:if>

    <c:if test="${hasBiological}">
        <chrome:box title="Biologicals" collapsable="true">
            <jsp:attribute name="additionalTitle"/>
            <jsp:body>
                <div style="padding-left:20px;">
                    <tags:button cssClass="foo" id="btn-add-behavioral" color="blue" value="Add" icon="Add" type="button" onclick="addBehavioral();" size="small"/>
                    <tags:indicator id="biological_AjaxIndicator" />
                <div id="_behaviorals">
                    <c:set var="size" value="${fn:length(command.aeReport.behavioralInterventions)}" />
                    <c:forEach items="${command.aeReport.behavioralInterventions}" varStatus="status" var="behavioral">
                        <c:set var="newIndex" value="${size - (status.index + 1)}" />
                        <ae:oneBehavioralIntervention index="${newIndex}" behavioral="${behavioral}" collapsed="true"/>
                    </c:forEach>
                </div>
                </div>
            </jsp:body>
        </chrome:box>
    </c:if>

<%--
    <c:if test="${hasBehavioral}">
        <chrome:box title="Behavioral" collapsable="true"></chrome:box>
    </c:if>
--%>
        <ae:reportingContext allReportDefinitions="${command.applicableReportDefinitions}" selectedReportDefinitions="${command.selectedReportDefinitions}" />
    <tags:tabControls flow="${flow}" tab="${tab}" />
    <tags:tabFields tab="${tab}" />
</form:form>

</body>
</html>
