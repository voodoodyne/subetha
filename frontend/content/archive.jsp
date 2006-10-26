<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetThreads" />

<t:action var="myList" type="org.subethamail.web.action.GetMyListRelationship">
	<t:param name="listId" value="${model.listId}"/>
</t:action>
<c:set var="perms" value="${myList.perms}"/>

<trim:list title="Archive" listId="${param.listId}">

	<c:choose>
		<c:when test="${empty model.messages}">
			<p>
				There are no messages in the archive.
			</p>
		</c:when>
		<c:otherwise>

			<table>
				<tr>
					<td>
						<fieldset><legend>Search</legend>
						<div class="searchBar">
							<form action="<c:url value="/archive_search.jsp"/>" method="get">
								<input type="hidden" name="listId" value="${param.listId}" />
								
								<input type="text" name="query" id="query" size="50" onkeyup="enableSingleField('query', 'searchSubmit');" />
								<input type="submit" value="Search" id="searchSubmit" />
							</form>
							<script type="text/javascript">
								document.getElementById('searchSubmit').disabled=true;
								document.getElementById('query').focus();
							</script>
						</div>
						</fieldset>
					</td>
					<c:if test="${perms.POST}">
						<td>
							<fieldset><legend>Post</legend>
								<form action="<c:url value="/msg_send.jsp"/>" method="get">
									<input type="hidden" name="listId" value="${param.listId}" />
									<input type="submit" value="Compose Message" />
								</form>
							</fieldset>
						</td>
					</c:if>
				</tr>
			</table>
	
			<ul class="rootSummaries">
				<c:forEach var="root" items="${model.messages}">
					<li>
						<se:summary msg="${root}" />

						<c:if test="${!empty root.replies}">
							<div class="nestedSummaries">
								<ul>
									<se:summaries msgs="${root.replies}" flat="true"/>
								</ul>
							</div>
						</c:if>
					</li>
				</c:forEach>
			</ul>

			<c:url var="queryURL" value="/archive.jsp">
				<c:param name="listId" value="${model.listId}"/>
			</c:url>
			<se:searchPaginator url="${queryURL}&" model="${model}"/>

		</c:otherwise>
	</c:choose>
	
</trim:list>