<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SubscribeMe"/>

<c:redirect url="/list.jsp">
	<c:param name="listId" value="${param.listId}"/>
</c:redirect>