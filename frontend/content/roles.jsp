<%@include file="/inc/top_standard.jspf" %>

<t:action var="list" type="org.subethamail.web.action.GetRoles" />

<trim:list title="Roles" listId="${param.listId}">

	<form action="<c:url value="/roles_set_default.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<p>
			The default role for new subscribers is
			<select name="defaultRole">
				<c:forEach var="role" items="${list.roles}">
					<option value="<c:out value="${role.id}"/>"
						<c:if test="${role.id == list.defaultRole.id}">selected="selected"</c:if>
					><c:out value="${role.name}"/></option>
				</c:forEach>
			</select><input type="submit" value="Set" />
		</p>
	</form>

	<form action="<c:url value="/roles_set_anon.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<p>
			The role for anonymous (not logged in) users is
			<select name="anonymousRole">
				<c:forEach var="role" items="${list.roles}">
					<option value="<c:out value="${role.id}"/>"
						<c:if test="${role.id == list.anonymousRole.id}">selected="selected"</c:if>
					><c:out value="${role.name}"/></option>
				</c:forEach>
			</select><input type="submit" value="Set" />
		</p>
	</form>
	
	<form action="" method="post">
		<table class="permissions">
			<tr>
				<th>Role Name</th>
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
	</form>

</trim:list>