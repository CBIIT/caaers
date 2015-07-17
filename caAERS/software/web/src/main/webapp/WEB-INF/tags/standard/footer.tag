<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<tags:ssoForm/>
<tags:jsDebug debugOn="${configuration.map.showDebugInformation}"/>
<c:if test="${configuration.map.showDebugInformation}">
    <tags:debugInfo/>
</c:if>
<div id="build-name"><c:out value="${buildInfo.buildName}" escapeXml="true"/></div>
