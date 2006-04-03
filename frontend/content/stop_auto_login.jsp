<%@include file="/inc/taglibs.jspf" %>
<%-- Does NOT include the standard top, which would trigger an autologin --%>

<t:action type="org.subethamail.web.action.auth.StopAutoLogin"/>

<c:redirect url="/index.jsp" />
