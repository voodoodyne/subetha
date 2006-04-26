<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:home title="User Profile">

	<h1>User Profile</h1>

	<form action="<c:url value="/user_changename.jsp"/>" method="post">
	<table>
		<tr>
			<td><strong>Name</strong></td>
			<td
				<c:if test="${!empty model.errors.name}">
					class="error"
				</c:if>			
			><input type="text" name="name" value="<c:out value="${me.name}"/>">
				<c:if test="${!empty model.errors.name}">
					<p class="error"><c:out value="${model.errors.name}"/></p>
				</c:if>
			</td>
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
	<c:if test="${!empty model.errors.password}">
		<p class="error"><c:out value="${model.errors.password}"/></p>
	</c:if>
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
