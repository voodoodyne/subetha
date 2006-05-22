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
				<td>Postmaster email</td>
				<td	<c:if test="${!empty model.errors.postmasterEmail}">
						class="error"
					</c:if>>
					<input type="text" size="40" name="postmasterEmail" value="<c:out value="${siteStatus.postmasterEmail}"/>" />
					<c:if test="${!empty model.errors.postmasterEmail}">
						<p class="error"><c:out value="${model.errors.postmasterEmail}" /></p>
					</c:if>
				</td>
				<td>The postmaster email address for the site.<br />("Your Name" &lt;postmater@yoursite.com&gt;)</td>
			</tr>
			<tr>
				<td>Default site url</td>
				<td	<c:if test="${!empty model.errors.defaultSiteUrl}">
						class="error"
					</c:if>>
					<input type="text" size="40" name="defaultSiteUrl" value="<c:out value="${siteStatus.defaultSiteUrl}"/>" />
					<c:if test="${!empty model.errors.defaultSiteUrl}">
						<p class="error"><c:out value="${model.errors.defaultSiteUrl}" /></p>
					</c:if>
				</td>
				<td>The global url for the site.<br />(http://hostname:port/se/)</td>
			</tr>
			</tbody>
		</table>
		<input type="submit" name="submit" value="Save" />
	</form>
	<form action="<c:url value="/site_status.jsp"/>" method="post" class="form-inline">
		<input type="submit" name="submit" value="Cancel" />
	</form>
</trim:main>