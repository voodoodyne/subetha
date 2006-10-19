<%@include file="/inc/top_standard.jspf" %>

<t:action var="msg" type="org.subethamail.web.action.PrepareReply" />

<c:choose>
	<c:when test="${empty msg.msgId}">
		<c:set var="type" value="Compose"/>
		<c:set var="title" value="Compose Message" />
	</c:when>
	<c:otherwise>
		<c:set var="type" value="Reply"/>
		<c:set var="title" value="${msg.subject}" />
	</c:otherwise>
</c:choose>

<trim:list title="${title}" listId="${msg.listId}">

	<fieldset><legend><c:out value="${type}" /></legend>
		<form action="<c:url value="/msg_send_submit.jsp"/>" method="post" class="form-inline">
			<c:choose>
				<c:when test="${!empty msg.msgId}">
					<input type="hidden" name="msgId" value="${msg.msgId}" />
				</c:when>
				<c:otherwise>
					<input type="hidden" name="listId" value="${msg.listId}" />
				</c:otherwise>
			</c:choose>

			<table>
				<tr>
					<th>Subject:</th>
					<td>
						<input type="text" id="formSubject" name="subject" size="80" 
							value="<c:out value="${msg.subject}" />"
							onkeyup="enableButton();" />
						<c:if test="${!empty model.errors.subject}">
							<p class="error"><c:out value="${model.errors.subject}" /></p>
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Message:</th>
					<td>
						<textarea rows="20" cols="80" name="message" id="formMessage" onkeyup="enableButton();"><c:if test="${!empty model.message}"><c:out value="${model.message}" /></c:if></textarea>
						<c:if test="${!empty model.errors.message}">
							<p class="error"><c:out value="${model.errors.message}" /></p>
						</c:if>
					</td>
				</tr>
				<tr>
				</tr>
			</table>
			<input type="submit" id="buttonSubmit" value="<c:out value="${type}"/>" />
		</form>

		<c:choose>
			<c:when test="${!empty msg.msgId}">
			<form action="<c:url value="/archive_msg.jsp"/>" method="get" class="form-inline">
				<input type="hidden" name="msgId" value="${msg.msgId}" />
				<input type="submit" value="Cancel" />
			</form>
			</c:when>
			<c:otherwise>
				<form action="<c:url value="/archive.jsp"/>" method="get" class="form-inline">
					<input type="hidden" name="listId" value="${msg.listId}" />
					<input type="submit" value="Cancel" />
				</form>
			</c:otherwise>
		</c:choose>

	</fieldset>

	<script language="JavaScript">
		document.getElementById('formSubject').focus();
		document.getElementById('buttonSubmit').disabled = true;
		function enableButton()
		{
			if (document.getElementById('formMessage').value != "" && document.getElementById('formSubject').value != "")
			{
				document.getElementById('buttonSubmit').disabled = false;
			}
			else
			{
				document.getElementById('buttonSubmit').disabled = true;
			}
		}
	</script>

	<c:if test="${!empty msg.mailData}">
		<p>
			From
			<span class="authorName"><c:out value="${msg.mailData.fromName}"/></span>
			
			<c:if test="${!empty msg.mailData.fromEmail}">
				<span class="authorEmail">
					&lt;<a href="mailto:<c:out value="${msg.mailData.fromEmail}"/>"><c:out value="${msg.mailData.fromEmail}"/></a>&gt;
				</span>
			</c:if>
			
			<span class="messageDate"><fmt:formatDate value="${msg.mailData.sentDate}" type="both" timeStyle="short" /></span>
		</p>
	
		<div class="message">
			<c:forEach var="part" items="${msg.mailData.textParts}">
				<div class="messagePart">
					<pre class="message" ><c:out value="${part.contents}"/></pre>
				</div>
			</c:forEach>
		</div>
	</c:if>

</trim:list>