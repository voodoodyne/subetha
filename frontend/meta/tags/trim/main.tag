<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:menus title="${title}">
	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:menus>