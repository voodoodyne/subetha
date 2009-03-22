<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.RoleSave"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/roles.jsp">
			<c:param name="listId" value="${model.listId}"/>
		</c:redirect>
	</c:when>
	<c:when test="${!empty model.roleId}">	<%-- this was an edit --%>
		<jsp:forward page="/role_edit.jsp" />
	</c:when>
	<c:otherwise>	<%-- this was an add --%>
		<jsp:forward page="/roles.jsp" />
	</c:otherwise>
</c:choose>
