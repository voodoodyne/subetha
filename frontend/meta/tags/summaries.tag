<%@include file="inc/taglibs.jspf" %>

<%@attribute name="msgs" required="true" type="java.util.List" %>
<%@attribute name="highlight" required="false" type="org.subethamail.core.lists.i.MailSummary" %>

<ul>
	<c:forEach var="msg" items="${msgs}">
		<li>
			<se:summary msg="${msg}" highlight="${highlight}"/>
		</li>
	</c:forEach>
</ul>
