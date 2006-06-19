<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.ResendMessage"/>

<c:redirect url="archive_msg.jsp">
	<c:param name="msgId" value="${model.msgId}"/>
</c:redirect>>
