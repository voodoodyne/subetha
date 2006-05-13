<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.HeldMsgAction"/>

<c:redirect url="/held_msgs.jsp">
	<c:param name="listId" value="${model.listId}"/>
</c:redirect>
