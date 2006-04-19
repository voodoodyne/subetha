<%@include file="/inc/top_standard.jspf" %>

<t:action var="msgs" type="org.subethamail.web.action.GetThreads" />

<trim:list title="Archive" listId="${param.listId}">

	<c:choose>
		<c:when test="${empty msgs}">
			<p>
				There are no messages in the archive.
			</p>
		</c:when>
		<c:otherwise>
			<div class="summaries">
				<se:summaries msgs="${msgs}"/>
			</div>
		</c:otherwise>
	</c:choose>
	
</trim:list>