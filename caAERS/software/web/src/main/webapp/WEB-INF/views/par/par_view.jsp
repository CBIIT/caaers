<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title>View Subject</title>

    <style type="text/css">
        table.tablecontent {
            width: 100%;
        }

        table.single-fields {
            width: 100%;
        }

        div.row div.label {width:13em;}
        div.row div.value {margin-left:14em;}
        
    </style>
    <script>
        function submitPage(s) {
            document.getElementById("nextView").value = s;
            document.getElementById("participant").submit();
        }

    </script>
</head>
<body>

<c:set var="unidentifiedMode" value="${configuration.map.unidentifiedMode}" />

<c:set var="_title">
    <jsp:attribute name="value">
        <c:if test="${unidentifiedMode}">Unidentified</c:if>
        <c:if test="${!unidentifiedMode}">${command.participant.lastName}, ${command.participant.firstName}</c:if>
    </jsp:attribute>
</c:set>
<chrome:box title="${_title}">

<chrome:flashMessage/>
<chrome:division title="Demographic Information">
    <br>
    <table id="test2" class="single-fields" width="100%">
        <tr>
            <td>
                <c:if test="${!unidentifiedMode}">
                <div class="leftpane">
                    <div class="row">
                        <div class="label">First name&nbsp; </div>
                        <div class="value">${command.participant.firstName}</div>
                    </div>

                    <div class="row">
                        <div class="label">Last name&nbsp; </div>
                        <div class="value">${command.participant.lastName}</div>
                    </div>

                    <div class="row">
                        <div class="label">Maiden name&nbsp; </div>
                        <div class="value">${command.participant.maidenName}</div>
                    </div>

                    <div class="row">
                        <div class="label">Middle name&nbsp; </div>
                        <div class="value">${command.participant.middleName}</div>
                    </div>
                </div>
                </c:if>
            </td>
            <td>
                <div class="row">
                    <div class="label">Date of birth&nbsp; </div>
                    <div class="value">
                        <c:if test="${!unidentifiedMode}"><tags:validDate date="${command.participant.dateOfBirth}" /></c:if>
                        <c:if test="${unidentifiedMode}">${command.participant.dateOfBirth.monthString} / ${command.participant.dateOfBirth.yearString}</c:if>
                    </div>
                </div>

                <div class="row">
                    <div class="label">Ethnicity&nbsp; </div>
                    <div class="value">${command.participant.ethnicity}</div>
                </div>

                <div class="row">
                    <div class="label">Race&nbsp; </div>
                    <div class="value">${command.participant.race}</div>
                </div>


                <div class="row">
                    <div class="label">Gender&nbsp;</div>
                    <div class="value">${command.participant.gender}</div>
                </div>

            </td>
        </tr>
    </table>
</chrome:division>

<c:if test="${not empty command.participant.identifiers && !unidentifiedMode}">
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
                <th scope="col">Study Title</th>
                <th scope="col">Site</th>
                <th scope="col" width="150px">Study Subject Identifier</th>
            </tr>
            <c:forEach items="${command.assignments}" var="assignment" varStatus="i">
                    <tr class="results">
                        <td>${assignment.studySite.study.primaryIdentifier}</td>
                        <td>${assignment.studySite.study.shortTitle}</td>
                        <td>
   						<c:if test ="${assignment.studySite.organization.externalId != null}">
							<img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/>
						</c:if>                          
                        ${assignment.studySite.organization.name}</td>
                        <td>${assignment.studySubjectIdentifier}</td>
                    </tr>
            </c:forEach>

        </table>
        <br>
    </chrome:division>

