<%@include file="/inc/top_standard.jspf" %>

<trim:plain title="Not Allowed">

	<h1>Not Allowed</h1>
	
	<p>
		You do not have permission to perform this action.
	</p>
	
	<c:if test="${!auth.loggedIn}">
		<p>
			This probably means that you need to login first.  Use the form
			at the top of this page.  If you do not remember your password,
			click the 'forgot' link to have your password sent to you.
		</p>
	</c:if>
	
	<p class="error">
		Requires permission 
		<c:out value="${requestScope['javax.servlet.error.exception'].pretty}" />
	</p>
</trim:plain>
