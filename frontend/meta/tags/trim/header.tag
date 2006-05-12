<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>

<trim:base title="${title}">
	<!-- ###### Header ###### -->
	<div id="header">
		<div class="locBar">
			<c:choose>
				<c:when test="${auth.loggedIn}">
					You are <c:out value="${auth.authName}" />
					[&nbsp;<a href="<c:url value="/logout.jsp"/>">logout</a>]
				</c:when>
				
				<c:otherwise>
					<div id="loginLink" style="padding-top:10px">
						<a id="loginAnchor" href="login_required.jsp" onclick="return showLoginDiv();">login</a>
					</div>
					<div id="loginDiv" style="display:none">
						<form action="<c:url value="/login_submit.jsp"/>" method="post" name="loginform">
							<input type="hidden" name="dest" value="${auth.usefulRequestURI}" />
							<table>
								<tr>
									<th>Email:</th>
									<td><input id="loginEmail" type="text" name="email"  /></td>
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
										<a href="<c:url value="/pw_forgot.jsp"/>">forgot?</a>
									</td>
								</tr>
							</table>
						</form>
					</div>
				</c:otherwise>
			</c:choose>
		</div> <!-- locBar -->
		
		<span class="headerTitle">SubEtha Mail</span>
		
		<c:if test="${auth.loggedIn}">
			<div class="menuBar">
				<a href="<c:url value="/home.jsp"/>" class="first">Home</a>
				
				<c:if test="${auth.siteAdmin}">
					<a href="<c:url value="/site_status.jsp"/>">Site Administration</a>
				</c:if>
			</div> <!-- menuBar -->
		</c:if>
			
	</div> <!-- header -->
	<script>
	<!--
		function showLoginDiv () {
		   	if (!document.getElementById) return true;
			var notloge = document.getElementById("loginLink");
		   	var logboxe = document.getElementById("loginDiv");
		   	var xcusere = document.getElementById("loginEmail");
		   	if (!notloge || !logboxe || ! xcusere) return true;
		   	notloge.style.display = 'none';
		   	logboxe.style.display = 'block';
		   	xcusere.focus();
		   	return false;
		}
		if (document.getElementById) {
			if {
				var loginAnchor = document.getElementById("loginAnchor");
				loginAnchor.focus();
			}
		}
	-->
	</script>
	
	<jsp:doBody/>
	
</trim:base>
