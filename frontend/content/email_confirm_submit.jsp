<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.EmailAddConfirm"/>

<c:choose>
	<c:when test="${!empty model.errors}">
		<jsp:forward page="email_confirm.jsp"/>
	</c:when>
	<c:otherwise>
		<c:redirect url="home.jsp" />
	</c:otherwise>
</c:choose>
