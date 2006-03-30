<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<div class="sideBox LHS">
		<div>Admin Menu</div>
		<a href="site_status.jsp">&rsaquo; Site Status</a>
		<a href="list_create.jsp">&rsaquo; Create List</a>
		<a href="lists.jsp">&rsaquo; List Lists</a>
	</div> <!-- sideBox LHS -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:base>