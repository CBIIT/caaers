<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
    <title>Participant Review</title>
    <tags:stylesheetLink name="participant"/>
    <script>
        function submitPage(s) {
            document.getElementById("nextView").value = s;
            document.getElementById("command").submit();
        }

    </script>
</head>
<body>

<tags:tabForm tab="${tab}" flow="${flow}"
              title="${command.participant.lastName}, ${command.participant.firstName}"
              willSave="false">

<jsp:attribute name="repeatingFields">
    	<c:if test="${(empty command.participant.id) or ( command.participant.id le 0) }">
            <input type="hidden" name="_finish" value="true"/>
        </c:if>

    <chrome:division title="Demographic Information">
        <br>
        <table id="test2" class="single-fields" width="100%">
            <tr>
                <td>
                    <div class="leftpane">
                        <div class="row">
                            <div class="label">First name:</div>
                            <div class="value">${command.participant.firstName}</div>
                        </div>
                        
                        <div class="row">
                            <div class="label">Last name:</div>
                            <div class="value">${command.participant.lastName}</div>
                        </div>

                        <div class="row">
                            <div class="label">Maiden name:</div>
                            <div class="value">${command.participant.maidenName}</div>
                        </div>

                        <div class="row">
                            <div class="label">Middle name:</div>
                            <div class="value">${command.participant.middleName}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="row">
                        <div class="label">Date of birth:</div>
                        <div class="value">${command.participant.dateOfBirth}</div>
                    </div>

                    <div class="row">
                        <div class="label">Ethnicity:</div>
                        <div class="value">${command.participant.ethnicity}</div>
                    </div>

                    <div class="row">
                        <div class="label">Race:</div>
                        <div class="value">${command.participant.race}</div>
                    </div>


                    <div class="row">
                        <div class="label">Gender:</div>
                        <div class="value">${command.participant.gender}</div>
                    </div>

                </td>
            </tr>
        </table>
    </chrome:division>
	
    <c:if test="${not empty command.participant.identifiers}">
        <chrome:division title="Identifiers">
            <table class="tablecontent" width="100%">
                <tr>
                    <th scope="col">Assigning Authority</th>
                    <th scope="col">Identifier Type</th>
                    <th scope="col">Identifier</th>
                </tr>
                <c:forEach items="${command.participant.identifiers}" var="identifier">
                    <tr class="results">
                        <c:if test="${(identifier.class.name eq 'gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier') }">
                            <td>${identifier.organization}</td>
                        </c:if>
                        <c:if test="${(identifier.class.name eq 'gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier') }">
                            <td>${identifier.systemName}</td>
                        </c:if>
                        <td>${identifier.type}</td>
                        <td>${identifier.value}</td>
                    </tr>
                </c:forEach>
            </table>
            <br>
        </chrome:division>
    </c:if>
		
        <chrome:division title="Study Subject Assignments">
            <table class="tablecontent" width="100%">
                <tr>
                    <th scope="col" width="150px">Study Primary ID</th>
                    <th scope="col">Study Short Title</th>
                    <th scope="col">Site</th>
                    <th scope="col" width="150px">Study Subject Identifier</th>
                </tr>
                <c:forEach items="${command.assignments}" var="assignment" varStatus="i">
                        <tr class="results">
                            <td>${assignment.studySite.study.primaryIdentifier}</td>
                            <td>${assignment.studySite.study.shortTitle}</td>
                            <td>${assignment.studySite.organization.name}</td>
                            <td>${assignment.studySubjectIdentifier}</td>
                        </tr>
                </c:forEach>

            </table>
            <br>
        </chrome:division>
	        
    </jsp:attribute>
</tags:tabForm>
</body>
</html>
