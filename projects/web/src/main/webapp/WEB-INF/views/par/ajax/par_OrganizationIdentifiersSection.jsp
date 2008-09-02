<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<tags:noform>
<c:forEach items="${indexes}" var="index" varStatus="status">

<par:parIdentifier
    title="Subject Identifier ${status.index + 1}"
    disableDelete="${fn:length(command.participant.organizationIdentifiers) lt 2}"
    sectionClass="organization-section-row"
    removeButtonAction="removeIdentifier"
    index="${index}"
    identifier="${command.participant.organizationIdentifiers[status.index]}"
    mainGroupName="mainOrg"
    containerName="addOrganizationIdentifierDiv"
    action="removeOrganizationIdentifier"/>
    
</c:forEach>
</tags:noform>