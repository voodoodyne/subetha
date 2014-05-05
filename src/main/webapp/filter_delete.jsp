<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.FilterDelete"/>

<c:redirect url="/filters.jsp">
	<c:param name="listId" value="${param.listId}" />
</c:redirect>
