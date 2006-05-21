<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired"/>

<c:set var="siteStatus" value="${backend.admin.siteStatus}" />

<trim:main title="Site Status">
	<h1>Site Status</h1>

	<ul>
		<li>System Encoding (file.encoding):<strong> <c:out value="${siteStatus.defaultCharset}"/></strong></li>
		<li>Number of lists: <strong><c:out value="${siteStatus.listCount}"/></strong></li>
		<li>Number of accounts</li>
		<li>Nubmer of email messages archived</li>
		<li>Some information about the cluster which we can get
			from the JMX systems</li>	
	</ul>
</trim:main>