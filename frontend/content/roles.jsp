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
	
	<h3>Change Permissions</h3>
	
	<form action="" method="post">
		<table class="permissions">
			<tr>
				<th>Role&nbsp;Name</th>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<th style="writing-mode: tb-rl"><c:out value="${perm}"/></th>
				</c:forEach>
			</tr>
			<c:forEach var="role" items="${list.roles}">
				<tr>
					<td><c:out value="${role.name}"/></td>
					<c:forEach var="perm" items="${backend.allPermissions}">
						<td>
							<c:choose>
								<c:when test="${role.owner}">
									x
								</c:when>
								<c:otherwise>
									<input type="checkbox" name="" 
										<c:if test="${f:contains(role.permissions, perm)}">checked="checked"</c:if>
									/>
								</c:otherwise>
							</c:choose>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
		<input type="submit" value="Update Permissions" />
	</form>
	
	<h3>Add Role</h3>

	<form action="<c:url value="/role_add.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table class="permissions">
			<tr>
				<th>Role&nbsp;Name</th>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<th style="writing-mode: tb-rl"><c:out value="${perm}"/></th>
				</c:forEach>
			</tr>
			<tr>
				<td>
					<input name="name" value="${model.name}" type="text" />
				</td>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<td>
						<input type="checkbox" name="" 
							<c:if test="">checked="checked"</c:if>
						/>
					</td>
				</c:forEach>
			</tr>
		</table>
		<input type="submit" value="Add Role" />
	</form>

</trim:list>