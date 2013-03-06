<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<tags:noform>
<c:forEach items="${indexes}" var="index" varStatus="status">

<par:parIdentifier
    title="Subject Identifier ${status.index + 1}"
    disableDelete="false"
    sectionClass="system-section-row"
    removeButtonAction="removeIdentifier"
    index="${index}"
    identifier="${command.participant.systemAssignedIdentifiers[status.index]}"
    mainGroupName="mainSys"
    containerName="addSystemIdentifierDiv"
    removeAction="removeSystemIdentifier"/>
</c:forEach>
</tags:noform>
