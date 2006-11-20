<%@include file="/inc/top_standard.jspf" %>

<trim:plain title="Not Allowed">

	<h1>Not Allowed</h1>
	
	<p>
		You do not have permission to perform this action.
	</p>
	
	<p class="error">
		<c:out value="${f:exceptionMessage(requestScope['javax.servlet.error.exception'])}"/>
	</p>

	<c:if test="${!auth.loggedIn}">
		<p>
			This probably means that you need to login first.  Use the form
			at the top of this page, or click <a href="<c:url value="/pw_forgot.jsp"/>">here</a>
			if you do not know your password.
		</p>
	</c:if>
	
</trim:plain>
