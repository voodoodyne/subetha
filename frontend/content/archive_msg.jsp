<%@include file="/inc/top_standard.jspf" %>

<t:action var="msg" type="org.subethamail.web.action.GetMessage" />


<trim:list title="${msg.fromName}" listId="${msg.listId}">
	
	<c:if test="${auth.loggedIn}">		
		<div class="sendTo">
			<form action="<c:url value="/archive_msg_resend.jsp"/>" method="post" style="display:inline">
				<span style="font-size:smaller">Send this message to: </span>
				
				<input type="hidden" name="msgId" value="${msg.id}" />
				<select name="email">
				<c:forEach var="email" items="${backend.accountMgr.self.emailAddresses}" varStatus="loop">
					<option value="<c:out value="${email}"/>"><c:out value="${email}"/></option>
				</c:forEach>
				</select>
				<input type="submit" value="Send" />
			</form>
		</div>
	</c:if>
	<div>
		<a href="<c:url value="/message/${msg.id}/${msg.subject}.eml"/>">View Full Message</a>
	</div>
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
		<c:forEach var="part" items="${msg.inlineParts}">
			<div class="messagePart">
				<c:out value="${part.contents}"/>
			</div><br/>
		</c:forEach>
	</div>

	<div class="attachments">
		<ul><li> Download Attachment:
				<ul>
				<c:forEach var="attachment" items="${msg.attachments}">
					<li>
					<a href="<c:url value="/attachment/${attachment.id}/${attachment.name}"/>">(<c:out value="${attachment.id}"/>) <c:out value="${attachment.name}" /></a>
					</li>
				</c:forEach>	
			</ul>
			</li>
		</ul>
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