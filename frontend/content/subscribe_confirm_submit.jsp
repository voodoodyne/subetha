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
		<jsp:forward page="subscribe_ok.jsp"/>
	</c:otherwise>
</c:choose>
