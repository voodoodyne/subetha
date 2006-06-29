<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<trim:main title="Change Password">

	<h1>Change Password</h1>

	<p>
		To change your password, enter (and confirm) the new one below.<br />
		For security reasons, your current password is not displayed.
	</p>
	
	<form action="<c:url value="/user_changepassword.jsp"/>" method="post">
		<table>
			<tr>
				<td><strong>Password</strong></td>
				<td
					<c:if test="${!empty model.errors.password}">
						class="error"
					</c:if>			
				>
					<input type="password" name="password" value="" />
					
					<c:if test="${!empty model.errors.password}">
						<p class="error"><c:out value="${model.errors.password}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><strong>Confirm</strong></td>
				<td><input type="password" name="confirm" value="" /></td>
			</tr>
			<tr>
				<td><input type="submit" value="Save" /></td>
				<td></td>
			</tr>
		</table>
	</form>	
</trim:main>
