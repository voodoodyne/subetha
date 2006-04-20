<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.RoleDelete"/>

<c:redirect url="/roles.jsp">
	<c:param name="listId" value="${model.listId}"/>
</c:redirect>
