<%--
  Copyright 2012 DTO Labs, Inc. (http://dtolabs.com)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
 --%>
<%--
   list.gsp

   Author: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
   Created: 7/25/12 5:22 PM
--%>


<%@ page import="org.yana.Project" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${g.message(code: 'project.label', default: 'Project')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>

<div id="list-node" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message.encodeAsHTML()}</div>
    </g:if>
    <g:if test="${message}">
        <div class="message" role="status">${message.encodeAsHTML()}</div>
    </g:if>
    <g:if test="${project}">
        <g:hasErrors bean="${project}">
            <div class="message">
            <g:renderErrors bean="${project}"/>
            </div>
        </g:hasErrors>
    </g:if>

    <section class="form">
        <g:form action="save">
            <fieldset class="form">
                <g:render template="form"/>
            </fieldset>
            <fieldset class="buttons">
                <g:actionSubmit action="cancel" value="Cancel" formnovalidate=""/>
                <g:submitButton name="save" value="Save"/>
            </fieldset>
        </g:form>
    </section>
</div>
</body>
</html>
