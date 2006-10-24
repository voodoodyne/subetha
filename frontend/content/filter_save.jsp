<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<t:action var="model" type="org.subethamail.web.action.FilterSave"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/filters.jsp">
			<c:param name="listId" value="${model.listId}" />
		</c:redirect>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/filter_edit.jsp" />
	</c:otherwise>				
</c:choose>
