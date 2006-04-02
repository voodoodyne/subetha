<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<!-- ###### Header ###### -->
	
	<div id="header">
		<span class="headerTitle">SubEtha Mail</span>
	</div> <!-- header -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:base>
