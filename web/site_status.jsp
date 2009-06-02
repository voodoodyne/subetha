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
	<ul>
		<li>Postmaster email: <strong><c:out value="${siteStatus.postmasterEmail}"/></strong></li>
		<li>Default site url: <strong><c:out value="${siteStatus.defaultSiteUrl}"/></strong></li>
		<li>Fallthrough SMTP Host: <strong><c:out value="${siteStatus.fallthroughHost}"/></strong></li>
	</ul>
	<form action="<c:url value="/site_status_edit.jsp"/>" method="get" class="form-inline">
		<input type="submit" name="submit" value="Edit" />
	</form>
</trim:main>