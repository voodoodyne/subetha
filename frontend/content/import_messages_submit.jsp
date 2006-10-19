<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.UploadMBOX"/>

<c:choose>
	<c:when test="${! empty model.errors}">
		<c:redirect url="/import_messages.jsp">
			<c:param name="listId" value="${param.listId}" />
		</c:redirect>
	</c:when>
	<c:otherwise>
		<trim:list title="Messages Imported" listId="${param.listId}">
			<p>${model.countImported} messages imported</p>
		</trim:list>
	</c:otherwise>		
</c:choose>
