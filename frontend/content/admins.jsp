<%@include file="/inc/top_standard.jspf"%>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="siteAdmins" value="${backend.admin.siteAdmins}"/>
<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:main title="Manage Site Administrators">
	<h1>Manage Site Administrators</h1>

	<p>Site Administrators have god'ish powers. Only give this power to people you trust.</p>

	<form action="<c:url value="/admins_remove_submit.jsp"/>" method="post">
		<input type="hidden" name="id" value="<c:out value="${person.id}" />" />
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
									<input type="submit" value="Remove" 
									onclick="return confirm('Are you sure you want to remove this administrator?');" />
								</c:if>
							</td>
						</tr>
				</c:forEach>
			</tbody>
		</table>
	</form>

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
				<td><input type="text" name="email" value="${model.email}" /></td>
				<td><input type="submit" value="Add" /></td>
			</tr>
		</table>

	</form>
</trim:main>
