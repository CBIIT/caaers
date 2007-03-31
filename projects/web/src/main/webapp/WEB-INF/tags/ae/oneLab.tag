<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="style"%>
<c:set var="fieldGroupName">lab${index}</c:set>
<c:set var="fieldGroup" value="${fieldGroups[fieldGroupName]}"/>

<chrome:division title="${fieldGroup.displayName}" cssClass="lab" id="lab-${index}" style="${style}">
    <tags:renderRow field="${fieldGroup.fields[0]}"/>
    <tags:renderRow field="${fieldGroup.fields[1]}"/>
    <c:forEach begin="2" end="7" step="2" var="i">
        <div class="row">
            <div class="label"><tags:renderLabel field="${fieldGroup.fields[i]}"/></div>
            <div class="value">
                <tags:renderInputs field="${fieldGroup.fields[i]}"/>
                <form:label path="${fieldGroup.fields[i+1].propertyName}">date</form:label>
                <tags:renderInputs field="${fieldGroup.fields[i+1]}"/>
            </div>
        </div>
    </c:forEach>
</chrome:division>
