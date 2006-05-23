<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetRoleForEdit" />

<t:action var="list" type="org.subethamail.web.action.GetRoles">
	<t:param name="listId" value="${model.listId}"/>
</t:action>

<trim:list title="Edit Role: ${model.name}" listId="${model.listId}">

	<form action="<c:url value="/role_save.jsp"/>" method="post" class="form-inline">
		<fieldset> <legend><c:out value="${model.name}"/></legend>

		<input type="hidden" name="roleId" value="${model.roleId}" />
		
		<table>
			<tr>
				<th>Name</th>
				<td
					<c:if test="${!empty model.errors.name}">class="error"</c:if>
				>
					<input type="text" name="name" value="<c:out value="${model.name}"/>" />
					
					<c:if test="${!empty model.errors.name}">
						<p><c:out value="model.errors.name"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		
		<table class="permissions">
			<tr>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<th style="writing-mode: tb-rl">
						<label for="perm${perm}"><img src="<c:url value="/perm_img?perm=${perm}"/>" alt="<c:out value="${perm.pretty}"/>" /></label>
					</th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach var="perm" items="${backend.allPermissions}">
					<td>
						<input type="checkbox" name="permissions" value="${perm}" id="perm${perm}"
							<c:if test="${f:contains(model.realPermissions, perm)}">checked="checked"</c:if>
						/>
					</td>
				</c:forEach>
			</tr>
		</table>
		<input type="submit" value="Save" />
	</form>
	<form action="<c:url value="/roles.jsp"/>" method="post" class="form-inline">
		<input type="hidden" name="listId" value="${model.listId}" />
		<input type="submit" value="Cancel" />
	</form>
	</fieldset>

	<fieldset>
	<legend>Delete Role</legend>

		<form action="<c:url value="/role_delete.jsp"/>" method="post">
			<input type="hidden" name="deleteRoleId" value="${model.roleId}" />
			<p>
				<input type="submit" value="Delete" onclick="return confirm('Are you sure you want to delete this role?');" />
				this role, converting any participating subscribers to
				
				<select name="convertToRoleId">
					<c:forEach var="convertRole" items="${list.roles}">
						<c:if test="${convertRole.id != model.roleId}">
							<option value="<c:out value="${convertRole.id}"/>">
								<c:out value="${convertRole.name}"/>
							</option>
						</c:if>
					</c:forEach>
				</select>
			</p>
		</form>
	</fieldset>
</trim:list>