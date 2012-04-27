
<%@ page import="com.dtosolutions.Filter" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'filter.label', default: 'Filter')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div id="show-filter" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list filter">
			
				<g:if test="${filterInstance?.dataType}">
				<li class="fieldcontain">
					<span id="dataType-label" class="property-label"><g:message code="filter.dataType.label" default="Data Type" /></span>
					
						<span class="property-value" aria-labelledby="dataType-label"><g:fieldValue bean="${filterInstance}" field="dataType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${filterInstance?.regex}">
				<li class="fieldcontain">
					<span id="regex-label" class="property-label"><g:message code="filter.regex.label" default="Regex" /></span>
					
						<span class="property-value" aria-labelledby="regex-label"><g:fieldValue bean="${filterInstance}" field="regex"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="form_footer">
					<g:hiddenField name="id" value="${filterInstance?.id}" />
					<span class="fake_button"><g:link action="edit" id="${filterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link></span>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
