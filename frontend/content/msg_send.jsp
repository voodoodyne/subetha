<%@include file="/inc/top_standard.jspf" %>

<c:choose>
	<c:when test="${param.type == 'post'}">
		<c:set var="type" value="Post"/>
		<c:set var="title" value="Post Message" />
		<c:set var="listId" value="${param.listId}" />
	</c:when>
	<c:otherwise>
		<t:action var="msg" type="org.subethamail.web.action.GetMessage" />
		<c:set var="type" value="Reply"/>
		<c:if test="${empty subject}">
			<c:set var="subject" value="Re: ${msg.subject}" />
		</c:if>
		<c:set var="title" value="${subject}" />
		<c:set var="listId" value="${msg.listId}" />
	</c:otherwise>
</c:choose>

<trim:list title="${title}" listId="${listId}">

	<fieldset> <legend><c:out value="${type}: ${msg.subject}" /></legend>
		<form action="<c:url value="/msg_send_submit.jsp"/>" method="post" class="form-inline">
			<c:if test="${!empty msg}">
				<input type="hidden" name="msgId" value="${msg.id}"/>
			</c:if>
			<c:if test="${empty msg}">
				<input type="hidden" name="listId" value="${listId}"/>
			</c:if>
			<input type="hidden" name="type" value="${param.type}"/>
			<table>
				<tr>
					<th>Subject:</th>
					<td>
						<input type="text" id="formSubject" name="subject" size="80" 
							value="<c:out value="${subject}" />"
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
		<c:if test="${!empty msg}">
			<form action="<c:url value="/archive_msg.jsp"/>" method="get" class="form-inline">
				<input type="hidden" name="msgId" value="${msg.id}"/>
				<input type="submit" value="Cancel" />
			</form>
		</c:if>
		<c:if test="${empty msg}">
			<form action="<c:url value="/archive.jsp"/>" method="get" class="form-inline">
				<input type="hidden" name="listId" value="${listId}"/>
				<input type="submit" value="Cancel" />
			</form>
		</c:if>
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
	</c:if>

</trim:list>