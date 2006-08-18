<%@include file="../inc/taglibs.jspf" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="headerTitle" required="false" %>

<trim:base title="${title}">
	<!-- ###### Header ###### -->
	
	<div id="header">
		<div class="locBar">
			<c:choose>
				<c:when test="${auth.loggedIn}">
					You are <c:out value="${auth.authName}" />
					[<a href="<c:url value="/logout.jsp"/>">logout</a>]
				</c:when>
				
				<c:otherwise>
					<form action="<c:url value="/login_submit.jsp"/>" method="post" id="loginform">
						<input type="hidden" name="dest" value="${auth.usefulRequestURI}" />
						<table>
							<tr>
								<th>Email:</th>
								<td><input type="text" name="email"  /></td>
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
					<script type="text/javascript">
					<!--
					document.getElementById('loginform').email.focus();
					// -->
					</script>
				</c:otherwise>
			</c:choose>
		</div> <!-- locBar -->
		
		<span id="headerImg">
			<img src="<c:url value="/img/logo_mock_sm.jpg" />" alt="SubEtha Mail" />
		</span>
		
		<%-- Need to clean up the CSS for these parts --%>
		<c:if test="${!empty headerTitle}">
			<div id="headerText"><c:out value="${headerTitle}"/></div>
		</c:if>
	</div> <!-- header -->
	
	<jsp:doBody/>
	
</trim:base>
