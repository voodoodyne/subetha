<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.EmailAdd"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/email_confirm.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:forward page="/home.jsp" />
	</c:otherwise>
</c:choose>
