<%-- This is the standard decorator for all caAERS pages --%>
<%@taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="standard" tagdir="/WEB-INF/tags/standard"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="blue" tagdir="/WEB-INF/tags/blue" %>

<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>caAERS || <decorator:title/></title>
<standard:head/>
<decorator:head/>
</head>
<body>
<div id="all">
<standard:header/>

<c:set var="__decorator_title"><decorator:title/></c:set>
<blue:body title="${__decorator_title}">
    <blue:flashMessage/>
    <decorator:body/>
</blue:body>

<standard:footer/>
</div>
</body>
</html>
