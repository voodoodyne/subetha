<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeMe"/>

<c:choose>
	<c:when test="${empty param.goto}">
		<c:redirect url="/home.jsp" />
	</c:when>
	<c:otherwise>
		<c:redirect url="${param.goto}" />
	</c:otherwise>
</c:choose>
