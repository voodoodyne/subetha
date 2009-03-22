<%@include file="/inc/top_standard.jspf"%>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="siteAdmins" value="${backend.admin.siteAdmins}"/>
<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:main title="Manage Site Administrators">
	<h1>Manage Site Administrators</h1>

	<p>Site Administrators can add, remove, and edit any mailing list.</p>

	<table class="sort-table" id="emails-table">
		<thead>
			<tr>
				<td>Name</td>
				<td>Addresses</td>
				<td>Action</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="person" items="${siteAdmins}" varStatus="loop">
					<tr>
						<td><c:out value="${person.name}" /></td>
						<td>
							<c:forEach var="e" items="${person.emailAddresses}"  varStatus="loop">
								<a href="mailto:<c:out value="${e}" />"><c:out value="${e}" /></a><c:if test="${! loop.last}">, </c:if>
							</c:forEach>
						</td>
						<td>
							<c:if test="${person.id != me.id}">
								<form action="<c:url value="/admins_remove_submit.jsp"/>" method="post">
									<input type="hidden" name="id" value="<c:out value="${person.id}" />" />
									<input type="submit" value="Remove" 
										onclick="return confirm('Are you sure you want to remove this administrator?');" />
								</form>
							</c:if>
						</td>
					</tr>
			</c:forEach>
		</tbody>
	</table>

	<form action="<c:url value="/admins_add_submit.jsp"/>" method="post">
		<h3>Add Site Administrator</h3>
		<p>
			Enter the email address of the person you want to add as a site administrator. This person
			must already have an account on the system.
		</p>

		<c:if test="${!empty model.errors.email}">
			<p class="error"><c:out value="${model.errors.email}" /></p>
		</c:if>

		<table>
			<tr>
				<td><input type="text" name="email" value="${model.email}" id="email" onkeyup="enableSingleField('email', 'addSubmit');" /></td>
				<td><input type="submit" value="Add" id="addSubmit" /></td>
			</tr>
		</table>

		<script type="text/javascript">
			document.getElementById('addSubmit').disabled=true;
			document.getElementById('email').focus();
		</script>
	</form>
</trim:main>
