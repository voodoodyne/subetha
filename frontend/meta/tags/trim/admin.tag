<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:header title="${title}">
	<div class="sideBox LHS">
		<div>Site Admin Menu</div>
		<a href="<c:url value="/site_status.jsp"/>">&rsaquo; Site Status</a>
		<a href="<c:url value="/list_create.jsp"/>">&rsaquo; Create List</a>
		<a href="<c:url value="/lists.jsp"/>">&rsaquo; List Lists</a>
	</div> <!-- sideBox LHS -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:header>