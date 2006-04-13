<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeAnon"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/subscribe_confirm.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:forward page="/list.jsp" />
	</c:otherwise>
</c:choose>
