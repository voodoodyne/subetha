<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeMass"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/list_subscribers.jsp">
			<c:param name="listId" value="${model.listId}" />
		</c:redirect>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/subscribe_mass.jsp" />
	</c:otherwise>				
</c:choose>
