<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<html>
<head>
  <title>Configure Password Policy</title>
  <style type="text/css">


    .nested_section {
     width: 100%;
     margin-left: 20px;
    }

    .required_label {
     text-align: left;
     float: left;
     line-height: 23px;
     margin: 0px 5px;
    }

    .required_value {
     float: left;
     line-height: 20px;
    }
    
    .required_item_heading {
     clear: both;
    }

    .updated {
     border: #494 solid;
     border-width: 1px 0;
     background-color: #8C8;
     padding: 1em 2em;
     text-align: center;
     margin: 1em 30%;
     color: #fff;
     font-weight: bold;
     font-size: 1.1em;
    }
  </style>
  <script type="text/javascript">
      AE.PAGE_HELP_LINK = 'passwordPolicyConfigure';
  </script>
</head>
<body>
	<div class="tabpane">
	<div class="workflow-tabs2">
    	    <ul id="" class="tabs autoclear">
    	    	<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.tools.configuration.Configuration:READ || gov.nih.nci.cabig.caaers.tools.configuration.Configuration:UPDATE">
        	    	<li id="thirdlevelnav" class="tab">
           	    		<div>
                    		<a href="configure"><caaers:message code="configure.menu.general"/></a>
                		</div>
            		</li>
            	</csmauthz:accesscontrol>
            	<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.PasswordPolicy:READ || gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.PasswordPolicy:UPDATE">
            		<li id="thirdlevelnav" class="tab selected">
                		<div>
                   			<a href="passwordPolicyConfigure"><caaers:message code="configure.menu.passwordPolicy"/></a>
                		</div>
            		</li>
            	</csmauthz:accesscontrol>
            	<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.CaaersFieldDefinition:READ || gov.nih.nci.cabig.caaers.domain.CaaersFieldDefinition:UPDATE">
            		<li id="thirdlevelnav" class="tab">
                		<div>
                   			<a href="mandatoryFields"><caaers:message code="configure.menu.mandatoryFields"/></a>
                		</div>
            		</li>
            	</csmauthz:accesscontrol>
        	</ul>
    	</div> 
    <form:form action="${action}">
    <chrome:box title="Password Policy Configuration" autopad="true">

    <c:url value="/pages/admin/passwordPolicyConfigure" var="action" />
    <p><tags:instructions code="passwordconfig" /></p>

      <chrome:division title="Login Policy">
	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Maximum password age (seconds):</div>
	    <div class="required_value">
	      <form:input path="loginPolicy.maxPasswordAge" size="8"/>
	    </div>
	  </div>
	</div>
	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Number of allowed failed login attempts:</div>
	    <div class="required_value">
	      <form:input path="loginPolicy.allowedFailedLoginAttempts" size="3"/>
	    </div>
	  </div>
	</div>

	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Allowed login time (seconds):</div>
	    <div class="required_value">
	      <form:input path="loginPolicy.allowedLoginTime" size="8"/>
	    </div>
	  </div>
	</div>
	
	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Lockout duration (seconds):</div>
	    <div class="required_value">
	      <form:input path="loginPolicy.lockOutDuration" size="8"/>
	    </div>
	  </div>
	</div>
      </chrome:division>
		
      <chrome:division title="Password Creation Policy">
	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Minimum password age (seconds):</div>
	    <div class="required_value">
	      <form:input path="passwordCreationPolicy.minPasswordAge" size="8"/>
	    </div>
	  </div>
	</div>

	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Password history size:</div>
	    <div class="required_value">
	      <form:input path="passwordCreationPolicy.passwordHistorySize" size="3"/>
	    </div>
	  </div>
	</div>

	<div class="nested_section">
	  <div class="row">
	    <div class="required_label">Minimum password length:</div>
	    <div class="required_value">	
	      <form:input path="passwordCreationPolicy.minPasswordLength" size="3"/>
	    </div>
	  </div>
	</div>  
	</chrome:division>
	<chrome:division title="Complexity Requirement" style="margin-left: 20px;">	  
	  
	  <div class="nested_section">
	    <div class="row">
	      <div class="required_label">Meets the following complexity requirements:</div>
	    </div>
	  </div>

	  <div class="nested_section">
	    <div class="nested_section">
	      <div class="row">
		<div class="required_value">
		  <form:checkbox path="passwordCreationPolicy.combinationPolicy.upperCaseAlphabetRequired"/>
		</div>
		<div class="required_label">At least one uppercase letter</div>
	      </div>
	    </div>
	    
	    <div class="nested_section">
	      <div class="row">
		<div class="required_value">
		  <form:checkbox path="passwordCreationPolicy.combinationPolicy.lowerCaseAlphabetRequired"/>
		</div>
		<div class="required_label">At least one lowercase letter</div>
	      </div>
	    </div>		
	    
	    <div class="nested_section">
	      <div class="row">
		<div class="required_value">
		  <form:checkbox path="passwordCreationPolicy.combinationPolicy.nonAlphaNumericRequired"/>
		</div>
		<div class="required_label">At least one non-alphanumeric character</div>
	      </div>
	    </div>
	    
	    <div class="nested_section">
	      <div class="row">
		<div class="required_value">
		  <form:checkbox path="passwordCreationPolicy.combinationPolicy.baseTenDigitRequired"/>
		</div>
		<div class="required_label">At least one digit</div>
	      </div>
	    </div>
	  </div>

	  <div class="nested_section">
	    <div class="row">
	      <div class="required_label">Largest substring of username allowed:</div>
	      <div class="required_value">
		<form:input path="passwordCreationPolicy.combinationPolicy.maxSubstringLength" size="3"/>
	      </div>
	    </div>
	  </div>
	</chrome:division>


      <c:if test="${updated}"><p class="updated">Settings saved</p></c:if>

  </chrome:box>
  	<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.PasswordPolicy:UPDATE">
	    <div class="row submit" style="float: right;"><tags:button type="submit" value="Save" color="green" icon="save" /></div>
    </csmauthz:accesscontrol>
    </form:form>
    </div>
</body>
</html>
