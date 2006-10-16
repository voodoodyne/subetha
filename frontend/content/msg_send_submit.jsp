<%@include file="inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.InjectMessage"/>

<c:choose>
	<c:when test="${!empty model.errors}">
		<c:set var="subject" value="${model.subject}" />
		<jsp:forward page="/msg_send.jsp"/>
	</c:when>
	<c:otherwise>
		<trim:list title="Send success" listId="${model.listId}">
		
			<p>
				Your message has been successfully sent to the list. 
				<c:url value="/archive.jsp" var="archivesUrl">
					<c:param name="listId" value="${model.listId}" />
				</c:url>
				Would you like to return to the <a href="<c:out value="${archivesUrl}" />">archives</a>?
			</p>
		
		</trim:list>
	</c:otherwise>
</c:choose>
