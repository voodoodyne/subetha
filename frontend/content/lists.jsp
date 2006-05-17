<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="lists" value="${backend.admin.allLists}" />

<trim:main title="All Lists">
	<h1>All Lists</h1>
	<c:choose>
		<c:when test="${empty lists}">
			<p>No lists have been created. You can <a href="<c:url value="/list_create.jsp"/>">create a list</a>.</p>
		</c:when>
		<c:otherwise>
			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Address</td>
					<td>URL</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="list" items="${lists}" varStatus="loop">
					<c:choose>
						<c:when test="${loop.index % 2 == 0}">
							<c:set var="color" value="a"/>
						</c:when>
						<c:otherwise>
							<c:set var="color" value="b"/>
						</c:otherwise>
					</c:choose>
					<tr class="${color}">
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
			</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String"]);
st1.onsort = st1.tableRowColors;
</script>

		</c:otherwise>
	</c:choose>
</trim:main>