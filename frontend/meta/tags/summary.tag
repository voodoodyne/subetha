<%@include file="inc/taglibs.jspf" %>

<%@attribute name="msg" required="true" type="org.subethamail.core.lists.i.MailSummary" %>
<%@attribute name="highlight" required="false" type="org.subethamail.core.lists.i.MailSummary" %>

<c:choose>
	<c:when test="${msg == highlight}">
		<strong>
			<c:choose
				><c:when test="${empty msg.subject}">(no subject)</c:when
				><c:otherwise><c:out value="${msg.subject}"/></c:otherwise
			></c:choose>
		</strong>
	</c:when>
	<c:otherwise>
		<span class="subject">
			<c:url var="msgUrl" value="/archive_msg.jsp">
				<c:param name="msgId" value="${msg.id}"/>
			</c:url>
			<a href="${msgUrl}"><c:choose
				><c:when test="${empty msg.subject}">(no subject)</c:when
				><c:otherwise><c:out value="${msg.subject}"/></c:otherwise
			></c:choose></a>
		</span>
	</c:otherwise>
</c:choose>
<span class="authorName"><c:out value="${msg.fromName}"/></span>

<c:if test="${!empty msg.fromEmail}">
	<span class="authorEmail">
		&lt;<a href="mailto:<c:out value="${msg.fromEmail}"/>"><c:out value="${msg.fromEmail}"/></a>&gt;
	</span>
</c:if>

<span class="messageDate"><fmt:formatDate value="${msg.dateCreated}" type="both" timeStyle="short" /></span>

<c:if test="${!empty msg.replies}">
	<se:summaries msgs="${msg.replies}" highlight="${highlight}"/>
</c:if>
