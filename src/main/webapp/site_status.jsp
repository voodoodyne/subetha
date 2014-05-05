<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired"/>

<c:set var="siteStatus" value="${backend.admin.siteStatus}" />

<trim:main title="Site Status">
	<h1>Site Status</h1>

	<ul>
		<li>System Encoding (file.encoding):<strong> <c:out value="${siteStatus.defaultCharset}"/></strong></li>
		<li>Number of lists: <strong><c:out value="${siteStatus.listCount}"/></strong></li>
		<li>Number of accounts: <strong><c:out value="${siteStatus.personCount}"/></strong></li>
		<li>Nubmer of email messages archived: <strong><c:out value="${siteStatus.mailCount}"/></strong></li>
	</ul>
	
	<h3>Site Config</h3>
	<p>You can edit these in subetha.xml</p>
	<ul>
		<li>Postmaster email: <strong><c:out value="${siteStatus.postmasterEmail}"/></strong></li>
		<li>Default site url: <strong><c:out value="${siteStatus.defaultSiteUrl}"/></strong></li>
		<li>Fallback SMTP Host: <strong><c:out value="${siteStatus.fallbackHost}"/></strong></li>
	</ul>
	<form action="<c:url value="/rebuild_search_indexes.jsp"/>" method="post" class="form-inline">
		<input type="submit" name="submit" value="Rebuild Fulltext Search Indexes" /> (this might take a while, and users might not see full results during rebuild)
	</form>
</trim:main>