<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.ListRemoveSubscriber"/>

<c:redirect url="/list_subscribers.jsp">
	<c:param name="listId" value="${model.listId}"/>
</c:redirect>