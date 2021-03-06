
<%@ page import="org.yana.NodeTypeRelationship" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>

		<div id="show-nodeTypeRelationship" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			
		<table width="100%" border="0" cellspacing=0 cellpadding=0 valign=top>
			<tr>
				<td valign=top>
					<div style="clear: left;">

					<table class="scaffold" width="600" border="0" cellspacing=5>
						<tr style="background-color:#021faf;">
							<td style="padding:10px;">
							<img src="${resource(dir:'images/icons/64',file:'NodeTypeRelationship.png')}" alt="" style="padding: 0px 25px 0px 7px;vertical-align:middle;" align="left" />
							<span class="image-title">${nodeTypeRelationshipInstance.name}</span>
							<br clear=left>

<div style="padding-top:5px;">
	<label for="id">
		<b>ID:</b>
	</label>
	<g:fieldValue bean="${nodeTypeRelationshipInstance}" field="id"/>
</div>
							
							</td>
						</tr>
						<tr>
							<td>
							
							<table border="0" cellspacing=5>
								<tr>
									
									<td><b>Parent:</b></td>
									<td><img src="${resource(dir:path,file:nodeTypeRelationshipInstance?.parent.image)}" alt="" style="padding: 0px 25px 0px 7px;vertical-align:middle;" align="left" /></td>
									<td><g:link controller="nodeType" action="show" id="${nodeTypeRelationshipInstance?.parent?.id}" style="font: bold 13px verdana, arial, helvetica, sans-serif">${nodeTypeRelationshipInstance?.parent?.encodeAsHTML()}</g:link></td>									
								</tr>
								<tr>
									
									<td><b>Child:</b></td>
									<td><img src="${resource(dir:path,file:nodeTypeRelationshipInstance?.child.image)}" alt="" style="padding: 0px 25px 0px 7px;vertical-align:middle;" align="left" /></td>
									<td><g:link controller="nodeType" action="show" id="${nodeTypeRelationshipInstance?.child?.id}" style="font: bold 13px verdana, arial, helvetica, sans-serif">${nodeTypeRelationshipInstance?.child?.encodeAsHTML()}</g:link></td>
								</tr>
							</table>

							</td>
						</tr>
						<tr>
							<td>
								<g:form>
									<fieldset class="form_footer">
										<g:hiddenField name="id" value="${nodeTypeRelationshipInstance?.id}" />
										<!-- <span class="fake_button"><g:link action="edit" id="${nodeTypeRelationshipInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link></span>-->
										<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
									</fieldset>
								</g:form>
							</td>
						</tr>
					</table>
					</div>
				</td>
				<td valign=top width=225>&nbsp;</td>
			</tr>
		</table>
		</div>
	</body>
</html>
