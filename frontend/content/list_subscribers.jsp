<%@include file="/inc/top_standard.jspf" %>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />
<%-- TODO:  figure out how to not do this twice --%>
<c:set var="perms" value="${f:wrapPerms(sub.perms)}" />

<t:action var="model" type="org.subethamail.web.action.GetSubscribers" />

<trim:list title="Subscribers" listId="${param.listId}">

	<c:choose>
		<c:when test="${empty model.subscriberData}">
			<p>There are no subscribers to this list.</p>
		</c:when>
		<c:otherwise>
			<%--
			<form action="<c:url value="/list_subscribers.jsp"/>" method="get">
				<input type="hidden" name="listId" value="<c:out value="${param.listId}" />" />
				Filter: <input type="text" name="query" value="<c:out value="${param.query}" />" />
			</form>
			 --%>

			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Addresses</td>
					<td>Role</td>
					<c:if test="${perms.UNSUBSCRIBE_OTHERS || perms.EDIT_ROLES}">
						<td>Action</td>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="p" items="${model.subscriberData}" varStatus="loop">
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
							<c:out value="${p.roleName}" />
						</td>
						<c:if test="${perms.UNSUBSCRIBE_OTHERS || perms.EDIT_ROLES}">
							<td>
								<c:if test="${perms.UNSUBSCRIBE_OTHERS}">
									<form action="person_unsubscribe.jsp" method="post" style="display:inline">
										<input type="hidden" name="subId" value="${p.subId}" />
										<input type="submit" value="Unsubscribe" />
									</form>
								</c:if>
								<c:if test="${perms.EDIT_ROLES}">
									<form action="person_set_role.jsp" method="post" style="display:inline">
										<input type="hidden" name="subId" value="${p.subId}" />
										Put Role List Here
										<input type="submit" value="Set" />
									</form>
								</c:if>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String"]);
st1.onsort = st1.tableRowColors;
</script>

			<c:url var="queryURL" value="/list_subscribers.jsp">
				<c:param name="query" value="${model.query}"/>
			</c:url>
			<se:searchPaginator url="${queryURL}&" model="${model}"/>

		</c:otherwise>
	</c:choose>
</trim:list>