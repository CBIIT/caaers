<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>

<div id="rule-${ruleCount}-column-${columnCount}" style="display:none" class="lineitem one-condition">

	<label for="AND" style="font-weight:bold;">And</label>		

	<select id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].objectType" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].objectType" onchange="handleDomainObjectonChange(this, ${ruleCount})">>
	        <option value="">Please select domain object</option>
		<c:forEach items="${command.ruleUi.condition[0].domainObject}" varStatus="optionStatus">
			<option value="${command.ruleUi.condition[0].domainObject[optionStatus.index].className}">
				${command.ruleUi.condition[0].domainObject[optionStatus.index].displayUri}
			</option>
		</c:forEach>
	</select>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].identifier" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].identifier" value="${command.ruleUi.condition[0].domainObject[0].identifier}"/>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].displayUri" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].displayUri" value="${command.ruleUi.condition[0].domainObject[0].displayUri}"/>


	
	<select id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].fieldName" 
			name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].fieldName" onchange="handleFieldOnchange(this, ${ruleCount}, ${columnCount})">
		<option value="">Please select field</option>	
		<%--
		<c:forEach items="${command.ruleUi.condition[0].domainObject[0].field}" varStatus="optionStatus">
			<option value="${command.ruleUi.condition[0].domainObject[0].field[optionStatus.index].name}">
				${command.ruleUi.condition[0].domainObject[0].field[optionStatus.index].displayUri}
			</option>
		</c:forEach>
		--%>
	</select>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].expression" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].expression" value="${command.ruleUi.condition[0].domainObject[0].field[0].expression}"/>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPrefix" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPrefix" value="${command.ruleUi.condition[0].domainObject[0].field[0].grammer.prefix}"/>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPostfix" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPostfix" value="${command.ruleUi.condition[0].domainObject[0].field[0].grammer.postfix}"/>
	
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].displayUri" name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].displayUri" value="${command.ruleUi.condition[0].domainObject[0].field[0].displayUri}"/>
	<input type="hidden" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].displayUri"  name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].displayUri" value="${command.ruleUi.condition[0].domainObject[0].field[0].operator[0].readableText}"/>
	

	<select id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].evaluator" 
				name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].evaluator"
				onchange="handleOperatorOnchange(this, ${ruleCount})">
		<option value="">Please select operator</option>
		<%--
		<c:forEach items="${command.ruleUi.condition[0].domainObject[0].field[0].operator}" varStatus="optionStatus">
			<option value="${command.ruleUi.condition[0].domainObject[0].field[0].operator[optionStatus.index].name}">
				${command.ruleUi.condition[0].domainObject[0].field[0].operator[optionStatus.index].displayUri}
			</option>
		</c:forEach>
		--%>
	</select>
	
	

	<span id="rule-${ruleCount}-column-${columnCount}-field-value">
	

<span id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value.span">	
	<select id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value" 
			name="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value" >
		<option value="">Please select value</option>		
		<%--
		<c:forEach items="${command.ruleUi.condition[0].domainObject[0].field[0].validValue}" varStatus="optionStatus">
			<option value="${command.ruleUi.condition[0].domainObject[0].field[0].validValue[optionStatus.index].value}">
				${command.ruleUi.condition[0].domainObject[0].field[0].validValue[optionStatus.index].displayUri}
			</option>
		</c:forEach>
		--%>
	</select>
</span>	
	
	</span>
	
	<a href="javascript:removeCondition(${ruleCount}, ${columnCount})">
		<img id="remove-column-${ruleCount}" src="<c:url value="/images/rule/remove_condition.gif"/>" align="absmiddle" style="cursor:hand;  border:0px"/>
	</a>
	
</div>
