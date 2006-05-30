<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.UploadMBOX"/>

<c:choose>
	<c:when test="${not empty model.errors}">
		<c:redirect url="/import_messages.jsp">
			<c:param name="listId" value="${model.listId}" />
		</c:redirect>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/import_success.jsp" />
	</c:otherwise>		
</c:choose>
