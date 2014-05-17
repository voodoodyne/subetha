<%@include file="inc/taglibs.jspf" %>

<%@attribute name="msgs" required="true" type="java.util.List" %>
<%@attribute name="highlight" required="false" type="org.subethamail.core.lists.i.MailSummary" %>
<%@attribute name="flat" required="false" type="java.lang.Boolean" %>

<c:if test="${!empty msgs}">
	<c:choose>
		<c:when test="${flat}">
			<c:forEach var="msg" items="${msgs}">
				<li>
					<se:summary msg="${msg}" highlight="${highlight}" />
				</li>
				<se:summaryNodeFlat msg="${msg}" highlight="${highlight}" />
			</c:forEach>
		</c:when>
		<c:otherwise>
			<ul>
				<c:forEach var="msg" items="${msgs}">
					<li>
						<se:summaryNode msg="${msg}" highlight="${highlight}" flat="${flat}"/>
					</li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</c:if>