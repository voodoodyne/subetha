<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<!-- ###### Header ###### -->
	
	<div id="header">
		<span id="headerImg">
			<a href="http://subetha.tigris.org/"><img src="<c:url value="/img/logo_mock_sm.jpg" />" alt="SubEtha Mail" border="0" /></a>
		</span>
		
		<div id="headerText">SubEtha Mail</div>
	</div> <!-- header -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:base>
