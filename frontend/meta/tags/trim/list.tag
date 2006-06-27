<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="listId" required="true" type="java.lang.Long" %>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription">
	<t:param name="listId" value="${listId}"/>
</t:action>

<c:set var="perms" value="${f:wrapPerms(sub.perms)}" />

<trim:menus title="${title}" headerTitle="${sub.list.name} <${sub.list.email}>">
	<div class="sideBox LHS">
		<div>List Menu</div>

		<c:url var="listUrl" value="/list.jsp">
			<c:param name="listId" value="${listId}"/>
		</c:url>
		<a href="${listUrl}">&rsaquo; List Overview</a>
		
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
	
	<c:if test="${perms.EDIT_FILTERS || perms.EDIT_ROLES
				|| perms.APPROVE_SUBSCRIPTIONS || perms.APPROVE_MESSAGES}">
		<div class="sideBox LHS">
			<div>List Admin Menu</div>
			
			<c:if test="${perms.EDIT_ROLES}">
				<c:url var="listRolesUrl" value="/roles.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listRolesUrl}">&rsaquo; Roles/Permissions</a>
			</c:if>
			
			<c:if test="${perms.EDIT_FILTERS}">
				<c:url var="listFiltersUrl" value="/filters.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listFiltersUrl}">&rsaquo; Mail Filters</a>
			</c:if>
			
			<c:if test="${perms.APPROVE_SUBSCRIPTIONS}">
				<c:url var="listHeldSubsUrl" value="/held_subs.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listHeldSubsUrl}">&rsaquo; Held Subscriptions</a>
			</c:if>
				
			<c:if test="${perms.APPROVE_MESSAGES}">
				<c:url var="listHeldMsgsUrl" value="/held_msgs.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${listHeldMsgsUrl}">&rsaquo; Held Messages</a>
			</c:if>
				
			<c:if test="${perms.MASS_SUBSCRIBE}">
				<c:url var="massSubUrl" value="/subscribe_mass.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${massSubUrl}">&rsaquo; Mass Subscribe</a>
			</c:if>
			
			<c:if test="${perms.IMPORT_MESSAGES}">
				<c:url var="importMsgsUrl" value="/import_messages.jsp">
					<c:param name="listId" value="${listId}"/>
				</c:url>
				<a href="${importMsgsUrl}">&rsaquo; Import</a>
			</c:if>
			
		</div> <!-- sideBox LHS -->
	</c:if>
	
	<div id="bodyText">
		<h1><c:out value="${title}"/></h1>
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:menus>