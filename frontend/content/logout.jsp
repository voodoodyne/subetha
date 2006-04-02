<%@include file="inc/taglibs.jspf" %>
<%-- Does NOT include the standard top, which would trigger an autologin --%>

<t:action var="logout" type="org.subethamail.web.action.auth.Logout"/>

<trim:headerless title="Bye!">

	<h1>Bye!</h1>
	
	<p>
		You are now logged out.  If you would like to log in again,
		<a href="index.jsp">click here</a>.
	</p>
	
	<c:if test="${logout.autoLoginEnabled}">
		<h2>Auto-Login Enabled</h2>
		
		<form action="stop_auto_login.jsp" method="get">
			<p>
				You have enabled auto-login by checking the "remember me" box.  If
				you would like to disable this behavior, click
				<input type="submit" value="Stop Auto-Login">
			</p>
		</form>
	</c:if>
</trim:headerless>
