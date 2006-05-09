<%@include file="inc/top_standard.jspf" %>

<trim:plain title="Confirm Additional Email Address">
	<p>
		An email has been sent to the address you specified.  It contains
		a confirmation code which will allow you to verify the new address
		and add it to your account.
	</p>
	
	<p>
		You may either:
	</p>
	
	<ul>
		<li>Click on the link embedded in the email.</li>
		<li>Copy and paste the confirmation code into the form below.</li>
	</ul>
	<form action="<c:url value="/email_confirm_submit.jsp"/>" method="post">
		<table>
			<tr>
				<th><label for="token">Confirmation Code</label></th>
				<td <c:if test="${!empty model.errors.badtoken}">class="error"</c:if>
					<c:if test="${!empty model.errors.token}">class="error"</c:if> >
					<input id="token" name="token" value="<c:out value="${model.token}"/>" size="60" type="text"/>
					
					<c:if test="${!empty model.errors.token}">
						<div class="error"><c:out value="${model.errors.token}"/></div>
					</c:if>
					<c:if test="${!empty model.errors.badtoken}">
						<div class="error"><c:out value="${model.errors.badtoken}"/></div>
					</c:if>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
					<input type="submit" value="Confirm"/>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
				<span style="font-size:smaller">
					Note: If you did not receive a confirmation, please click your browsers back button and resubmit your email address.
				</span>
				</td>
			
			</tr>
			
		</table>
	</form>
</trim:plain>

