<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>${title}</title>
		
		<link rel="stylesheet" type="text/css" href="css/bluehaze.css" />
		<link rel="stylesheet" type="text/css" href="css/color-scheme.css" />
		<link rel="stylesheet" type="text/css" href="css/subetha.css" />
  	</head>
	
	<body>
		<!-- ###### Header ###### -->
		
		<div id="header">
			<div class="locBar">
				<c:choose>
					<c:when test="${auth.loggedIn}">
						You are <c:out value="${auth.authName}" />
						[<a href="logout.jsp">logout</a>]
					</c:when>
					<c:otherwise>
						<form action="login_submit.jsp" method="post">
							<table>
								<tr>
									<th>Email:</th>
									<td><input type="text" name="email" /></td>
								</tr>
								<tr>
									<th>Password:</th>
									<td><input type="password" name="password" /></td>
								</tr>
								<tr>
									<th>Remember?</th>
									<td>
										<input type="checkbox" name="remember" />
										<input type="submit" value="Login" />
										<a href="pw_forgot.jsp">forgot?</a>
									</td>
								</tr>
							</table>
						</form>
					</c:otherwise>
				</c:choose>
			</div> <!-- locBar -->
			
			<span class="headerTitle">SubEtha Mail</span>
			
			<c:if test="${auth.loggedIn}">
				<div class="menuBar">
					<a href="home.jsp" class="first">Home</a>
					
					<c:if test="${auth.siteAdmin}">
						<a href="site_status.jsp">Site Administration</a>
					</c:if>
				</div> <!-- menuBar -->
			</c:if>
				
		</div> <!-- header -->
		
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
				<a href="http://subetha.tigris.org/">Sub-Etha Mail</a> is free software
			</div>
			
			<div>
				Powered by <a href="http://www.jboss.org/">JBoss</a>
			</div>
		</div> <!-- footer -->
	</body>
</html>
