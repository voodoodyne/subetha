<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetThreads" />

<trim:list title="Archive" listId="${param.listId}">

	<c:choose>
		<c:when test="${empty model.messages}">
			<p>
				There are no messages in the archive.
			</p>
		</c:when>
		<c:otherwise>
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