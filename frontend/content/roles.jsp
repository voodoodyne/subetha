<%@include file="/inc/top_standard.jspf" %>

<t:action var="list" type="org.subethamail.web.action.GetRoles" />

<trim:list title="Roles" listId="${param.listId}">

	<form action="<c:url value="/role_set_special.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<fieldset>
			<legend>New Subscribers</legend>
				The default role for new subscribers is:<br/>
				<select name="defaultRoleId">
					<c:forEach var="role" items="${list.roles}">
						<option value="<c:out value="${role.id}"/>"
							<c:if test="${role.id == list.defaultRole.id}">selected="selected"</c:if>
						><c:out value="${role.name}"/></option>
					</c:forEach>
				</select><input type="submit" value="Set" />
		</fieldset>
	</form>

	<form action="<c:url value="/role_set_special.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<fieldset>
			<legend>Anonymous</legend>
				The role for anonymous (not logged in) users is: <br/>
				<select name="anonymousRoleId">
					<c:forEach var="role" items="${list.roles}">
						<option value="<c:out value="${role.id}"/>"
							<c:if test="${role.id == list.anonymousRole.id}">selected="selected"</c:if>
						><c:out value="${role.name}"/></option>
					</c:forEach>
				</select><input type="submit" value="Set" />
		</fieldset>
	</form>
	<fieldset>	<legend>Current Role Permissions</legend>
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
									<c:url var="roleEditUrl" value="/role_edit.jsp">
										<c:param name="roleId" value="${role.id}" />
									</c:url>
									<a href="${roleEditUrl}">
										<c:out value="${role.name}"/>
									</a>
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
					</tr>
				</c:forEach>
			</table>
	</fieldset>	
	
	<form action="<c:url value="/role_save.jsp"/>" method="post">
		<fieldset> <legend>Add Role</legend>
	
			<input type="hidden" name="listId" value="${param.listId}" />
			<table class="permissions">
				<tr>
					<th>Name</th>
					<c:forEach var="perm" items="${backend.allPermissions}">
						<th style="writing-mode: tb-rl">
							<label for="perm${perm}"><img src="<c:url value="/perm_img?perm=${perm}"/>" alt="<c:out value="${perm.pretty}"/>" /></label>
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
							<input type="checkbox" name="permissions" value="${perm}" id="perm${perm}"
								<c:if test="${f:contains(model.realPermissions, perm)}">checked="checked"</c:if>
							/>
						</td>
					</c:forEach>
				</tr>
			</table>
			<input type="submit" value="Add Role" />
		</fieldset>
	</form>

	<fieldset>
		<legend>Permissions Key</legend>
		<table class="keyTable">
			<tr>
				<th>Permission</th>
				<th>Description</th>
			</tr>
		<c:forEach var="perm" items="${backend.allPermissions}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index % 2 == 0}">
					<c:set var="color" value="a"/>
				</c:when>
				<c:otherwise>
					<c:set var="color" value="b"/>
				</c:otherwise>
			</c:choose>
			<tr class="${color}">
				<td><c:out value="${perm.pretty}" /></td>
				<td><c:out value="${perm.description}" /></td>
			</tr>
		</c:forEach>
		</table>
	</fieldset>
</trim:list>