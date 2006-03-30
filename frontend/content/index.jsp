<%@include file="inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRedirect">
	<t:param name="dest" value="home.jsp"/>
</t:action>

<trim:plain title="Sub-Etha Mail">
	<h1>Welcome</h1>
	<p>
		You must log in before you can do anything interesting.  To create
		an account, you must subscribe to a mailing list on this server.
	</p>
</trim:plain>