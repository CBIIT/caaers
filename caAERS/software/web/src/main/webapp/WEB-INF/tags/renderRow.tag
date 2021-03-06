<%-- 
	Can render field or a 'label or value'. The preference is given to label, and value attributes, if they are present the field is kind of ignored.
--%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>
<%@attribute name="field" type="gov.nih.nci.cabig.caaers.web.fields.InputField"%>
<%@attribute name="readOnly" type="java.lang.Boolean"%>
<%@attribute name="cssClass"%>
<%@attribute name="style"%>
<%@attribute name="extraParams"%>
<%@attribute name="label" fragment="true" %>
<%@attribute name="value" fragment="true" %>
<%@attribute name="deleteParams" %>
<c:set var="__splitDatePath" value="${field.propertyName}.year" />
<caaers:renderFilter elementID="${field.categoryName eq 'composite' ? field.attributes.subfields[0].propertyName: field.categoryName eq 'split_date' ? __splitDatePath : field.propertyName}">
<div class="row ${cssClass} ${field.required ? 'required' : ''} ${field.attributes.mandatory ? 'mandatory' : '' }" id="${field.propertyName}-row" <c:if test="${not empty style}">style="${style}"</c:if>>
    <div class="label ${field.required ? 'required' : ''} ${field.attributes.mandatory ? 'mandatory' : '' }" >
        <c:choose>
            <c:when test="${not empty label}"><jsp:invoke fragment="label"/></c:when>
            <c:otherwise><tags:renderLabel field="${field}"/></c:otherwise>
        </c:choose>
    </div>
    <div class="value ${field.required ? 'required' : ''} ${field.attributes.mandatory ? 'mandatory' : '' }">
        <c:choose>
            <c:when test="${not empty value}"><jsp:invoke fragment="value" /></c:when>
            <c:otherwise><tags:renderInputs disabled="${readOnly}" readonly="${readOnly}" field="${field}"/></c:otherwise>
        </c:choose>
	    <tags:extraParams extraParam="${field.attributes.extraParams}" />
        <c:if test="${field.attributes.enableDelete}"><input type="button" name="delete" value="Delete" onClick="javascript:fireRowDelete(${deleteParams},'${id}','${cssClass}');" /></c:if>
        <c:if test="${not empty extraParams}">${extraParams}</c:if>
    </div>
    <c:if test="${not empty field.attributes.details}">
        <div class="extra">${field.attributes.details}</div>
    </c:if>
</div>
</caaers:renderFilter>