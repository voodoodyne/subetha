<%@include file="inc/top_standard.jspf" %>

<trim:plain title="Confirm Subscribe">
	<p>
		An email has been sent to the address you specified.  It contains
		a confirmation code which will allow you to subscribe.
	</p>
	
	<p>
		You may either:
	</p>
	
	<ul>
		<li>Click on the link embedded in the email.</li>
		<li>Copy and paste the confirmation code into the form below.</li>
	</ul>
	
	<form action="<c:url value="/subscribe_confirm_submit.jsp"/>" method="post">
		<table>
			<tr>
				<th><label for="token">Confirmation Code</label></th>
				<td <c:if test="${model.badTokenError}">class="error"</c:if> >
					<input id="token" name="token" value="<c:out value="${model.token}"/>" size="90" type="text"/>
					
					<c:if test="${model.badTokenError}">
						<div class="error">
							The code is invalid
						</div>
					</c:if>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
					<input type="submit" value="Confirm"/>
				</td>
			</tr>
		</table>
	</form>
</trim:plain>

