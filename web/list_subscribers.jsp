<%@include file="/inc/top_standard.jspf" %>

<t:action var="myList" type="org.subethamail.web.action.GetMyListRelationship" />
<c:set var="perms" value="${myList.perms}" />

<t:action var="model" type="org.subethamail.web.action.GetSubscribers" />

<trim:list title="Subscribers" listId="${param.listId}">

	<form id="searchForm" action="<c:url value="/list_subscribers.jsp"/>" method="get" style="display:inline">
		<input type="hidden" name="listId" value="${param.listId}" />
		<input type="text" name="query" id="query" value="<c:out value="${param.query}" />"
			 onkeyup="submitForm(); enableSingleField('query', 'searchSubmit');"
			 />
		<input type="submit" value="Search" id="searchSubmit" />
		<script type="text/javascript">
			document.getElementById('searchSubmit').disabled=true;
			document.getElementById('query').focus();
			function submitForm() {
				if (document.getElementById('query').value == '') {
					document.getElementById('searchForm').submit();
				}
			}
		</script>
	</form>
	
	<br /><br />
	
	<c:choose>
		<c:when test="${empty model.subscribers && empty param.query}">
			<p>There are no subscribers to this list.</p>
		</c:when>
		<c:when test="${empty model.subscribers && ! empty param.query}">
			<p>Your query did not return any results.</p>
		</c:when>
		<c:otherwise>

			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Addresses</td>
					<c:if test="${perms.VIEW_ROLES}">
						<td>Role</td>
					</c:if>
					<c:if test="${perms.EDIT_SUBSCRIPTIONS || perms.EDIT_ROLES || perms.EDIT_NOTES}">
						<td>Action</td>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="p" items="${model.subscribers}" varStatus="loop">
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
						<c:if test="${perms.VIEW_ROLES}">
							<td>
								<c:out value="${p.roleName}" />
							</td>
						</c:if>
						<c:if test="${perms.EDIT_SUBSCRIPTIONS || perms.EDIT_ROLES || perms.EDIT_NOTES}">
							<td>
								<c:if test="${perms.EDIT_SUBSCRIPTIONS || perms.EDIT_ROLES || perms.EDIT_NOTES}">
									<form action="<c:url value="/subscription_edit.jsp"/>" method="get" style="display:inline">
										<input type="hidden" name="personId" value="${p.id}" />
										<input type="hidden" name="listId" value="${param.listId}" />
										<input type="submit" value="Edit" />
									</form>
								</c:if>
								<c:if test="${perms.EDIT_SUBSCRIPTIONS}">
									<form action="<c:url value="/person_unsubscribe.jsp"/>" method="post" style="display:inline">
										<input type="hidden" name="personId" value="${p.id}" />
										<input type="hidden" name="listId" value="${param.listId}" />
										<input type="submit" value="Unsubscribe" onclick="return confirm('Are you sure you want to unsubscribe this user?');"/>
									</form>
								</c:if>
							</td>
						</c:if>
					</tr>
					<c:if test="${perms.VIEW_NOTES && !empty p.note}">
						<tr class="${color}">
							<td colspan="4">
								<div class="note">
									NOTE: ${f:escapeText(p.note)}
								</div>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String"]);
st1.onsort = st1.tableRowColors;
</script>

			<c:url var="queryURL" value="/list_subscribers.jsp">
				<c:param name="listId" value="${model.listId}"/>
				<c:param name="query" value="${model.query}"/>
			</c:url>
			<se:searchPaginator url="${queryURL}&" model="${model}"/>
			
			<c:url var="showAllURL" value="/list_subscribers.jsp">
				<c:param name="listId" value="${model.listId}"/>
				<c:param name="query" value="${model.query}"/>
				<c:param name="count" value="1000000000"/>
			</c:url>
			<small><a href="${showAllURL}">show all</a></small>

			<c:url var="printableURL" value="/list_subscribers_print.jsp">
				<c:param name="listId" value="${model.listId}"/>
				<c:param name="query" value="${model.query}"/>
			</c:url>
			<small><a href="${printableURL}" target="printable">printable</a></small>
		</c:otherwise>
	</c:choose>
</trim:list>