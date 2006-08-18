<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<!-- ###### Header ###### -->
	
	<div id="header">
		<span id="headerImg">
			<img src="<c:url value="/img/logo_mock_sm.jpg" />" alt="SubEtha Mail" />
		</span>
		
		<div id="headerText">SubEtha Mail</div>
	</div> <!-- header -->
	
	<div id="bodyText">
		<jsp:doBody/>
	</div>
</trim:base>
