<%@ include file="/WEB-INF/views/taglibs.jsp" %>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui"%>
<%@taglib prefix="search" tagdir="/WEB-INF/tags/search"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>



<search:renderValueColumn index="${index}" uiAttribute="${uiAttribute }"></search:renderValueColumn>
