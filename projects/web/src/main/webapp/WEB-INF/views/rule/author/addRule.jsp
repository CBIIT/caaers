<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>


<div id="rule-${ruleCount + 1}" class="section" style="display:none">
	<h3 style="position:relative; float:left" class="handle"">
	<span style="position:relative; float:left">Rule - (${ruleCount+1})</span>
	<a href="javascript:deleteRule(${ruleCount + 1})">
		<img id="close-image" src="<c:url value="/images/rule/window-close.gif"/>"  align="absmiddle"  style="position:relative; float:right; height:18px"/>
	</a>
	<img src="<c:url value="/images/chrome/spacer.gif"/>" style="position:relative; float:right;width:5px;height:10px" align="absmiddle" />
	<a href="javascript:toggle(${ruleCount + 1})">
		<img id="toggle-image-${ruleCount + 1}" onclick="" src="<c:url value="/images/rule/window-minimize.gif"/>" valign="top" align="absmiddle"  style="position:relative; float:right; height:18px"/>
	</a>
	</h3>
	<div id="crap-${ruleCount + 1}">
		<%--<form:form>--%>

		<div style="margin-left:50px;">
			<label class="label" for="ruleSet.rule[${ruleCount+1}].metaData.name">Name</label>
			<input id="ruleSet.rule[${ruleCount}].metaData.name" name="ruleSet.rule[${ruleCount}].metaData.name" style="width: 200px;" value="Rule-${ruleCount+1}" type="text" readonly="readonly" >
		</div>
		<br/>
		<div id="rule-condition-action-container-${ruleCount + 1}">
		<div style="margin-left:50px;">
			<label class="label" for="condition">Condition(s)</label>
		</div>
		
		<div class="row" id="rule-${ruleCount + 1}-columns">
			<br/>
			<c:forEach varStatus="conditionStatus" begin="0" items="${command.ruleSet.rule[ruleCount].condition.column}">
				<div id="condition-${conditionStatus.index}" style="margin-left:200px;" class="lineitem">
					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />
					<c:choose>
						<c:when test="${conditionStatus.index == 0}">
						<label for="IF">IF</label><img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:15px;height:1px" align="absmiddle" />
						</c:when>
						<c:otherwise><label for="AND">AND</label></c:otherwise>
					</c:choose>													
					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />


					<select id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].objectType" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].objectType" onchange="handleDomainObjectonChange(this, ${ruleCount})">
					        <option value="">Please select Domain Object</option>
					        
						<c:forEach items="${ruleUi.condition[0].domainObject}" varStatus="optionStatus">
							<option value="${ruleUi.condition[0].domainObject[optionStatus.index].className}">
							${ruleUi.condition[0].domainObject[optionStatus.index].displayUri}
							</option>
						</c:forEach>
						
					</select>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].identifier" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].identifier" value="${ruleUi.condition[0].domainObject[0].identifier}"/>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].displayUri" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].displayUri" value="${ruleUi.condition[0].domainObject[0].displayUri}"/>
					
					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />


					<select id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].fieldName" 
						name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].fieldName" onchange="handleFieldOnchange(this, ${ruleCount}, ${conditionStatus.index})">
						<option value="">Please select Field</option>
						<%--
						<c:forEach items="${ruleUi.condition[0].domainObject[0].field}" varStatus="optionStatus">
							<option value="${ruleUi.condition[0].domainObject[0].field[optionStatus.index].name}">
							${ruleUi.condition[0].domainObject[0].field[optionStatus.index].displayUri}
							</option>
						</c:forEach>
						--%>
					</select>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].expression" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].expression" value="${ruleUi.condition[0].domainObject[0].field[0].expression}"/>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].grammerPrefix" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].grammerPrefix" value="${ruleUi.condition[0].domainObject[0].field[0].grammer.prefix}"/>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].grammerPostfix" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].grammerPostfix" value="${ruleUi.condition[0].domainObject[0].field[0].grammer.postfix}"/>

					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].displayUri" name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].displayUri" value="${ruleUi.condition[0].domainObject[0].field[0].displayUri}"/>
					<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].displayUri"  name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].displayUri" value="${ruleUi.condition[0].domainObject[0].field[0].operator[0].readableText}"/>
						
						
					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />

					<select id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].evaluator" 
						name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].evaluator"
						onchange="handleOperatorOnchange(this, ${ruleCount})">
						<option value="">Please select operator</option>
						<%--
						<c:forEach items="${ruleUi.condition[0].domainObject[0].field[0].operator}" varStatus="optionStatus">
							<option value="${ruleUi.condition[0].domainObject[0].field[0].operator[optionStatus.index].name}">
							${ruleUi.condition[0].domainObject[0].field[0].operator[optionStatus.index].displayUri}
							</option>
						</c:forEach>
						--%>
					</select>


					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />


				<span id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].value.span">
				
					<select id="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].value" 
						name="ruleSet.rule[${ruleCount}].condition.column[${conditionStatus.index}].fieldConstraint[0].literalRestriction[0].value"
						onchange="onCategoryChange(this, ${ruleCount})">
						<option value="">Please select Value</option>	
						<%--
						<c:forEach items="${ruleUi.condition[0].domainObject[0].field[0].validValue}" varStatus="optionStatus">
							<option value="${ruleUi.condition[0].domainObject[0].field[0].validValue[optionStatus.index].value}">
							${ruleUi.condition[0].domainObject[0].field[0].validValue[optionStatus.index].displayUri}
							</option>
						</c:forEach>
						--%>
					</select>
				</span>

					<a href="javascript:fetchCondition(${ruleCount})">
						<img id="add-column-${ruleCount}" src="<c:url value="/images/rule/add_condition.gif"/>" align="absmiddle" style="cursor:hand; border:0px"/>
					</a>
				<c:if test="${columnCount > 0}">
					<a href="javascript:removeCondition(${ruleCount}, ${columnCount})">
						<img id="remove-column-${ruleCount}" src="<c:url value="/images/rule/remove_condition.gif"/>" align="absmiddle" style="cursor:hand;  border:0px"/>
					</a>
				</c:if>
				</div>

				<br/>

			</c:forEach>
			</div>
			<div class="row">
				<div  style="margin-left:50px;"><label for="action" class="label">Action</label></div>
				<br/>
				<div id="action-template"  style="margin-left:200px;">
					<img src="<c:url value="/images/chrome/spacer.gif"/>" style="width:10px;height:10px" align="absmiddle" />
					<select id="ruleSet.rule[${ruleCount}].action" name="ruleSet.rule[${ruleCount}].action" multiple="multiple" size="3">
						
						<c:choose>
							<c:when test='${command.ruleSetName == "Mandatory Sections Rules"}'>
								<c:forEach var="reportSectionName" items="${command.reportSectionNames}">
									<option value="${reportSectionName}">${reportSectionName}</option>
								</c:forEach>
							</c:when>
							<c:when test='${command.ruleSetName == "SAE Reporting Rules"}'>
								<c:forEach var="reportDefinition" items="${reportDefinitions}">
									<option value="${reportDefinition.name}">${reportDefinition.name}</option>
								</c:forEach>
								<option value="IGNORE">No Report Required (Study Level Exception Rule)</option>								
							</c:when>
							<c:otherwise>														
								<option value="ROUTINE_AE">Assess as Routine AE</option>														
								<option value="SERIOUS_ADVERSE_EVENT">Assess as Serious AE</option>														
								<c:forEach var="reportDefinition" items="${reportDefinitions}">
									<option value="${reportDefinition.name}">${reportDefinition.name}</option>
								</c:forEach>
							</c:otherwise>
						</c:choose>	
					</select>
					<%--					
					<a href="javascript:addAction(${ruleCount})">
						<img id="add-action-image" onclick="addAction(${ruleCount})" src="<c:url value="/images/rule/add_condition.gif"/>" align="absmiddle" style="cursor:hand"/>
					</a>
					<a href="javascript:addAction(${ruleCount})">
						<img id="remove-action-image" onclick="deleteAction(${ruleCount})" src="<c:url value="/images/rule/remove_condition.gif"/>" align="absmiddle" style="cursor:hand"/>											
					</a>
					--%>
				</div>
			</div>
		</div>
			
	<%--</form:form>--%>
	
</div>