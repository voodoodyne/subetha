<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:choose>
	<c:when test="${empty model.errors.name}">	<%-- First time visiting, or possibly a pw set error --%>
		<c:set var="myName" value="${backend.accountMgr.self.name}"/>
	</c:when>
	<c:otherwise>
		<c:set var="myName" value="${model.name}"/>
	</c:otherwise>
</c:choose>

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
				>
					<input type="text" name="name" value="<c:out value="${myName}"/>">
					
					<c:if test="${!empty model.errors.name}">
						<p class="error"><c:out value="${model.errors.name}"/></p>
					</c:if>
				</td>
				<td><input type="submit" value="Save" /></td>
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
				<td></td>
			</tr>
			<tr>
				<td><strong>Confirm</strong></td>
				<td><input type="password" name="confirm" value="" /></td>
				<td><input type="submit" value="Save" /></td>
			</tr>
		</table>
	</form>	
</trim:home>
