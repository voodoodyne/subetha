<%@include file="inc/taglibs.jspf" %>

<%--
	This file only exists because JSP tag files cannot reference themselves; otherwise
	this would be part of summaries.tag.
--%>

<%@attribute name="msg" required="true" type="org.subethamail.core.lists.i.MailSummary" %>
<%@attribute name="highlight" required="false" type="org.subethamail.core.lists.i.MailSummary" %>
<%@attribute name="flat" required="false" type="java.lang.Boolean" %>

<se:summary msg="${msg}" highlight="${highlight}" />
<se:summaries msgs="${msg.replies}" highlight="${highlight}" flat="${flat}"/>