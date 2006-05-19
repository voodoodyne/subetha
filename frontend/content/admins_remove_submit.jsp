<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.AdminRemove"/>

<c:choose>
	<c:when test="${!empty model.errors}">
		<jsp:forward page="/admins.jsp"/>
	</c:when>
	<c:otherwise>
		<c:redirect url="/admins.jsp" />
	</c:otherwise>
</c:choose>
