<%@include file="/inc/top_standard.jspf" %>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />
<%-- TODO:  figure out how to not do this twice --%>
<c:set var="perms" value="${f:wrapPerms(sub.perms)}" />

<t:action var="model" type="org.subethamail.web.action.GetSubscribers" />

<c:if test="${perms.EDIT_ROLES}">
	<t:action var="listRoles" type="org.subethamail.web.action.GetRoles">
		<t:param name="listId" value="${param.listId}"/>
	</t:action>
</c:if>

<trim:list title="Subscribers" listId="${param.listId}">

	<c:choose>
		<c:when test="${empty model.subscriberData}">
			<p>There are no subscribers to this list.</p>
		</c:when>
		<c:otherwise>

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
									<form action="<c:url value="/person_unsubscribe.jsp"/>" method="post" style="display:inline">
										<input type="hidden" name="personId" value="${p.id}" />
										<input type="hidden" name="listId" value="${param.listId}" />
										<input type="submit" value="Unsubscribe" onclick="return confirm('Are you sure you want to unsubscribe this user?');"/>
									</form>
								</c:if>
								<c:if test="${perms.EDIT_ROLES}">
									<form action="<c:url value="/person_set_role.jsp"/>" method="post" style="display:inline">
										<input type="hidden" name="personId" value="${p.id}" />
	 									<input type="hidden" name="listId" value="${param.listId}" />
										<select name="roleId">
											<c:forEach var="role" items="${listRoles.roles}" varStatus="loop">
												<c:if test="${role.name != sub.roleName}">
													<option value="${role.id}"><c:out value="${role.name}"/></option>
												</c:if>
											</c:forEach>
										</select>
										<input type="submit" value="Change" />
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