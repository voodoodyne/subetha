<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.ForgotPassword"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/"/>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/pw_forgot.jsp" />
	</c:otherwise>				
</c:choose>