<chrome:division title="Medical History">
    <c:forEach items="${command.assignments}" var="assignment" varStatus="i">
        <chrome:division title="${assignment.studySite.study.shortTitle} (${assignment.studySite.study.primaryIdentifier})" collapsable="true" id="a_id_${assignment.id}" collapsed="true">
            <table width=100% border="0" cellspacing="5">
                <tr><td colspan="2"><h2><tags:message key="section.general" /></h2></td></tr>
                <tr>
                    <td width="5%"></td>
                    <td colspan="3">
                        <div class="row">
                            <div class="label" style="text-align:left;"><tags:message
                                    key="baseline.performance"/>&nbsp;</div>
                            <div class="value">${assignment.baselinePerformance}</div>
                        </div>
                    </td>
                </tr>

                <tr><td colspan="4"><h2><tags:message key="section.disease.information" /></h2></td></tr>
                <tr>
                    <td></td>
                    <td colspan="3">
                        <div class="row">
                            <div class="label" style="text-align:left;"><tags:message key="disease.name"/>&nbsp;</div>
                            <div class="value">${assignment.diseaseHistory.abstractStudyDisease.termName}</div>
                        </div>
                        <div class="row">
                            <div class="label" style="text-align:left;"><tags:message key="primary.site.of.disease"/>&nbsp;</div>
                            <div class="value">
                                <c:if test="${assignment.diseaseHistory.codedPrimaryDiseaseSite.id == 110}">${assignment.diseaseHistory.codedPrimaryDiseaseSite.name} - ${assignment.diseaseHistory.otherPrimaryDiseaseSite}</c:if>
                                <c:if test="${assignment.diseaseHistory.codedPrimaryDiseaseSite.id != 110}">${assignment.diseaseHistory.codedPrimaryDiseaseSite.name}</c:if>
                            </div>
                        </div>
                        <div class="row">
                            <div class="label" style="text-align:left;"><tags:message key="date.of.initial.diagnosis"/>&nbsp;</div>
                            <div class="value"><tags:validDate date="${assignment.diseaseHistory.diagnosisDate}" /></div>
                        </div>
                    </td>
                </tr>

                <tr>
                    <td colspan="2"><h2><tags:message key="section.metastatic.disease.site" /></h2></td>
                </tr>
                <tr>
                    <td></td>
                    <td valign="top">
                        <c:if test="${fn:length(assignment.diseaseHistory.metastaticDiseaseSites) == 0}">
                            <i><tags:message key="no.disease.sites" /></i>
                        </c:if>
                        <c:forEach items="${assignment.diseaseHistory.metastaticDiseaseSites}" var="mds" varStatus="status">
                            <c:if test="${mds.codedSite.id != 110}">${mds.codedSite.name}</c:if>
                            <c:if test="${mds.codedSite.id == 110}">${mds.codedSite.name} - ${mds.otherSite}</c:if>
                            <br/>
                        </c:forEach>
                    </td>
                </tr>

                <tr>
                    <td colspan="2"><h2><tags:message key="section.preexisting.conditions" /></h2></td>
                </tr>
                <tr>
                    <td></td>
                    <td valign="top">
                            <c:if test="${fn:length(assignment.preExistingConditions) == 0}">
                                <i><tags:message key="no.preexisting.conditions" /></i>
                            </c:if>

                            <c:forEach items="${assignment.preExistingConditions}" var="pc" varStatus="status">
                                <c:if test="${!pc.preExistingCondition.retiredIndicator}">
                                    <c:if test="${not empty pc.preExistingCondition}">${pc.preExistingCondition.text}</c:if>
                                    <c:if test="${empty pc.preExistingCondition}">${pc.other}</c:if>
                                    <br/>
                                </c:if>
                            </c:forEach>
                    </td>
                </tr>

                <tr>
                    <td colspan="2"><h2><tags:message key="section.conmeds" /></h2></td>
                </tr>
                <tr>
                    <td></td>
                    <td valign="top">
                        <c:if test="${fn:length(assignment.concomitantMedications) == 0}">
                                <i><tags:message key="no.conmeds" /></i>
                        </c:if>

                        <c:if test="${fn:length(assignment.concomitantMedications) > 0}">
                        <table width="50%" class="tablecontent">
                            <tr>
                                <th>Name</th>
                                <th><tags:message key="still.taking" /></th>
                                <th><tags:message key="start.date" /></th>
                                <th><tags:message key="end.date" /></th>
                            </tr>
                            <c:forEach items="${assignment.concomitantMedications}" var="cm" varStatus="status">
                            <tr>
                                <td>${cm.agentName}</td>
                                <td><c:if test="${cm.stillTakingMedications}"><tags:message key="yes" /></c:if><c:if test="${!cm.stillTakingMedications}"><tags:message key="no" /></c:if></td>
                                <td><tags:validDate date="${cm.startDate}" /></td>
                                <td><tags:validDate date="${cm.endDate}" /></td>
                            </tr>
                            </c:forEach>
                        </table>
                        </c:if>
                    </td>
                </tr>

                <tr>
                    <td colspan="2"><h2><tags:message key="section.prior.therapies" /></h2></td>
                </tr>
                <tr>
                    <td></td>
                    <td valign="top">
                        <c:if test="${fn:length(assignment.priorTherapies) == 0}">
                                <i><tags:message key="no.prior.therapies" /></i>
                        </c:if>

                        <c:if test="${fn:length(assignment.priorTherapies) > 0}">
                        <table width="100%" class="tablecontent">
                            <tr>
                                <th>Name</th>
                                <th><tags:message key="comments" /></th>
                                <th><tags:message key="start.date" /></th>
                                <th><tags:message key="end.date" /></th>
                                <th><tags:message key="therapy.agents" /></th>
                            </tr>
                            <c:forEach items="${assignment.priorTherapies}" var="pt" varStatus="status">
                                <c:if test="${!pt.priorTherapy.retiredIndicator}">
                            <tr>
                                <td valign="top">${pt.priorTherapy.text}</td>
                                <td valign="top">${pt.other}</td>
                                <td valign="top"><tags:validDate date="${pt.startDate}" /></td>
                                <td valign="top"><tags:validDate date="${pt.endDate}" /></td>
                                <td valign="top">
                                    <c:if test="${fn:length(pt.priorTherapyAgents) > 0}">
                                        <c:forEach items="${pt.priorTherapyAgents}" var="pta" varStatus="status">
                                            ${pta.name}<br />
                                        </c:forEach>
                                    </c:if>
                                </td>
                            </tr>
                                </c:if>
                            </c:forEach>
                        </table>
                        </c:if>
                    </td>
                </tr>
            </table>
        </chrome:division>
    </c:forEach>
</chrome:division>

</chrome:box>

</body>
</html>
