<%@include file="/inc/top_standard.jspf" %>

<t:action var="holds" type="org.subethamail.web.action.GetHeldMessages" />

<trim:list title="Held Messages" listId="${param.listId}">
	<c:choose>
		<c:when test="${empty holds}">
			<p>There are no held messages to this list.</p>
		</c:when>
		
		<c:otherwise>
			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>Date</td>
					<td>Subject</td>
					<td>From</td>
					<td>Type</td>
					<td>Action</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="msg" items="${holds}" varStatus="loop">
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
							<fmt:formatDate value="${msg.date}" type="both" dateStyle="short" timeStyle="short"/>
						</td>
						<td>
							<c:url var="msgUrl" value="/archive_msg.jsp">
								<c:param name="msgId" value="${msg.id}"/>
							</c:url>
							<a href="${msgUrl}">
								<c:if test="${empty msg.subject}">(no subject)</c:if><c:out value="${msg.subject}"/>
							</a>
						</td>
						<td>
							<c:out value="${msg.from}"/>
						</td>
						<td>
							<c:choose>
								<c:when test="${msg.hard}">
									<div class="error">HARD</div>
								</c:when>
								<c:otherwise>
									SOFT
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<form action="held_msg_action.jsp" method="post" style="display:inline">
								<input type="hidden" name="msgId" value="${msg.id}" />
								<input type="submit" name="action" value="Approve" />
							</form>
							<form action="held_msg_action.jsp" method="post" style="display:inline">
								<input type="hidden" name="msgId" value="${msg.id}" />
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