<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetListSettings" />

<trim:list title="Settings" listId="${param.listId}">

	<form action="<c:url value="/list_settings_submit.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table>
			<tr>
				<th><label for="name">Short Name</label></th>
				<td
					<c:if test="${!empty model.errors.name}">
						class="error"
					</c:if>
				>
					<input id="name" name="name" type="text" size="60" value="${model.name}" />
					
					<c:if test="${!empty model.errors.name}">
						<p class="error"><c:out value="${model.errors.name}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="description">Description</label></th>
				<td
					<c:if test="${!empty model.errors.description}">
						class="error"
					</c:if>
				>
					<textarea id="description" name="description" rows="5" cols="60" style="width:95%"
					><c:out value="${model.description}"/></textarea>
					
					<c:if test="${!empty model.errors.description}">
						<p class="error"><c:out value="${model.errors.description}"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		<input type="submit" value="Save" />
	</form>

</trim:list>