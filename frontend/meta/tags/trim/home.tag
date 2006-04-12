<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>Settings</div>
		<a href="<c:url value="/home.jsp"/>">&rsaquo; Home</a>
		<a href="<c:url value="/user_profile.jsp"/>">&rsaquo; User Profile</a>
	</div>

	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:header>