<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@attribute name="field" type="gov.nih.nci.cabig.caaers.web.ae.InputField"%>
<c:choose>
    <c:when test="${field.categoryName == 'text'}"><form:input path="${field.propertyName}"/></c:when>
    <%-- TODO: a calendar popup of some description --%>
    <c:when test="${field.categoryName == 'date'}"><form:input path="${field.propertyName}"/></c:when>
    <c:when test="${field.categoryName == 'textarea'}"><form:textarea path="${field.propertyName}"/></c:when>
    <c:when test="${field.categoryName == 'select'}">
        <form:select path="${field.propertyName}" items="${field.options}"/>
    </c:when>
    <c:when test="${field.categoryName == 'autocompleter'}">
        <input size="50" id="${field.textfieldId}"/>
        <div id="${field.choicesId}" class="autocomplete"></div>
        <form:hidden path="${field.propertyName}"/>
    </c:when>
    <c:otherwise>
        UNIMPLEMENTED FIELD TYPE ${field.type} for ${field.propertyName}
    </c:otherwise>
</c:choose>