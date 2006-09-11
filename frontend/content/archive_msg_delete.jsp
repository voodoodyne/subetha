<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.DeleteMessage"/>

<c:redirect url="/archive.jsp">
	<c:param name="listId" value="${model.listId}"/>
</c:redirect>
