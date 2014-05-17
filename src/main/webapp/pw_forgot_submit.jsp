<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.ForgotPassword"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<trim:plain title="Password Reminder">
			<h1>Password Reminder emailed</h1>
			<p>
				Your password has been emailed to you.
			</p>
		</trim:plain>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/pw_forgot.jsp" />
	</c:otherwise>				
</c:choose>
