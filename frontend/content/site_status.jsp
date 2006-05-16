<%@include file="/inc/top_standard.jspf" %>

<t:action var="siteStatus" type="org.subethamail.web.action.SiteStatus" />

<trim:admin title="Site Status">
	<h1>Site Status</h1>

	<ul>
		<li>System Encoding (file.encoding):<strong> <c:out value="${siteStatus.systemEncoding}"/></strong></li>
		<li>Lists: <strong><c:out value="${fn:length(siteStatus.lists)}"/></strong></li>
		<li>Number of accounts</li>
		<li>Nubmer of email messages archived</li>
		<li>A list of all site administrators?</li>
		<li>Some information about the cluster which we can get
			from the JMX systems</li>	
	</ul>
	


</trim:admin>