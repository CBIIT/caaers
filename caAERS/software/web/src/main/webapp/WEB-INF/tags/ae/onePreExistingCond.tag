<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>

<%@attribute name="index" required="true"%>
<%@attribute name="preExistingCondition" type="gov.nih.nci.cabig.caaers.domain.PreExistingCondition"%>
<%@attribute name="otherValue" type="java.lang.String"%>

<c:set var="mainGroup">preExistingCondition${index}</c:set>
<c:set var="preCondField" value="${fieldGroups[mainGroup].fields[0]}" />

<c:set var="otherField" value="${fieldGroups[mainGroup].fields[1]}" />        

<chrome:division id="aeReport.saeReportPreExistingConditions[${index}]" collapsable="false"
deleteParams="'preExistingCondition', ${index}, 'anchorPreExistingCondition', {}" enableDelete="true" collapsed="false" skipHeaderHTMLTag="true">

    <jsp:attribute name="title">
		<c:out value="${not empty preExistingCondition.text ? preExistingCondition.text : otherValue }" />
	</jsp:attribute>
    <jsp:attribute name="titleFragment" >
            <c:if test="${empty preExistingCondition.text && empty otherValue}">
    <ui:row path="aeReport.saeReportPreExistingConditions[${index}]">
        <jsp:attribute name="label">
            <ui:label path="aeReport.saeReportPreExistingConditions[${index}].preExistingCondition" mandatory="${preCondField.attributes.mandatory}"
            required="${preCondField.required}" text="${preCondField.displayName}"/>
        </jsp:attribute>
        <jsp:attribute name="value">
            <ui:select options="${preExistingConditionOptions}" path="aeReport.saeReportPreExistingConditions[${index}].preExistingCondition"
                       mandatory="${preCondField.attributes.mandatory}"
                       required="${preCondField.required}" ></ui:select>&nbsp;
                <c:set var="display" value="${empty preExistingCondition ? 'inline' : 'none'}" />
                <span id="otherPreExistingCondition_${index}" style="display:none;">
                    <%-- Other, Specify--%>
                    <c:if test="${otherField.required or otherField.attributes.mandatory}"><tags:requiredIndicator/></c:if>&nbsp;Other, specify <ui:text path="${otherField.propertyName}" mandatory="${otherField.attributes.mandatory}" />
                </span>
            <tags:errors path="aeReport.saeReportPreExistingConditions[${index}]"/>
        </jsp:attribute>
    </ui:row>
    </c:if>
    </jsp:attribute>

    <jsp:body>

<%--<ae:fieldGroupDivision fieldGroupFactoryName="preExistingCondition" index="${index}" enableDelete="true" deleteParams="'preExistingCondition', ${index}, '_preExistingConditions'">--%>

<%--</ae:fieldGroupDivision>--%>

<script language="JavaScript">
    Event.observe("aeReport.saeReportPreExistingConditions[${index}].preExistingCondition", "change", function() {
        if (($('aeReport.saeReportPreExistingConditions[${index}].preExistingCondition').options[$('aeReport.saeReportPreExistingConditions[${index}].preExistingCondition').selectedIndex].value) == "")
            $('otherPreExistingCondition_${index}').show();
        else
            $('otherPreExistingCondition_${index}').hide();
    })
</script>

<script>

    function setTitlePEC_${index}() {
        var titleID = $('titleOf_aeReport.saeReportPreExistingConditions[${index}]');
        var select = $("aeReport.saeReportPreExistingConditions[${index}].preExistingCondition");
        var value = select.options[select.selectedIndex].text;
        $(titleID).innerHTML = value;
    }

    Event.observe($("aeReport.saeReportPreExistingConditions[${index}].preExistingCondition"), "change", function() {
        setTitlePEC_${index}();
    });
</script>        

</jsp:body>
</chrome:division>    