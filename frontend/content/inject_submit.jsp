<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.Inject"/>

<jsp:forward page="/inject.jsp" />

<%--
<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/inject.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/inject.jsp" />
	</c:otherwise>				
</c:choose>
--%>