<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeConfirm"/>

<c:choose>
	<c:when test="${model.badTokenError}">
		<jsp:forward page="subscribe_confirm.jsp"/>
	</c:when>
	<c:when test="${model.held}">
		<jsp:forward page="subscribe_held.jsp"/>
	</c:when>
	<c:otherwise>
		<c:redirect url="list.jsp">
			<c:param name="listId" value="${model.listId}" />
		</c:redirect>
	</c:otherwise>
</c:choose>
