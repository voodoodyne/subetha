<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRedirect"/>

<trim:plain title="Password Reminder">
	<h1>Forgot Your Password?</h1>
	<p>
		Fortunately for you, we can send your password back to you.
	</p>
	
	<%@include file="/inc/forgot_password_form.jspf" %>
</trim:plain>