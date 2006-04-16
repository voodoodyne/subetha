<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired"/>

<trim:admin title="Inject Mail">
	<h1>Inject Mail</h1>
	
	<p>
		This form is for debugging.  It allows you to directly
		inject a piece of SMTP mail.
	</p>
	
	<form action="<c:url value="/inject_submit.jsp"/>" method="post">
		<table>
			<tr>
				<th><label for="to">To Address</label></th>
				<td
					<c:if test="${!empty model.errors.to}">
						class="error"
					</c:if>
				>
					<input id="to" name="to" type="text" size="60" value="${model.to}" />
					
					<c:if test="${!empty model.errors.to}">
						<p class="error"><c:out value="${model.errors.to}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="body">Message Body</label></th>
				<td
					<c:if test="${!empty model.errors.body}">
						class="error"
					</c:if>
				>
					<textarea id="body" name="body" rows="30" cols="80" style="width:95%"
					><c:out value="${model.body}"/></textarea>
					
					<c:if test="${!empty model.errors.body}">
						<p class="error"><c:out value="${model.errors.body}"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		
		<input type="submit" value="Inject Mail" />
	</form>

</trim:admin>