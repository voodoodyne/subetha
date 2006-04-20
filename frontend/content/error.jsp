<%@ page isErrorPage="true" %>
<%@ page import="org.subethamail.web.util.ExceptionUtils" %>

<% if (ExceptionUtils.causedBy(exception, org.subethamail.common.NotFoundException.class)) { %>

	<jsp:forward page="/error_notfound.jsp" />
	
<% } else if (ExceptionUtils.causedBy(exception, org.subethamail.core.lists.i.PermissionException.class)) { %>

	<jsp:forward page="/error_permission.jsp" />
	
<% } else { throw exception; } %>

