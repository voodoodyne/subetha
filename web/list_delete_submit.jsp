<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.DeleteList"/>

<c:choose>
	<c:when test="${!model.wrongPassword}">
		<c:redirect url="/lists.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/list_delete.jsp" />
	</c:otherwise>				
</c:choose>
