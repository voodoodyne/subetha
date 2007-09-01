<%@include file="/inc/top_standard.jspf" %>

<t:action var="myList" type="org.subethamail.web.action.GetMyListRelationship" />
<c:set var="perms" value="${myList.perms}" />

<t:action var="model" type="org.subethamail.web.action.GetSubscribers">
	<t:param name="count" value="1000000000" />
</t:action>

<trim:plain title="Subscribers">
	<table>
	<thead>
		<tr>
			<td>Name</td>
			<td>Addresses</td>
			<c:if test="${perms.VIEW_ROLES}">
				<td>Role</td>
			</c:if>
			<c:if test="${perms.VIEW_NOTES}">
				<td>Note</td>
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
				<c:if test="${perms.VIEW_NOTES && !empty p.note}">
					<td>
						<div class="note">${f:escapeText(p.note)}</div>
					</td>
				</c:if>
			</tr>
		</c:forEach>
	</tbody>
	</table>
</trim:plain>