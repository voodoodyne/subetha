<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeMass"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<trim:list title="Mass Subscribe Results" listId="${param.listId}">
			<c:choose>
				<c:when test="${!empty model.addedEmails}">
					<p>Processed:</p>
					<ul>
					<c:forEach var="email" items="${model.addedEmails}">
						<li><c:out value="${email}" /></li>
					</c:forEach>
					</ul>
				</c:when>
				<c:otherwise>
					<p>Sorry, no emails were added to the list.</p>
				</c:otherwise>
			</c:choose>
		</trim:list>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/subscribe_mass.jsp" />
	</c:otherwise>				
</c:choose>
