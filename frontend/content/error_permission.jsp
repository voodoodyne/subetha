<%@include file="/inc/top_standard.jspf" %>

<trim:plain title="Not Allowed">

	<h1>Not Allowed</h1>
	
	<p>
		You do not have permission to perform this action.
	</p>
	
	<c:if test="${!auth.loggedIn}">
		<p>
			You might if you were logged in.  Use the form above.
		</p>
	</c:if>
	
	<p class="error">
		<c:out value="${f:exceptionMessage(requestScope['javax.servlet.error.exception'])}"/>
	</p>
</trim:plain>
