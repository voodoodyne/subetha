<%@include file="/inc/top_standard.jspf" %>

<trim:list title="Subscribed" listId="${model.listId}">
	<c:url var="url" value="list.jsp">
		<c:param name="listId" value="${model.listId}"/>
	</c:url>
	<p>
		You are now subscribed.  To review your options for this list,
		visit the <a href="${url}">list overview</a>.
	</p>
</trim:list>