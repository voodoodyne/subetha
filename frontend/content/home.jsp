<%@include file="inc/top_standard.jspf" %>

<trim:plain title="Sub-Etha Mail">
	<h1>Welcome, <c:out value="${me.name}"/></h1>
	
	<p>
		Probably this should be split into multiple pages
		with subnav on LHS?
		We need a "change name and password" page too.
	</p>
	
	<p>
		Your email addresses:
	</p>
	<table>
		<tr>
			<td>foo@bar.com</td>
			<td><input type="submit" value="remove" /></td>
		</tr>
		<tr>
			<td>bob@subgenius.com</td>
			<td><input type="submit" value="remove" /></td>
		</tr>
		<tr>
			<td>marvin@siriuscybernetics.com</td>
			<td><input type="submit" value="remove" /></td>
		</tr>
		<tr>
			<td><input type="text" /></td>
			<td><input type="submit" value="add" /></td>
		</tr>
	</table>
	
	<p>
		Your subscriptions:
	</p>
	<table>
		<tr>
			<th>List</th>
			<th>Role</th>
			<th>Deliver To</th>
		</tr>
		<tr>
			<td>announce@happyhour.com</td>
			<td>Owner</td>
			<td>
				<select>
					<option>Delivery Disabled</option>
					<option>foo@bar.com</option>
					<option>bob@subgenius.com</option>
					<option selected="true">marvin@siriuscybernetics.com</option>
				</select>
				<input type="submit" value="set" />
			</td>
		</tr>
		<tr>
			<td>goodgod@goatse.cx</td>
			<td>Reader</td>
			<td>
				<select>
					<option selected="true">Delivery Disabled</option>
					<option>foo@bar.com</option>
					<option>bob@subgenius.com</option>
					<option>marvin@siriuscybernetics.com</option>
				</select>
				<input type="submit" value="set" />
			</td>
		</tr>
	</table>
</trim:plain>