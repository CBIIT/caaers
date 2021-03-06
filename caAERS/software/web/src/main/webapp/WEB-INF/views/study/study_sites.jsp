<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp" %>

<csmauthz:accesscontrol var="_isSSPAonCC" scope="request" domainObject="${command.study.studyCoordinatingCenter.organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_site_participation_administrator" />
<csmauthz:accesscontrol var="_isSSPAonFS" scope="request" domainObject="${command.study.primaryFundingSponsor.organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_site_participation_administrator" />

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title>${tab.longTitle}</title>
    <tags:dwrJavascriptLink objects="createStudy"/>

    <script language="JavaScript" type="text/JavaScript">
        var addSiteEditor;
        function fireDelete(selected, trClass) {
            var confirmation = confirm("Do you really want to delete?");
            if (!confirmation) return; //return if not agreed.

            if($('study.studySites'+ selected + '.retiredIndicator')) {
                $('study.studySites'+ selected + '.retiredIndicator').value = 'true';
            }
            ValidationManager.validate = false;
            var ssfrm = $('command');
            ssfrm._target.name = '_noname';
            ssfrm._action.value = 'removeSite';
            ssfrm._selected.value = selected;
            ssfrm.submit();
        }

/*
        function refreshDeleteButtons() {
            var deleteBtns = $$('.del-ss-section');
            if (deleteBtns.length > 1) {
                //deleteBtns[0].enable();
                deleteBtns[0].show();
            }
            if (deleteBtns.length == 1) {
                //deleteBtns[0].disable();
                deleteBtns[0].hide();
            }

        }
*/

        var jsStudySite = Class.create();
        Object.extend(jsStudySite.prototype, {
            initialize: function(index, orgName) {
                this.index = index;
                this.orgName = orgName;
                this.sitePropertyName = "study.studySites[" + index + "].organization";
                this.siteInput = $(this.sitePropertyName + "-input");
                this.options = {minChars : AE.autocompleterDelay , frequency : AE.autocompleterChars};
                
                if(this.siteInput){
                   if(orgName){
                        this.siteInput.value = orgName;
                   }
                   AE.createStandardAutocompleter(this.sitePropertyName,
                                 this.sitePopulator.bind(this),
                                 this.siteSelector.bind(this),
                                 this.options
                   );
                }
                
            },
            sitePopulator: function(autocompleter, text) {
                createStudy.restrictOrganizations(text, ${_isSSPAonCC || _isSSPAonFS}, ${command.study.ctepEsysIdentifier ne null}, function(values) {
                    autocompleter.setChoices(values)
                })
            },

            siteSelector: function(organization) {
            	var image;            	
            	if(organization.externalId != null){
                          image = '&nbsp;<img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/>';
                } else {
                          image = '';
                }
                
                var nciInstituteCode = organization.nciInstituteCode == null ? "" : " ( " + organization.nciInstituteCode + " ) ";
                return image + "" +organization.name + nciInstituteCode 
            }

        });

        Event.observe(window, "load", function() {
        <c:forEach varStatus="status" items="${command.study.studySites}" var="ss">
            new jsStudySite(${status.index}, "${ss.organization.fullName}");
        </c:forEach>
            addSiteEditor = new ListEditor('ss-section', createStudy, "StudySite", {
                addFirstAfter: "ss-table-head",
                nextIndexCallback : function(){
                	return $('_ITEM_COUNT').value;
            	},
                addCallback: function(nextIndex) {
                    //initilze auto completer and calendar
                    new jsStudySite(nextIndex);
                    $('_ITEM_COUNT').value = parseInt($('_ITEM_COUNT').value) + 1;
                    // refreshDeleteButtons();
                }
            });


            // enable-disable delete buttons
            // refreshDeleteButtons();

        });

    </script>
</head>

<body>
<study:summary/>
<tags:tabForm tab="${tab}" flow="${flow}" formName="studySiteForm" hideErrorDetails="false" willSave="${not empty command.study.id}">
    <jsp:attribute name="singleFields">
		<p><tags:instructions code="study.study_sites.top"/></p>
		<input type="hidden" name="_action" value="">
		<input type="hidden" name="_selected" value="">
		<input type="hidden" id="_ITEM_COUNT" name="_ITEM_COUNT" value="${fn:length(command.study.studySites)}">
 	    <div align="left" style="margin-left: 50px">
             <tags:table contentID="studySites" width="100%">
             <table width="100%" border="0" cellspacing="1" cellpadding="3">
                 <tr id="ss-table-head" bgcolor="#cccccc">
                     <th width="95%" class="tableHeader"><tags:requiredIndicator/>Site</th>
                     <th width="5%" class="tableHeader" style=" background-color: none"><caaers:message code="table.action" /></th>
                 </tr>
                 <c:forEach varStatus="status" items="${command.study.studySites}" var="ss">
                 	<c:if test="${not ss.retired}">
                     <study:oneStudySite cssClass="ss-section" index="${status.index}" readOnly="${not empty ss.id}"/>
                    </c:if>
                     <form:hidden id="study.studySites${status.index}.retiredIndicator" path="study.studySites[${status.index}].retiredIndicator"   />
                 </c:forEach>
             </table>
             </tags:table>
         </div>
		<br>
        <tags:listEditorAddButton divisionClass="ss-section" label="Add Study Site"/>
        
    </jsp:attribute>

</tags:tabForm>
</body>
</html>
