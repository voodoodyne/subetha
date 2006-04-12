<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:home title="User Profile">

	<h1>User Profile</h1>

	<form action="<c:url value="/user_changename.jsp"/>" method="post">
	<table>
		<tr>
			<td><strong>Name</strong></td>
			<td><input type="text" name="name" value="<c:out value="${me.name}"/>"></td>
		</tr>
		<tr>
			<td><input type="submit" value="Save" /></td>
			<td>&nbsp;</td>
		</tr>
	</table>
	</form>
	
	<p>
		To change your password, enter (and confirm) the new one below.<br />
		For security reasons, your current password is not displayed.
	</p>
	
	<form action="<c:url value="/user_changepassword.jsp"/>" method="post">
	<table>
		<tr>
			<td><strong>Password</strong></td>
			<td><input type="password" name="password" value="" /></td>
		</tr>
		<tr>
			<td><strong>Confirm</strong></td>
			<td><input type="password" name="confirm" value="" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="Save" /></td>
			<td>&nbsp;</td>
		</tr>
	</table>
	</form>	
</trim:home>
