<%@include file="inc/taglibs.jspf" %>

<%-- Note that url *must* include a trailing '?' or '&' --%>
<%@attribute name="url" required="true" %>
<%@attribute name="model" required="true" type="org.subethamail.web.model.PaginateModel" %>

<div class="paginator">
	<c:if test="${model.totalCount > 0}">
		<div class="main">
			<c:choose>
				<c:when test="${model.head}">
					<span class="disabled">Prev &lt;</span>
				</c:when>
				<c:otherwise>
					<a href="${url}skip=${model.previous}&count=${model.count}">Prev &lt;</a>
				</c:otherwise>
			</c:choose>
			
			<c:forEach var="page" items="${model.paginationPages}">
				<c:choose>
					<c:when test="${page.current}">
						<strong>${page.displayNumber}</strong>
					</c:when>
					<c:otherwise>
						<a href="${url}skip=${page.skip}&count=${model.count}">${page.displayNumber}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<c:choose>
				<c:when test="${model.tail}">
					<span class="disabled">&gt; Next</span>
				</c:when>
				<c:otherwise>
					<a href="${url}skip=${model.next}&count=${model.count}">&gt; Next</a>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
	
	<div class="total">
		${model.totalCount} results total
	</div>
</div>