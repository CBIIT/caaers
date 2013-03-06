<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="commons" uri="http://bioinformatics.northwestern.edu/taglibs/commons"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>

<html>

<head>
    <title>caAERS diagnosis , happy.jsp</title>

	<link rel="stylesheet" type="text/css"/>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

	<style type="text/css">

       .label { width: 12em; padding: 1px;  margin-right: 0.5em; } 

       div.row div.value  {white-space:normal;}
       
       .pane {
  			float : none
		}
		
		div.row div.pass {
		  line-height: 1.5em;
		  font-weight: bold;
		  color: green;
		}
		div.row div.fail {
		  line-height: 1.5em;
		  font-weight: bold;
		  color: red;
		}
		div.row div.error {
		  line-height: 1.5em;
		  color: red;
		}
	</style>
    <script type="text/javascript">
        AE.PAGE_HELP_LINK = 'happy';
    </script>
</head>
<body>


     <div class="workflow-tabs2">
  <ul id="" class="tabs autoclear">
    <li id="thirdlevelnav" class="tab"><div>
        <a href="trackReports">Report Logs</a>
    </div></li>
    <li id="thirdlevelnav" class="tab selected"><div>
        <a href="#">System Status</a>
    </div></li>
  </ul>
</div>



 		
<chrome:box title="caAERS Diagnostics">
<chrome:division title="Configuration">
   <div class="row">
        	<div class="leftpanel">
        		<div class="row">
                	<div class="label">PSC URL </div>
                	<div class="value">${command.pscUrl}</div>
            	</div>

           		<div class="row">
                	<div class="label">LabViewer URL</div>
                	<div class="value">${command.labViewerUrl} </div>
            	</div>

            	<div class="row">
                	<div class="label">caXchange URL</div>
                	<div class="value">${command.caExchangeUrl}</div>
            	</div>            	
            </div>
	  </div>
  </chrome:division>
  <chrome:division title="Email Configuration">

	      <div class="row">
      
        	<div class="leftpanel">
        		<div class="row">
                	<div class="label">SMTP Host</div>
                	<div class="value">${command.smtpHost}</div>
            	</div>
           		<div class="row">
                	<div class="label">SMTP Port</div>
                	<div class="value">${command.smtpPort}</div>
            	</div>
            	
			<c:if test="${command.smtpTestResult}">
				<div class="row">
                	<div class="label">Test Connection</div>
                	<div class="pass">Pass</div>
            	</div> 
			</c:if>
            <c:if test="${not command.smtpTestResult}">
				<div class="row">
                	<div class="label">Test Connection</div>
                	<div class="fail">Fail</div>
            	</div> 
            	<div class="row">
                	<div class="label">StackTrace</div>
                	<div class="error">${command.smtpError}</div>
            	</div> 
			</c:if>	
           	
            </div>
	  </div>
  </chrome:division>

<chrome:division title="ServiceMix Configuration">
   <div class="row">
      
        	<div class="leftpanel">

         		<div class="row">
                	<div class="label">ServiceMix URL</div>
                	<div class="value">${command.serviceMixUrl}</div>
            	</div>
            	           	
			<c:if test="${command.serviceMixUp}">
				<div class="row">
                	<div class="label">ServiceMix Status</div>
                	<div class="pass">Active</div>
            	</div> 
			</c:if>
			
            <c:if test="${not command.serviceMixUp}">
				<div class="row">
                	<div class="label">ServiceMix Status</div>
                	<div class="fail">InActive</div>
            	</div> 
			</c:if>	
           	
            </div>
	  </div>
  </chrome:division>
  </chrome:box>
  <div class="content buttons autoclear"/>
</div>


</body>
</html>
