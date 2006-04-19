<%@include file="/inc/top_standard.jspf" %>

<t:action var="msg" type="org.subethamail.web.action.GetMessage" />

<trim:list title="${msg.subject}" listId="${msg.listId}">

	<p>
		From
		<span class="authorName"><c:out value="${msg.fromName}"/></span>
		
		<c:if test="${!empty msg.fromEmail}">
			<span class="authorEmail">
				&lt;<a href="mailto:<c:out value="${msg.fromEmail}"/>"><c:out value="${msg.fromEmail}"/></a>&gt;
			</span>
		</c:if>
		
		<span class="messageDate"><fmt:formatDate value="${msg.dateCreated}" type="both" timeStyle="short" /></span>
	</p>

	<div class="message">
		<c:forEach var="part" items="${msg.textParts}">
			<div class="messagePart">
				<c:out value="${part}"/>
			</div>
		</c:forEach>
	</div>

	<c:if test="${msg != msg.threadRoot || !empty msg.replies}">
		<h3>Thread History</h3>
		<div class="summaries">
			<ul>
				<li><se:summary msg="${msg.threadRoot}" highlight="${msg}"/></li>
			</ul>
		</div>
	</c:if>
</trim:list>