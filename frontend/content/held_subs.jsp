<%@include file="/inc/top_standard.jspf" %>

<t:action var="holds" type="org.subethamail.web.action.GetHeldSubscriptions" />

<trim:list title="Held Subscriptions" listId="${param.listId}">
	<c:choose>
		<c:when test="${empty holds}">
			<p>There are no held subscriptions to this list.</p>
		</c:when>
		
		<c:otherwise>
			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Addresses</td>
					<td>Date</td>
					<td>Action</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="p" items="${holds}" varStatus="loop">
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
							<fmt:formatDate value="${p.dateSubscribed}" type="both" dateStyle="short" timeStyle="short" />
						</td>
						<td>
							<form action="held_sub_action.jsp" method="post" style="display:inline">
								<input type="hidden" name="listId" value="${param.listId}" />
								<input type="hidden" name="personId" value="${p.id}" />
								<input type="submit" name="action" value="Approve" />
							</form>
							<form action="held_sub_action.jsp" method="post" style="display:inline">
								<input type="hidden" name="listId" value="${param.listId}" />
								<input type="hidden" name="personId" value="${p.id}" />
								<input type="submit" name="action" value="Discard" />
							</form>
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
</trim:list>