<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<t:action var="data" type="org.subethamail.web.action.GetSubscribersDataForList" />
<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />
<c:set var="list" value="${sub.list}" />

<trim:admin title="List Subscribers">

	<h1>List Subscribers for ${list.name}</h1>
	
		<c:choose>
		<c:when test="${empty data}">
			<p>There are no subscribers to this list.</p>
		</c:when>
		<c:otherwise>
			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Addresses</td>
					<td>Role</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="p" items="${data}">
					<tr>
						<td>
							<c:out value="${p.name}" />
						</td>
						<td>
							<c:forEach var="e" items="${p.emailAddresses}">
								<a href="mailto:<c:out value="${e}" />"><c:out value="${e}" /></a>
							</c:forEach>
						</td>
						<td>
							<c:out value="${p.roleName}" />
						</td>
					</tr>
				</c:forEach>
			</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String"]);
</script>

		</c:otherwise>
	</c:choose>
	
	
</trim:admin>