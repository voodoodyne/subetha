<%@include file="/inc/top_standard.jspf" %>

<t:action var="data" type="org.subethamail.web.action.GetSubscribers" />

<trim:list title="Subscribers">

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
							<c:forEach var="e" items="${p.emailAddresses}"  varStatus="loop">
								<a href="mailto:<c:out value="${e}" />">
									<c:choose>
										<c:when test="${e == p.deliverTo}"><strong><c:out value="${e}" /></strong></c:when>
										<c:otherwise><c:out value="${e}" /></c:otherwise
									></c:choose
								></a><c:if test="${! loop.last}">, </c:if>
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
	
	
</trim:list>