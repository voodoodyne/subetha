<%@include file="/inc/top_standard.jspf" %>

<t:action var="loginModel" type="org.subethamail.web.action.auth.Login"/>

<c:if test="${empty loginModel.error}">
	<c:choose>
		<c:when test="${empty loginModel.dest}">
			<c:redirect url="/home.jsp"/>
		</c:when>
		<c:otherwise>
			<c:redirect url="${loginModel.dest}"/>
		</c:otherwise>				
	</c:choose>
</c:if>


<trim:headerless title="Login Failed">
	<h1>Login Failed</h1>
	
	<p class="error">
		<c:out value="${loginModel.error}"/>
	</p>
	
	<%@include file="/inc/login_form.jspf" %>
</trim:headerless>
