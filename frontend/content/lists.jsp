<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="lists" value="${backend.admin.allLists}" />

<trim:admin title="All Lists">
	<h1>All Lists</h1>
	
	<table>
		<tr>
			<th>Name</th>
			<th>Address</th>
			<th>URL</th>
		</tr>
		<c:forEach var="list" items="${lists}">
			<tr>
				<td>
					<c:url var="listIdUrl" value="list.jsp">
						<c:param name="listId" value="${list.id}" />
					</c:url>
					<a href="${listIdUrl}"><c:out value="${list.name}" /></a>
				</td>
				<td>
					<a href="mailto:<c:out value="${list.email}"/>"><c:out value="${list.email}" /></a>
				</td>
				<td>
					<a href="<c:out value="${list.url}"/>"><c:out value="${list.url}" /></a>
				</td>
			</tr>
		</c:forEach>
	</table>

</trim:admin>