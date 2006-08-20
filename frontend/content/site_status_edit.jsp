<%@include file="/inc/top_standard.jspf" %>

<c:set var="siteStatus" value="${backend.admin.siteStatus}" />

<trim:main title="Site Status Edit">
	<h1>Site Status Edit</h1>
	
	<form action="<c:url value="/site_status_submit.jsp"/>" method="post" class="form-inline">
		<table class="sort-table">
			<thead>
				<tr>
					<td>Name</td>
					<td>Value</td>
					<td>Description</td>
				</tr>
			</thead>
			<tbody>
			<tr>
				<td>Postmaster Email</td>
				<td	<c:if test="${!empty model.errors.postmasterEmail}">
						class="error"
					</c:if>>
					<input type="text" size="40" name="postmasterEmail" value="<c:out value="${siteStatus.postmasterEmail}"/>" />
					<c:if test="${!empty model.errors.postmasterEmail}">
						<p class="error"><c:out value="${model.errors.postmasterEmail}" /></p>
					</c:if>
				</td>
				<td>
					<div>
						The postmaster email address for the site.  Used only as sender address
						for "forgot your password" and "confirm additional email address" emails
						to users.
					</div>
					<div><small>Example: "Your Name" &lt;postmater@yoursite.com&gt;</small></div>
				</td>
			</tr>
			<tr>
				<td>Default Site URL</td>
				<td	<c:if test="${!empty model.errors.defaultSiteUrl}">
						class="error"
					</c:if>>
					<input type="text" size="40" name="defaultSiteUrl" value="<c:out value="${siteStatus.defaultSiteUrl}"/>" />
					<c:if test="${!empty model.errors.defaultSiteUrl}">
						<p class="error"><c:out value="${model.errors.defaultSiteUrl}" /></p>
					</c:if>
				</td>
				<td>
					<div>
						The default url for the site, included in the text of
						"forgot your password" et al emails.
					</div>
					<div><small>Example: http://www.example.com/se/</small></td>
			</tr>
			</tbody>
		</table>
		<input type="submit" name="submit" value="Save" />
	</form>
	<form action="<c:url value="/site_status.jsp"/>" method="post" class="form-inline">
		<input type="submit" name="submit" value="Cancel" />
	</form>
</trim:main>