<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:header title="${title}">
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:header>