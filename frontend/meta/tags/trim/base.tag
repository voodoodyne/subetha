<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>${title}</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/bluehaze.css"/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/color-scheme.css"/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/sortabletable.jsp"/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/subetha.css"/>" />

		<script type="text/javascript" src="<c:url value="/js/sortabletable.jsp"/>"></script>
  	</head>
	
	<body>
		<jsp:doBody/>
		
		<!-- ###### Footer ###### -->
		
		<div id="footer">
			<div class="footerLHS">
				<a href="http://validator.w3.org/check/referer">Valid XHTML 1.0 Strict</a>
			</div> <!-- footerLHS -->
			
			<div class="footerLHS">
				<a href="http://jigsaw.w3.org/css-validator/check/referer">Valid CSS 2</a>
			</div> <!-- footerLHS -->
			
			<div>
				<a href="http://subetha.tigris.org/">SubEtha Mail</a>
				<a href="<c:url value="/version.jsp"/>"><c:out value="${applicationScope.backend.version}"/></a>
				is free software
			</div>
			
			<div>
				Powered by <a href="http://www.jboss.org/">JBoss</a>
			</div>
		</div> <!-- footer -->
	</body>
</html>
