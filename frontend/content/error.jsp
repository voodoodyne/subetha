<%@ page isErrorPage="true" %>
<%@ page import="org.subethamail.web.util.ExceptionUtils" %>

<% if (ExceptionUtils.causedBy(exception, org.subethamail.common.NotFoundException.class)) { %>

	<jsp:forward page="/error_notfound.jsp" />
	
<% } else if (ExceptionUtils.causedBy(exception, org.subethamail.entity.i.PermissionException.class)) { %>

	<jsp:forward page="/error_permission.jsp" />

<% } else { %>
	
	<jsp:forward page="/error_pretty.jsp">
		<jsp:param name="exception" value="${exception}" />
	</jsp:forward>
	
<% }%>

