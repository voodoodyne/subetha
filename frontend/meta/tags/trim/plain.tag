<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<div id="bodyText">
		<jsp:doBody/>
	</div> <!-- bodyText -->
</trim:base>