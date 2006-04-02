<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="listId" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>List Menu</div>
		<a href="list.jsp?listId=${listId}">&rsaquo; List Overivew</a>
		<a href="list_archives.jsp?listId=${listId}">&rsaquo; Archives</a>
		<a href="list_subscribers.jsp?listId=${listId}">&rsaquo; Subscribers</a>
	</div> <!-- sideBox LHS -->
	
	<div class="sideBox LHS">
		<div>List Admin Menu</div>
		<a href="list_settings.jsp?listId=${listId}">&rsaquo; Settings</a>
		<a href="list_filters.jsp?listId=${listId}">&rsaquo; Filters</a>
		<a href="list_roles.jsp?listId=${listId}">&rsaquo; Roles</a>
		<a href="list_held_subs.jsp?listId=${listId}">&rsaquo; Held Subscriptions</a>
		<a href="list_held_msgs.jsp?listId=${listId}">&rsaquo; Held Messages</a>
	</div> <!-- sideBox LHS -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:header>