<%@include file="/inc/top_standard.jspf" %>

<t:action var="msgs" type="org.subethamail.web.action.GetThreads" />

<trim:list title="Archive">
	<ul>
		<c:forEach var="msg" items="${msgs}">
			<li>
				<span class="subject"><c:out value="${msg.subject}"/></span>
				<span class="authorName"><c:out value="${msg.fromName}"/></span>
				<c:if test="${!empty msg.fromEmail}">
					<span class="authorEmail">
						&lt;<a href="mailto:<c:out value="${msg.fromEmail}"/>"><c:out value="${msg.fromEmail}"/></a>&gt;
					</span>
				</c:if>
			</li>
		</c:forEach>
	</ul>
</trim:list>