<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/extremecomponents.css"/>">
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>Enter basic AE information</title>

    <style type="text/css">
/* This is intended to apply to the grade longselect only */
.longselect {
	width: 40em;
	white-space: normal;
	overflow:visible;
}
.longselect label {
	padding-left: 3.0em;
	text-indent: -2.5em;
}
.first-item .delete-control {
	display: none;
}
.main-fields .extra {
	width: 100%
}
.ctcCategoryValueDiv {
	width:260px;
	overflow:visible;
	height:40px;
}
.ctcCategoryClass {
	overflow:auto;
	width:620px;
}
.help-content {
	width:550px;
}
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
        var aeReportId = ${empty command.aeReport.id ? 'null' : command.aeReport.id}

        var AETerminologyVersionID = ${not empty command.assignment.studySite.study.aeTerminology.ctcVersion ? command.assignment.studySite.study.aeTerminology.ctcVersion.id : command.assignment.studySite.study.aeTerminology.meddraVersion.id}
        var OtherMeddraTerminologyVersionID = ${not empty command.assignment.studySite.study.aeTerminology.ctcVersion ? (not empty command.assignment.studySite.study.otherMeddra.id ? command.assignment.studySite.study.otherMeddra.id : -1 ): -1}

        var grades = ['NORMAL','MILD', 'MODERATE', 'SEVERE', 'LIFE_THREATENING', 'DEATH'];

        // ----------------------------------------------------------------------------------------------------------------

        var initialCtcTerm = [ ]
        <c:forEach items="${command.aeReport.adverseEvents}" var="ae" varStatus="aeStatus">
            <c:if test="${not empty ae.adverseEventCtcTerm.ctcTerm}">
            initialCtcTerm[${aeStatus.index}] = {
                id: ${ae.adverseEventCtcTerm.term.id},
                category: {
                    id: ${ae.adverseEventCtcTerm.term.category.id},
                    ctc: {
                        id: ${ae.adverseEventCtcTerm.term.category.ctc.id}
                    }
                },
                fullName: '${ae.adverseEventCtcTerm.term.fullName}',
                fullNameWithMedDRA : '${ae.adverseEventCtcTerm.term.fullName}',
                lowLevelTermField : '${ae.lowLevelTerm.fullName}',
                otherRequired: ${ae.adverseEventCtcTerm.term.otherRequired}
            }
            </c:if>
        </c:forEach>

        var AESections = new Array();

        function ctcVersion() {
            return ctcVersion;
        }

        function termValueSelector(term) {
            return term.fullName;
        }

        var AESection = Class.create();
        Object.extend(AESection.prototype, {
            initialize: function(div, ctcTerm) {
                this.div = $(div)
                AESections.push(this);
                this.initialCtcTerm = ctcTerm;
                this.resetTermText()

                Event.observe(this._ctcCategoryId(), "change", this.clearSelectedTerm.bindAsEventListener(this))
                
                if (this.initialCtcTerm != null ){
                	if (this.initialCtcTerm.lowLevelTermField)  $(this._detailsForOtherLltInpId()).value = this.initialCtcTerm.lowLevelTermField
                }
                
                // Taking care of meddra or other 
                this.initializeMeddraOrOther(this.initialCtcTerm)

               

                initSearchField();
                AE.registerCalendarPopups(this.div.id);
            },

            _index: function() { return +this.div.getAttribute("item-index"); },

            _aeProperty:              function() { return "aeReport.adverseEvents[" + this._index() + "]" },
            _ctcDetailsId:            function() { return this._aeProperty() + ".ctc-details" },
            _ctcCategoryId:           function() { return this._aeProperty() + ".ctc-category" },
            _ctcTermId:               function() { return this._aeProperty() + ".adverseEventCtcTerm.ctcTerm" },
            _ctcTermInputId:          function() { return this._ctcTermId() + "-input" },
            _ctcTermChoicesId:        function() { return this._ctcTermId() + "-choices" },
            _ctcTermIndicatorId:      function() { return this._ctcTermId() + "-indicator" },
            _detailsForOtherId:       function() { return this._aeProperty() + ".detailsForOther" },
            _detailsForOtherRowId:    function() { return this._aeProperty() + ".detailsForOther-row" },
            _detailsForOtherLltId:    function() { return this._aeProperty() + ".lowLevelTerm" },
            _detailsForOtherLltInpId: function() { return this._detailsForOtherLltId() + "-input" },
            _detailsForOtherLltRowId: function() { return this._aeProperty() + ".lowLevelTerm-row" },
            _selectMeddraId:          function() { return "select-meddra-" + this._index() },
            _selectOtherId:           function() { return "select-other-" + this._index() },


            resetTermText: function() {
                if (this.initialCtcTerm) {
                    this.selectTerm(this.initialCtcTerm)
                    $(this._ctcTermInputId()).value = termValueSelector(this.initialCtcTerm)
                }
            },
            
            updateMeddraOrOther: function() {
            	var isMeddra     = true
                var meddraRow    = $(this._detailsForOtherLltRowId())
                var meddraRowInp = $(this._detailsForOtherLltInpId())
                var meddra       = $(this._detailsForOtherLltId())
                var otherRow     = $(this._detailsForOtherRowId())
                var other        = $(this._detailsForOtherId())
                
                if (isMeddra) {
                } else {
                    meddraRowInp.value = ""
                    meddra.value = ""
                }
            	
            },
            
            initializeMeddraOrOther: function(ctcTerm) {
            	var meddraRow    = $(this._detailsForOtherLltRowId())
                var otherRow     = $(this._detailsForOtherRowId())
                var meddraRowInp = $(this._detailsForOtherLltInpId())
                var other        = $(this._detailsForOtherId())
                
            	if (this.initialCtcTerm != null ) {
            		var meddra = ctcTerm.lowLevelTermField
            		 if (meddra.length == 0) {
            		 	meddraRowInp.value=""
                    	meddra.value=""
                	 } else {
                	 	 this.updateMeddraOrOther();
                    }
                } else{
                    this.updateMeddraOrOther();
                }
            },
            
            clearSelectedTerm: function() {
                $(this._ctcTermInputId()).className = 'pending-search'
                $(this._ctcTermInputId()).value = 'Begin typing here'
/*
                $(this._detailsForOtherId()).value = ""
                AE.slideAndHide(this._detailsForOtherRowId())
*/
            },

            selectTerm: function(newCtcTerm) {
                $A($(this._ctcCategoryId()).options).each(function(opt) {
                    if (opt.value == newCtcTerm.category.id) {
                        opt.selected = true
                    }
                })

                $(this._ctcTermId()).value = newCtcTerm.id
                if (newCtcTerm.otherRequired) {
                    if ($(this._ctcDetailsId()).visible()) {
                        AE.slideAndShow(this._detailsForOtherLltRowId())
                    } else {
                        $(this._detailsForOtherLltRowId()).show()
                    }
                } else {
                    AE.slideAndHide(this._detailsForOtherLltRowId())
                    $(this._detailsForOtherLltId()).value = ""
                }

                this.updateGrades(newCtcTerm.id)
                },

            updateGrades: function(ctcTermId) {
                createAE.getTermGrades(ctcTermId, function(grades) {
                    // Note that row index is 0 to 4, while grade is 1 to 5

                    // update text
                    grades.each(function(grade) {
                        var text = $(this._aeProperty() + ".grade-text-" + (grade.code - 1))
                        text.update(grade.code + ": " + grade.displayName.escapeHTML().gsub("(\\r\\n)|(\\n)|(\\r)", "<br>\n"))
                    }.bind(this))

                    // show & hide
                    var validCodes = grades.collect(function(g) { return g.code })
                    for (var i = 0 ; i <= 4 ; i++) {
                        var row = $(this._aeProperty() + ".grade-row-" + i)
                        if (validCodes.include(i + 1)) {
                            row.enableDescendants()
                            row.show()
                        } else {
                            row.hide()
                            row.disableDescendants()
                        }
                    }
                }.bind(this))
            },

            termPopulator: function(autocompleter, text) {
                createAE.matchTerms(text, AETerminologyVersionID, $F(this._ctcCategoryId()), 25, function(values) {
                    autocompleter.setChoices(values)
                })
            }

        })

        function postReset() {
            AESections.each(function(ae) {
                ae.resetTermText()
            })
        }

        var aesEditor;
        Event.observe(window, "load", function() {
        	 //only show the workflow tab, if it is associated to workflow
            var associatedToWorkflow = ${command.associatedToWorkflow};
            if(associatedToWorkflow){
            	<c:forEach items="${command.selectedReportsAssociatedToWorkflow}" var="report" varStatus="status">
	 	          	routingHelper.retrieveReviewCommentsAndActions('${report.id}');
 	          	</c:forEach>
            }
        
            // do this before any of the behaviors are applied to avoid their onChange side effects
            Event.observe("command", "reset", function() {
                // onReset fires _before_ the reset; delay action so it happens afterward
                setTimeout(postReset, 150)
            })
            
            aesEditor = new ListEditor("ae-section", createAE, "AdverseEvent", {
                addParameters: [aeReportId],
                addCallback: function(nextIndex) {
                    new AESection("ae-section-" + nextIndex);
                },
                reorderCallback : function(original, target){
                   var tempHTML = $("aeReport.adverseEvents["+original+"].startDate-indicator").innerHTML;
                   $("aeReport.adverseEvents["+original+"].startDate-indicator").innerHTML = $("aeReport.adverseEvents["+target+"].startDate-indicator").innerHTML;
                   $("aeReport.adverseEvents["+target+"].startDate-indicator").innerHTML = tempHTML;
                   $$('span.primary-indicator').each(function(el, indx){
                       if(indx == 0) el.innerHTML='[Primary]';
                       else el.innerHTML = '';
                   });
                },
                reorderable: false,
                deletable: false, 
                minimizeable: false
            }, "aeReport.adverseEvents")

            // item-index attribute is added by the list editor and it's needed to start up each AESection
            <c:forEach items="${command.aeReport.adverseEvents}" varStatus="status">
            new AESection("ae-section-${status.index}", initialCtcTerm[${status.index}])
            </c:forEach>

			Event.observe('main', 'click', function(evt){
				el = evt.element();
				var name = el.name;
				var value = el.value;
				if(value == 'MILD' || value == 'MODERATE' || value== 'SEVERE' || value == 'LIFE_THREATENING' || value == 'DEATH')
				{
					var idx = name.substring(23, name.indexOf(']'));
					var chkDeath = $('outcomes[' + idx + '][1]');
					if(chkDeath){
						chkDeath.checked = (value == 'DEATH');
					}
				}
			});
			
        })

        function showAjaxTable(an, ctc, tableId, outerTableId) {
            var ctcId = ctc;
            var parameterMap = getParameterMap('command');

            createAE.buildTermsTableByCategory(parameterMap, ctcId, tableId, showTable2);
            function showTable2(table) {
                //$('indicator').className = 'indicator'
                var testDiv = $(tableId);
                var testOuterDiv = $(outerTableId);
                
                testDiv.innerHTML = table;
                testDiv.show();
                testOuterDiv.show();
            }

        }

        function hideShowAllTable(popupTable) {
            var popupTableDiv = $(popupTable);
            popupTableDiv.hide();
        }

        function buildTable(form, ctcId, tableId) {
            var parameterMap = getParameterMap(form);
            createAE.buildTermsTableByCategory(parameterMap, ctcId, tableId, showTable2);

            function showTable2(table) {
                //$('indicator').className = 'indicator'
                var testDiv = $(tableId);
                testDiv.innerHTML = table;
                
                testDiv.show();
            }
        }

        function fillCtcTerm(termId, tableId) {

            var index = tableId.substring(tableId.length - 1, tableId.length)

            var div = $(tableId+'-outer')
            div.hide()
            createAE.getTermByTermId(termId, function(values) {

                var ae = AESections[index];
                ae.selectTerm(values[0])
                var ctcTerm = $('aeReport.adverseEvents[' + index + '].adverseEventCtcTerm.ctcTerm-input')
                ctcTerm.value = termValueSelector(values[0])
                ctcTerm.className='autocomplete'
                
            });
        }

        function enableDisableAjaxTable(tableIndex) {
            var testDiv = $('table' + tableIndex);
            if (!testDiv == '') {
                testDiv.hide()
            }

            if ($('aeReport.adverseEvents[' + tableIndex + '].ctc-category').value == '') {
                $('showAllTerm' + tableIndex).hide()
            } else {
                $('showAllTerm' + tableIndex).show()
            }
        }

        function addAdverseEvents(selectedTerms){
            var termId = selectedTerms.keys()[0];

            var tID = lookupByTermId(termId);
            if (tID != -1) {
                openDivisionById(tID);
                document.location = document.location.toString().split("#")[0] + "#adverseEventTerm-" + termId;
                return;
            }

           // var newIndex = $$(".ae-section").length;
			var externalFunction = createAE['addAdverseEventWithTerms'];
            var externalArgs = [termId];
			aesEditor.add('', externalFunction,externalArgs);
      
        }

        // ----------------------------------------------------------------------------------------------------------------

         function lookupByTermId(_id) {
            var list = $$('div.aeID-' + _id);
            for (i=0; i<list.length; i++) {
                return (list[i].id);
            }
            return -1;
         }

        // ----------------------------------------------------------------------------------------------------------------

         function closeDivisionById(_id) {
                 panelDiv = $("contentOf-" + _id);
                 imageId= 'image-' + _id;
                 imageSource = $(imageId).src;
                 CloseDown(panelDiv, arguments[1] || {});
                 document.getElementById(imageId).src = imageSource.replace('down','right');
         }

         // ----------------------------------------------------------------------------------------------------------------

         function openDivisionById(_id) {
                 panelDiv = $("contentOf-" + _id);
                 imageId= 'image-' + _id;
                 imageSource = $(imageId).src;
                 OpenUp(panelDiv, arguments[1] || {});
                 document.getElementById(imageId).src = imageSource.replace('right','down');
         }



</script>

</head>
<body>

<tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="ae_captureRoutine">
  <jsp:attribute name="instructions">
    <tags:instructions code="instruction_ae_enterBasics" />
  </jsp:attribute>
  <jsp:attribute name="repeatingFields">

    <div style="margin-left:20px; margin-bottom:10px;">
    	<tags:button color="blue" size="small" value="Add Adverse Event" icon="+"  markupWithTag="a"
			href="captureRoutine?study=${command.study.id}&participant=${command.participant.id}&adverseEventReportingPeriod=${command.aeReport.reportingPeriod.id}&_page=0&_target1=1&displayReportingPeriod=true&addReportingPeriodBinder=true" />
    </div>
    <c:forEach items="${command.aeReport.adverseEvents}" varStatus="status" var="ae">
      <ae:oneAdverseEvent index="${status.index}" collapsed="${status.index gt 0}" adverseEvent="${ae}"/>
    </c:forEach>
	<ae:reportingContext allReportDefinitions="${command.applicableReportDefinitions}" selectedReportDefinitions="${command.selectedReportDefinitions}" />
  </jsp:attribute>
  
</tags:tabForm>

</body>
</html>
