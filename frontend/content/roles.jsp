<%@include file="/inc/top_standard.jspf" %>

<t:action var="list" type="org.subethamail.web.action.GetRoles" />

<trim:list title="Roles" listId="${param.listId}">

	<form action="<c:url value="/role_set_special.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<p>
			The default role for new subscribers is
			<select name="defaultRoleId">
				<c:forEach var="role" items="${list.roles}">
					<option value="<c:out value="${role.id}"/>"
						<c:if test="${role.id == list.defaultRole.id}">selected="selected"</c:if>
					><c:out value="${role.name}"/></option>
				</c:forEach>
			</select><input type="submit" value="Set" />
		</p>
	</form>

	<form action="<c:url value="/role_set_special.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<p>
			The role for anonymous (not logged in) users is
			<select name="anonymousRoleId">
				<c:forEach var="role" items="${list.roles}">
					<option value="<c:out value="${role.id}"/>"
						<c:if test="${role.id == list.anonymousRole.id}">selected="selected"</c:if>
					><c:out value="${role.name}"/></option>
				</c:forEach>
			</select><input type="submit" value="Set" />
		</p>
	</form>
	
	<table class="permissions">
		<tr>
			<th>Role</th>
			<c:forEach var="perm" items="${backend.allPermissions}">
				<th style="writing-mode: tb-rl">
					<img src="<c:url value="/perm_img?perm=${perm}"/>" alt="<c:out value="${perm.pretty}"/>" />
				</th>
			</c:forEach>
		</tr>
		<c:forEach var="role" items="${list.roles}">
			<tr>
				<th class="role">
					<c:choose>
						<c:when test="${role.owner}">
							<em><c:out value="${role.name}"/></em>
						</c:when>
						<c:otherwise>
							<c:out value="${role.name}"/>
						</c:otherwise>
					</c:choose>
				</th>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<td>
						<c:if test="${f:contains(role.permissions, perm)}">
							<img src="<c:url value="/img/check.gif"/>" alt="Yes" />
						</c:if>
					</td>
				</c:forEach>
				<th class="role">
					<c:if test="${!role.owner}">
						<form action="<c:url value="/role_edit.jsp"/>" method="get">
							<input type="hidden" name="roleId" value="${role.id}" />
							<input type="submit" value="Edit" />
						</form>
					</c:if>
				</th>
			</tr>
		</c:forEach>
	</table>
	
	<h3>Add Role</h3>

	<form action="<c:url value="/role_save.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table class="permissions">
			<tr>
				<th>Name</th>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<th style="writing-mode: tb-rl">
						<img src="<c:url value="/perm_img?perm=${perm}"/>" alt="<c:out value="${perm.pretty}"/>" />
					</th>
				</c:forEach>
			</tr>
			<tr>
				<th class="role <c:if test="${!empty model.errors.name}">error</c:if>">
					<input name="name" value="${model.name}" type="text" />
					
					<c:if test="${!empty model.errors.name}">
						<p class="error"><c:out value="${model.errors.name}"/></p>
					</c:if>
				</th>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<td>
						<input type="checkbox" name="permissions" value="${perm}"
							<c:if test="${f:contains(model.realPermissions, perm)}">checked="checked"</c:if>
						/>
					</td>
				</c:forEach>
			</tr>
		</table>
		<input type="submit" value="Add Role" />
	</form>

</trim:list>