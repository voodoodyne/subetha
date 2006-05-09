<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.HeldSubAction"/>

<c:redirect url="/held_subs.jsp">
	<c:param name="listId" value="${model.listId}"/>
</c:redirect>
