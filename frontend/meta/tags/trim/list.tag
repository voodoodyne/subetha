<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="listId" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>List Menu</div>
		
		<c:url var="listUrl" value="list.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listUrl}">&rsaquo; List Overivew</a>
		
		<c:url var="listArchivesUrl" value="list_archives.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listArchivesUrl}">&rsaquo; Archives</a>
		
		<c:url var="listSubscribersUrl" value="list_subscribers.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listSubscribersUrl}">&rsaquo; Subscribers</a>
	</div> <!-- sideBox LHS -->
	
	<div class="sideBox LHS">
		<div>List Admin Menu</div>
		
		<c:url var="listSettingsUrl" value="list_settings.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listSettingsUrl}">&rsaquo; Settings</a>
		
		<c:url var="listFiltersUrl" value="list_filters.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listFiltersUrl}">&rsaquo; Filters</a>
		
		<c:url var="listRolesUrl" value="list_roles.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listRolesUrl}">&rsaquo; Roles</a>
		
		<c:url var="listHeldSubsUrl" value="list_held_subs.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listHeldSubsUrl}">&rsaquo; Held Subscriptions</a>
		
		<c:url var="listHeldMsgsUrl" value="list_held_msgs.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listHeldMsgsUrl}">&rsaquo; Held Messages</a>
	</div> <!-- sideBox LHS -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:header>