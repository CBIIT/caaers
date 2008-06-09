<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="ctmsfn" uri="http://gforge.nci.nih.gov/projects/ctmscommons/taglibs/functions" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@attribute name="field" type="gov.nih.nci.cabig.caaers.web.fields.InputField"%>
<%@attribute name="size"%>
<%@attribute name="disabled" type="java.lang.Boolean" %>
<c:choose>
    <c:when test="${field.categoryName == 'text'}"><form:input path="${field.propertyName}" disabled="${disabled}" size="${empty size ? field.attributes.size : size}" title="${field.displayName}" cssClass="${field.required ? 'validate-NOTEMPTY&&MAXLENGTH2000' : 'validate-MAXLENGTH2000'}" /></c:when>
    <c:when test="${field.categoryName == 'date'}"><tags:dateInput path="${field.propertyName}" title="${field.displayName}" cssClass="${field.required ? 'validate-NOTEMPTY' : ''}" /></c:when>
    <c:when test="${field.categoryName == 'textarea'}"><form:textarea path="${field.propertyName}" disabled="${disabled}" cols="${not empty field.attributes.cols ? field.attributes.cols : ''}" rows="${not empty field.attributes.rows ? field.attributes.rows : ''}" title="${field.displayName}" cssClass="${field.required ? 'validate-NOTEMPTY&&MAXLENGTH2000' : 'validate-MAXLENGTH2000'}" /></c:when>
    <c:when test="${field.categoryName == 'checkbox'}"><form:checkbox path="${field.propertyName}" disabled="${disabled}"/></c:when>
    <c:when test="${field.categoryName == 'inplace_text'}"><tags:inplaceTextField field="${field}" /></c:when>
    <c:when test="${field.categoryName == 'lable'}"><tags:value propertyName="${field.propertyName}" /></c:when>
    <c:when test="${field.categoryName == 'select'}" >
        <form:select path="${field.propertyName}" items="${field.attributes.options}" disabled="${disabled}" title="${field.displayName}" cssClass="${field.required ? 'validate-NOTEMPTY' : ''}"/>
    </c:when>
    <c:when test="${field.categoryName == 'composite'}">
        <c:forEach items="${field.attributes.subfields}" var="subfield">
            <label>
                ${subfield.displayName}${empty subfield.displayName ? '' : ':'}
                <tags:renderInputs field="${subfield}"/>
            </label>
        </c:forEach>
    </c:when>
    <c:when test="${field.categoryName == 'autocompleter'}">
        <input size="${empty size ? empty field.attributes.size ? '50' : field.attributes.size : size}" type="text" id="${field.propertyName}-input" title="${field.displayName}" ${disabled ? 'disabled' : ''} class="autocomplete ${field.required ? 'validate-NOTEMPTY' : ''}"/>
        <tags:indicator id="${field.propertyName}-indicator"/>
        <c:if test="${field.attributes.enableClear and not disabled}"><input type="button" id="${field.propertyName}-clear" name="C" value="Clear" onClick="javascript:$('${field.propertyName}-input').clear();$('${field.propertyName}').clear();" /></c:if>
        <div id="${field.propertyName}-choices" class="autocomplete" style="display: none"></div>
        <form:hidden path="${field.propertyName}"/>
    </c:when>
    <c:when test="${field.categoryName == 'longselect'}">
        <div class="longselect" id="${field.propertyName}-longselect">
            <c:forEach items="${field.attributes.options}" var="option" varStatus="stat">
                <label id=${field.propertyName}-row-${stat.index}>
                    <form:radiobutton path="${field.propertyName}" value="${option.key}"
                        id="${field.propertyName}-radio-${stat.index}" cssClass="longselect-radio"/>
                    <span id="${field.propertyName}-text-${stat.index}">${ctmsfn:nl2br(option.value)}</span>
                </label>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        UNIMPLEMENTED FIELD TYPE ${field.categoryName} for ${field.propertyName}
    </c:otherwise>
</c:choose>
 	<c:if test="${not empty field.attributes.help}">
 		<tags:hoverHelp path="${field.propertyName}"><spring:message code="${field.attributes.help}" text="No help available ${field.attributes.help}" /></tags:hoverHelp>
    </c:if>
<tags:errors path="${field.propertyName}"/>
<tags:errors path="${field.propertyName}.*"/>
