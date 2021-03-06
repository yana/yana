<%@ page import="org.yana.Project" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${g.message(code: 'project.label', default: 'Project')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<div id="edit-project" class="content scaffold-edit" role="main">
    <h1><g:message code="default.editAcls.label" args="[entityName,project.name]"/></h1>
    <g:if test="${flash.message|| request.message}">
        <div class="message" role="status">${flash.message?flash.message.encodeAsHTML(): request.message.encodeAsHTML()}</div>
    </g:if>
    <g:if test="${params.saved=='1'}">
        <div class="message" role="status">
            ${g.message(code: "projectController.savePermission.success.message", args: [params.permissionGrant, params.permission, params.recipient]).encodeAsHTML()}
        </div>
    </g:if>
    <g:if test="${params.deleted=='1'}">
        <div class="message" role="status">
            ${g.message(code: "projectController.deletePermission.success.message", default: 'Permission deleted').encodeAsHTML()}
        </div>
    </g:if>
    <g:if test="${request.error && request.errorCode}">
        <div class="message" role="status">
            ${g.message(code: request.errorCode, default: 'Error', args: [request.permissionGrant, request.permission, request.recipient]).encodeAsHTML()}
        </div>
    </g:if>

    <div>
      <g:render template="aclForm" />
    </div>
</div>
</body>
</html>
