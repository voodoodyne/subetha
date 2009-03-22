<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.SiteStatusSave"/>

<c:choose>
	<c:when test="${empty model.errors}">
		<c:redirect url="/site_status.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:forward page="/site_status_edit.jsp" />
	</c:otherwise>				
</c:choose>
