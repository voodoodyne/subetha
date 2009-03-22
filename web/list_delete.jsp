<%@include file="/inc/top_standard.jspf" %>

<trim:list title="Delete List" listId="${param.listId}">
	<p>
		This is NOT reversable!  It will delete all subscriptions and
		all archived messages.  You must enter your password to confirm this
		operation.
	</p>
	
	<p>
		Note:  This might take a while.
	</p>
	
	<c:if test="${model.wrongPassword}">
		<p class="error">Wrong password.</p>
	</c:if>
	
	<form action="<c:url value="/list_delete_submit.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}"/>
		Your password: <input type="password" name="password" value="${model.password}" />
		<input type="submit" value="DELETE THIS LIST"/>
	</form>
</trim:list>