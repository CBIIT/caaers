<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:stylesheetLink name="ae"/>
    <tags:includeScriptaculous/>
    <tags:dwrJavascriptLink objects="createAE,createStudy"/>
    <script type="text/javascript">
        var aeReportId = ${empty command.aeReport.id ? 'null' : command.aeReport.id}

        var EnterPriorTherapy = Class.create()
        Object.extend(EnterPriorTherapy.prototype, {
            initialize: function(index, priorTherapyName) {
                this.index = index
                var cmProperty = "aeReport.adverseEventPriorTherapies[" + index + "]";
                this.priorTherapyProperty = cmProperty + ".priorTherapy"
                this.otherProperty = cmProperty + ".other"

                if (priorTherapyName) $(this.priorTherapyProperty + "-input").value = priorTherapyName

                $("select-priorTherapy-" + this.index)
                    .observe("click", this.updatePriorTherapyOrOther.bind(this))
                $("select-other-" + this.index)
                    .observe("click", this.updatePriorTherapyOrOther.bind(this))

                AE.createStandardAutocompleter(
                    this.priorTherapyProperty, this.termPopulator.bind(this),
                    function(priorTherapy) { return priorTherapy.text })

                this.initializePriorTherapyOrOther()
            },

            termPopulator: function(autocompleter, text) {
                createAE.matchPriorTherapies(text, function(values) {
                    autocompleter.setChoices(values)
                })
            },

            updatePriorTherapyOrOther: function() {
                var isPriorTherapy = $("select-priorTherapy-" + this.index).checked
                var priorTherapyRow = $(this.priorTherapyProperty + "-row")
                var otherRow = $(this.otherProperty + "-row")
                if (isPriorTherapy) {
                    priorTherapyRow.removeClassName("disabled")
                    otherRow.addClassName("disabled")
                    priorTherapyRow.getElementsByClassName("value")[0].enableDescendants()
                    otherRow.getElementsByClassName("value")[0].disableDescendants()
                } else {
                    otherRow.removeClassName("disabled")
                    priorTherapyRow.addClassName("disabled")
                    otherRow.getElementsByClassName("value")[0].enableDescendants()
                    priorTherapyRow.getElementsByClassName("value")[0].disableDescendants()
                }
            },

            initializePriorTherapyOrOther: function() {
                var otherValue = $(this.otherProperty).value
                if (otherValue.length == 0) {
                    $("select-priorTherapy-" + this.index).click()
                } else {
                    $("select-priorTherapy-" + this.index).click()
                }
            }
        })

        Element.observe(window, "load", function() {
            <c:forEach items="${command.aeReport.adverseEventPriorTherapies}" varStatus="status" var="aePriorTherapy">
            new EnterPriorTherapy(${status.index}, '${aePriorTherapy.priorTherapy.text}')
            </c:forEach>

            new ListEditor("priorTherapy", createAE, "PriorTherapy", {
                addFirstAfter: "single-fields",
                addParameters: [aeReportId],
                addCallback: function(index) {
                    new EnterPriorTherapy(index);
                }
            })
        })
    </script>
</head>
<body>
<tags:tabForm tab="${tab}" flow="${flow}">
        <jsp:attribute name="instructions">
            You are entering prior therapies for ${command.assignment.participant.fullName}
            on ${command.assignment.studySite.study.shortTitle}. If there's
            an appropriate known therapies, enter that.  Otherwise, enter a description in the other
            field.
        </jsp:attribute>
      
        <jsp:attribute name="repeatingFields">
        <c:forEach items="${command.aeReport.adverseEventPriorTherapies}" varStatus="status">
            <ae:onePriorTherapy index="${status.index}"/>
        </c:forEach>    
        </jsp:attribute>
        
         <jsp:attribute name="localButtons">
         <tags:listEditorAddButton divisionClass="priorTherapy" label="Add a Therapy"/>
        <%--<input type="button" value="Add a Therapy" id="add-priorTherapy-button"/>
        <tags:indicator id="add-priorTherapy-indicator"/> --%>
         </jsp:attribute>
    </tags:tabForm>
</body>
</html>

