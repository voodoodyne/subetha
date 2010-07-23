<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SearchArchive" />

<trim:list title="Archive Search" listId="${param.listId}">

	<div id="searchBar">
		<form action="<c:url value="/archive_search.jsp"/>" method="get">
			<input type="hidden" name="listId" value="${param.listId}" />
			
			<input type="text" size="40" name="query" value="<c:out value="${model.query}"/>" />
			<input type="submit" value="Search" />
		</form>
	</div>

	<c:choose>
		<c:when test="${! empty model.error}">
			<p>
				<c:out value="${model.error}" />
			</p>
		</c:when>
		<c:when test="${empty model.hits}">
			<p>
				The search returned no matches.
			</p>
		</c:when>
		<c:otherwise>
			<table class="sort-table" id="searchResult">
				<thead>
					<tr>
						<td>Subject</td>
						<td>Author</td>
						<td>Date</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="msg" items="${model.hits}" varStatus="loop">
						<c:choose>
							<c:when test="${loop.index % 2 == 0}">
								<c:set var="color" value="a"/>
							</c:when>
							<c:otherwise>
								<c:set var="color" value="b"/>
							</c:otherwise>
						</c:choose>
						<tr class="${color}">
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
				</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("searchResult"), ["String", "String", "String"]);
st1.onsort = st1.tableRowColors;
</script>

			<c:url var="queryURL" value="/archive_search.jsp">
				<c:param name="listId" value="${model.listId}"/>
				<c:param name="query" value="${model.query}"/>
			</c:url>
			<se:searchPaginator url="${queryURL}&" model="${model}"/>

		</c:otherwise>
	</c:choose>
	
</trim:list>