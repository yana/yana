<div class="topbar">
<table border=0 cellspacing=0 cellpadding=0 width='100%' valign=top>
	<tr>
		<td align=left class="logo">&nbsp;</td>
		<td>
            <g:if test="${session.project}">
                <g:link action="list" controller="project">Project</g:link>: ${session.project.encodeAsHTML()}
            </g:if>
            <dto:breadcrumbs>${it}</dto:breadcrumbs>
        </td>
		<td class="header_search">
			<div>
			  <g:form url='[controller: "search", action: "index"]' id="searchableForm" name="searchableForm" method="get">
			      <g:textField class="search_input" name="q" value="${params.q}" size="25"/> <input class="button" type="submit" value="Search" />
			  </g:form>
			  <div style="clear: both; display: none;" class="hint">See <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for advanced queries</div>
			</div>
		</td>
        <td style="width: 25%; text-align: right;">
            <sec:ifLoggedIn>
                Welcome back, <sec:username/> | <a
                    href="${createLink(controller: 'logout', action: 'index')}">Logout</a>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <a href="${createLink(controller: 'login', action: 'auth')}">Login</a>
            </sec:ifNotLoggedIn>
        </td>
	</tr>
</table></div>
<div class="topmenu">
			<ul class="sf-menu">
				<li class="current"><g:link controller="node" action="list">Nodes</g:link>
					<ul>
						<li><g:link controller="node" action="list">List</g:link></li>
						<li><g:link controller="node" action="create">Create</g:link></li>
						<!--
						<li class="current"><g:link controller="childNode" action="list">Node Relationships</g:link>
							<ul>
								<li class="current"><g:link controller="childNode" action="create">Create Node Relationship</g:link></li>
							</ul>
						</li>
						-->
					</ul>

				</li>
				<li class="spacer"><img src="<g:createLinkTo dir='images' file='pix.png'/>" width='10' height='24'/></li>
				<li><g:link controller="nodeType" action="list">Types</g:link>
					<ul>
						<li><g:link controller="nodeType" action="list">NodeTypes</g:link>
						<!--
							<ul>
								<li><g:link controller="nodeType" action="create">Create NodeType</g:link></li>
							</ul>
					  	 -->
						</li>
						<li><g:link controller="attribute" action="list">Attributes</g:link>
						<!--
							<ul>
								<li><g:link controller="attribute" action="create">Create Attribute</g:link></li>
							</ul>
						  -->
						</li>
						<li><g:link controller="filter" action="list">Filters</g:link>
						<!--
							<ul>
								<li><g:link controller="filter" action="create">Create Filter</g:link></li>
							</ul>
						  -->
						</li>
						<li><g:link controller="nodeTypeRelationship" action="list">Nodetype Relationship</g:link>
						<!--
							<ul>
								<li><g:link controller="nodeTypeRelationship" action="create">Create Nodetype Relationship</g:link></li>
							</ul>
						 -->
						</li>
					</ul>
				</li>
				<!--
				<li class="spacer"><img src="<g:createLinkTo dir='images' file='pix.png'/>" width='10' height='24'/></li>
				<li><g:link controller="webhook" action="list">Webhooks</g:link>
					<ul>
						<li><g:link controller="webhook" action="list">List</g:link></li>
						<li><g:link controller="webhook" action="create">Create</g:link></li>
					</ul>
				</li>
				-->
				<li class="spacer"><img src="<g:createLinkTo dir='images' file='pix.png'/>" width='10' height='24'/></li>
				<li><a href="${grailsApplication.config.grails.serverURL}/import">Admin</a>
					<ul>
						<li><g:link controller="import" action="importxml">Import Model</g:link></li>
                        <li><g:link controller="export" action="xml">Export Model</g:link></li>

                        <li><g:link controller="user" action="search">Users</g:link>
							<ul>
								<li><g:link controller="user" action="create">Create User</g:link></li>
							</ul>
						</li>
						<li><g:link controller="role" action="search">Roles</g:link>
							<ul>
								<li><g:link controller="role" action="create">Create Role</g:link></li>
							</ul>
						</li>
					</ul>
				</li>
			</ul>

</div>
