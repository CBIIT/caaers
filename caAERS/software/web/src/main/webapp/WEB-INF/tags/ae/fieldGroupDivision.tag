<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="fieldGroupFactoryName" required="true" type="java.lang.String" %>
<%@attribute name="style"%>
<%@attribute name="deleteParams"%>
<%@attribute name="enableDelete"%>
<%@attribute name="id"%>
<%@attribute name="collapsed" type="java.lang.Boolean" %>
<%@attribute name="title" description="Title to be displayed on the division header" %>

<%@variable name-given="fieldGroup" %>

<c:set var="fieldGroupName"><c:out value="${fieldGroupFactoryName}" escapeXml="true"/><c:out value="${index}" escapeXml="true"/></c:set>
<c:set var="fieldGroup" value="${fieldGroups[fieldGroupName]}"/>

<chrome:division title="${empty title ? fieldGroup.displayName : title}" cssClass="${fieldGroupFactoryName}" id="${fieldGroupFactoryName}-${index}" style="${style}" collapsable="true" enableDelete="${enableDelete}" deleteParams="${deleteParams}" collapsed="${collapsed}">
    <jsp:doBody/>
</chrome:division>
