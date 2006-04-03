<%@include file="/inc/top_standard.jspf" %>

<c:url var="homeUrl" value="/home.jsp"/>

<t:action type="org.subethamail.web.action.auth.AuthRedirect">
	<t:param name="dest" value="${homeUrl}"/>
</t:action>

<trim:plain title="SubEtha Mail">
	<h1>Welcome</h1>
	<p>
		You must log in before you can do anything interesting.  To create
		an account, you must subscribe to a mailing list on this server.
	</p>
</trim:plain>