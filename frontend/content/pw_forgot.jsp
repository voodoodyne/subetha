<%@include file="/inc/top_standard.jspf" %>

<trim:plain title="Password Reminder">
	<h1>Forgot Your Password?</h1>
	<p>
		Fortunately for you, we can send your password back to you.
	</p>
	
	<form action="<c:url value="/pw_forgot_submit.jsp"/>" method="post">
		<table>
			<tr>
				<th>Email Address:</th>
				<td
					<c:if test="${!empty model.errors.email}">
						class="error"
					</c:if>
				>
					<input type="text" name="email" value="<c:out value="${model.email}"/>" />
					
					<c:if test="${!empty model.errors.email}">
						<p class="error"><c:out value="${model.errors.email}"/></p>
					</c:if>
				</td>
				<td>
					<input type="submit" value="Send"/>
				</td>
			</tr>
		</table>
	</form>
</trim:plain>