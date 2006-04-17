<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />

<c:set var="listId" value="${sub.list.id}" />
<c:set var="perms" value="${f:wrapPerms(sub.perms)}" />

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>List Menu</div>

		<c:url var="listUrl" value="/list.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listUrl}">&rsaquo; List Overivew</a>
		
		<c:if test="${perms.READ_ARCHIVES}">
			<c:url var="listArchivesUrl" value="/archive.jsp">
				<c:param name="listId" value="${listId}"/>
			</c:url>
			<a href="${listArchivesUrl}">&rsaquo; Archives</a>
		</c:if>

		<c:if test="${perms.VIEW_SUBSCRIBERS}">
			<c:url var="listSubscribersUrl" value="/list_subscribers.jsp">
				<c:param name="listId" value="${listId}"/>
			</c:url>
			<a href="${listSubscribersUrl}">&rsaquo; Subscribers</a>
		</c:if>
	</div> <!-- sideBox LHS -->
	
	<c:if test="${perms.EDIT_SETTINGS || perms.EDIT_FILTERS || perms.EDIT_ROLES
				|| perms.APPROVE_SUBSCRIPTIONS || perms.APPROVE_MESSAGES}">
		<div class="sideBox LHS">
			<div>List Admin Menu</div>
			
			<c:if test="${perms.EDIT_SETTINGS}">
				<c:url var="listSettingsUrl" value="/list_settings.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listSettingsUrl}">&rsaquo; Settings</a>
			</c:if>
			
			<c:if test="${perms.EDIT_FILTERS}">
				<c:url var="listFiltersUrl" value="/list_filters.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listFiltersUrl}">&rsaquo; Filters</a>
			</c:if>
			
			<c:if test="${perms.EDIT_ROLES}">
				<c:url var="listRolesUrl" value="/list_roles.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listRolesUrl}">&rsaquo; Roles</a>
			</c:if>
			
			<c:if test="${perms.APPROVE_SUBSCRIPTIONS}">
				<c:url var="listHeldSubsUrl" value="/list_held_subs.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listHeldSubsUrl}">&rsaquo; Held Subscriptions</a>
			</c:if>
				
			<c:if test="${perms.APPROVE_MESSAGES}">
				<c:url var="listHeldMsgsUrl" value="/list_held_msgs.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listHeldMsgsUrl}">&rsaquo; Held Messages</a>
			</c:if>
		</div> <!-- sideBox LHS -->
	</c:if>
	
	<div id="bodyText">
		<h1><c:out value="${sub.list.name}"/> &lt;<c:out value="${sub.list.email}"/>&gt;</h1>
		<h2><c:out value="${title}"/></h2>
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:header>