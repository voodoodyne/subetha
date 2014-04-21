<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.RebuildSearchIndexes"/>
<c:redirect url="/site_status.jsp"/>
