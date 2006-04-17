<%@include file="/inc/top_standard.jspf" %>

<trim:admin title="Inject Mail">
	<h1>Inject Mail</h1>
	
	<p>
		This form is for debugging.  It allows you to directly
		inject a piece of SMTP mail.
	</p>
	
	<form action="<c:url value="/inject" />" method="post">
		<table>
			<tr>
				<th><label for="authName">Auth Name</label></th>
				<td>
					<input id="authName" name="authName" type="text" size="60" />
				</td>
			</tr>
			<tr>
				<th><label for="authPassword">Auth Password</label></th>
				<td>
					<input id="authPassword" name="authPassword" type="password" size="60" />
				</td>
			</tr>
			<tr>
				<th><label for="recipient">Recipient</label></th>
				<td>
					<input id="recipient" name="recipient" type="text" size="60" />
				</td>
			</tr>
			<tr>
				<th><label for="message">Message Body</label></th>
				<td>
					<textarea id="message" name="message" rows="25" cols="80" style="width:95%"></textarea>
				</td>
			</tr>
		</table>
		
		<input type="submit" value="Inject Mail" />
	</form>

</trim:admin>