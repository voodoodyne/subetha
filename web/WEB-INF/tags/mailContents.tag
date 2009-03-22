<%@include file="inc/taglibs.jspf" %>

<%@attribute name="msg" required="true" type="org.subethamail.core.lists.i.MailData" %>

<c:if test="${!empty msg}">
	<p>
		From
		<span class="authorName"><c:out value="${msg.fromName}"/></span>
		
		<c:if test="${!empty msg.fromEmail}">
			<span class="authorEmail">
				&lt;<a href="mailto:<c:out value="${msg.fromEmail}"/>"><c:out value="${msg.fromEmail}"/></a>&gt;
			</span>
		</c:if>
		
		<span class="messageDate"><fmt:formatDate value="${msg.sentDate}" type="both" timeStyle="short" /></span>
	</p>

	<div class="message">
		<c:forEach var="part" items="${msg.textParts}">
			<div class="messagePart">
				<pre class="message" ><c:out value="${part.contents}"/></pre>
			</div>
		</c:forEach>
	</div>

	<c:if test="${!empty msg.attachments}">
		<div class="attachments">
			<h3>Attachments</h3>
			<ul>
				<c:forEach var="attachment" items="${msg.attachments}">
					<li>
						<a href="<c:url value="/attachment/${attachment.id}/${attachment.name}"/>"><c:out value="${attachment.name}" /></a>
					</li>
				</c:forEach>	
			</ul>
		</div>
	</c:if>
</c:if>