<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>Main Menu</div>
		<a href="<c:url value="/home.jsp"/>">&rsaquo; My Subscriptions</a>
		<a href="<c:url value="/user_profile.jsp"/>">&rsaquo; My Profile</a>
		<a href="<c:url value="/change_password.jsp"/>">&rsaquo; Change Password</a>
	</div>

	<c:if test="${auth.siteAdmin}">
		<div class="sideBox LHS">
			<div>Site Admin Menu</div>
			<a href="<c:url value="/site_status.jsp"/>">&rsaquo; Site Status</a>
			<a href="<c:url value="/list_create.jsp"/>">&rsaquo; Create List</a>
			<a href="<c:url value="/lists.jsp"/>">&rsaquo; List Lists</a>
			<a href="<c:url value="/site_administrators.jsp"/>">&rsaquo; Administrators</a>
		</div>
	</c:if>

	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:header>