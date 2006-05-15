<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>Main Menu</div>
		<a href="<c:url value="/home.jsp"/>">&rsaquo; Home</a>
		<a href="<c:url value="/user_profile.jsp"/>">&rsaquo; My Profile</a>
		<a href="<c:url value="/change_password.jsp"/>">&rsaquo; Change Password</a>
		<c:if test="${auth.siteAdmin}">
			<a href="<c:url value="/site_status.jsp"/>">&rsaquo; Site Administration</a>
		</c:if>
	</div>

	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:header>