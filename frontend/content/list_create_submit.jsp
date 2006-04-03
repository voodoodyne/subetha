<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.CreateList"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/list.jsp">
			<c:param name="listId" value="${model.id}" />
		</c:redirect>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/list_create.jsp" />
	</c:otherwise>				
</c:choose>
