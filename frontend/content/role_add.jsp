<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.RoleAdd"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/roles.jsp">
			<c:param name="listId" value="${model.listId}"/>
		</c:redirect>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/role_add.jsp" />
	</c:otherwise>
</c:choose>
