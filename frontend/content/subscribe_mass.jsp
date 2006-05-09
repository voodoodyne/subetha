<%@include file="/inc/top_standard.jspf" %>

<trim:list title="Mass Subscribe" listId="${param.listId}">
	<p>
		Enter a list of email addresses.
	</p>

	<form action="<c:url value="/subscribe_mass_submit.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table>
			<tr>
				<th>Invite?</th>
				<td>
					<input id="invite" name="invite" type="checkbox" value="true" 
						<c:if test="${model.invite}">checked="checked"</c:if>
					/>
					<label for="invite">Send invite instead of subscribe</label>
				</td>
			</tr>
			<tr>
				<th><label for="emails">Email&nbsp;Addresses</label></th>
				<td
					<c:if test="${!empty model.errors.emails}">
						class="error"
					</c:if>
				>
					Email addresses should be space or comma separated, and may contain
					personal names.  I.e. "Joe User" &lt;juser@nowhere.com&gt;
					<textarea id="emails" name="emails" rows="15" cols="60" style="width:95%"
					><c:out value="${model.emails}"/></textarea>
					
					<c:if test="${!empty model.errors.emails}">
						<p class="error"><c:out value="${model.errors.emails}"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		<input type="submit" value="Subscribe" />
	</form>

</trim:list>