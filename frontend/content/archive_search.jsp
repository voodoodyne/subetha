<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SearchArchive" />

<trim:list title="Archive Search" listId="${param.listId}">

	<div class="searchBar">
		<form action="<c:url value="/archive_search.jsp"/>" method="get">
			<input type="hidden" name="listId" value="${param.listId}" />
			
			<input type="text" size="50" name="query" value="<c:out value="${model.query}"/>" />
			<input type="submit" value="Search" />
		</form>
	</div>
	
	<c:choose>
		<c:when test="${empty model.hits}">
			<p>
				The search returned no matches.
			</p>
		</c:when>
		<c:otherwise>
			<table class="searchResult">
				<tr>
					<th>Subject</th>
					<th>Author</th>
					<th>Date</th>
				</tr>
				<c:forEach var="msg" items="${model.hits}">
					<tr>
						<%-- <td>${msg.score}</td> --%>
						<td>
							<span class="subject">
								<c:url var="msgUrl" value="/archive_msg.jsp">
									<c:param name="msgId" value="${msg.id}"/>
								</c:url>
								<a href="${msgUrl}"><c:choose
									><c:when test="${empty msg.subject}">(no subject)</c:when
									><c:otherwise><c:out value="${msg.subject}"/></c:otherwise
								></c:choose></a>
							</span>
						</td>
						<td>
							<span class="authorName"><c:out value="${msg.fromName}"/></span>
							
							<c:if test="${!empty msg.fromEmail}">
								<span class="authorEmail">
									&lt;<a href="mailto:<c:out value="${msg.fromEmail}"/>"><c:out value="${msg.fromEmail}"/></a>&gt;
								</span>
							</c:if>
						</td>
						<td>
							<span class="messageDate"><fmt:formatDate value="${msg.sentDate}" type="both" timeStyle="short" /></span>
						</td>
					</tr>
				</c:forEach>
			</table>

			<c:url var="queryURL" value="/archive_search.jsp">
				<c:param name="listId" value="${model.listId}"/>
				<c:param name="query" value="${model.query}"/>
			</c:url>
			<se:searchPaginator url="${queryURL}&" model="${model}"/>

		</c:otherwise>
	</c:choose>
	
</trim:list>