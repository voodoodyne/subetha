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
					<input type="password" name="password" id="password" 
						onkeyup="enableChanged('submit', 'password', passwordOrigValue); checkSame('password', 'confirm', 'submit');" value="" />
					
					<c:if test="${!empty model.errors.password}">
						<p class="error"><c:out value="${model.errors.password}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><strong>Confirm</strong></td>
				<td><input type="password" name="confirm" id="confirm" 
					onkeyup="enableChanged('submit', 'confirm', confirmOrigValue); checkSame('password', 'confirm', 'submit');" value="" /></td>
			</tr>
			<tr>
				<td><input type="submit" value="Save" id="submit" /></td>
				<td></td>
			</tr>
		</table>
		<script type="text/javascript">
			var passwordOrigValue = document.getElementById('password').value;
			var confirmOrigValue = document.getElementById('confirm').value;
			document.getElementById('submit').disabled=true;
		</script>

	</form>	
</trim:main>
