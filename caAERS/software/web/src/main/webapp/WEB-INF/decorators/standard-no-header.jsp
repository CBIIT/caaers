<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp" %>
<c:set var="cntRF" value="${0}" scope="request" />
<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <standard:head/>
    <decorator:head/>
	<SCRIPT language="JavaScript">
        AE.SESSION_TIME_OUT_ENABLED = false;
	</SCRIPT>
    <style>
        #main {
            padding:0;
            top:0;
            margin-top:0;
        }
    </style>
</head>
<body>
<chrome:body title="${__decorator_title}">
    <decorator:body/>
</chrome:body>

</body>
</html>
