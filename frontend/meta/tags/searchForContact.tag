<%@include file="inc/taglibs.jspf" %>

<%@attribute name="listId" required="true" %>
<%@attribute name="name" required="true" %>
<%@attribute name="email" required="false" %>

<a href="archive_search.jsp?listId=<c:out value="${listId}" />&query=<c:out value="${name}" />" 
   title="Search for <c:out value="${name}" />">[P]</a>