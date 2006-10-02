<%@include file="/inc/top_standard.jspf" %>

<trim:list title="Mass Subscribe" listId="${param.listId}">

	<fieldset>
	<legend>Enter a list of email addresses</legend>

	<form action="<c:url value="/subscribe_mass_submit.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table>
			<tr>
				<th>How?</th>
				<td>
					<input id="invite" name="how" type="radio" value="INVITE" 
						<c:if test="${model.how == 'INVITE' || empty model.how}">checked="checked"</c:if>
					/>
					<label for="invite">Send Invite</label>
					|
					<input id="welcome" name="how" type="radio" value="WELCOME" 
						<c:if test="${model.how == 'WELCOME'}">checked="checked"</c:if>
					/>
					<label for="welcome">Send Welcome Message</label>
					|
					<input id="silent" name="how" type="radio" value="SILENT" 
						<c:if test="${model.how == 'SILENT'}">checked="checked"</c:if>
					/>
					<label for="silent">Silently Subscribe</label>
				</td>
			</tr>
			<tr>
				<th><label for="emails">Email&nbsp;Addresses</label></th>
				<td
					<c:if test="${!empty model.errors.emails}">
						class="error"
					</c:if>
				>
					<textarea id="emails" name="emails" rows="10" cols="50" style="width:95%"
					 onkeyup="enableSingleField('emails', 'submit');"
					><c:out value="${model.emails}"/></textarea>
					<div>
						<small>
							Email addresses should be comma separated, and may contain
							personal names.  For example:<br/>
							 "Joe User" &lt;juser@nowhere.com&gt;, Bob &lt;bob@nowhere.com&gt;, another@nowhere.com
						</small>
					</div>
					<c:if test="${!empty model.errors.emails}">
						<p class="error"><c:out value="${model.errors.emails}"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		<input type="submit" value="Subscribe" id="submit" />
	</form>
	<script type="text/javascript">
		document.getElementById('submit').disabled=true;
		document.getElementById('emails').focus();
	</script>

	</fieldset>

</trim:list>