<%@include file="inc/top_standard.jspf" %>

<trim:admin title="Create List">
	<h1>Create List</h1>
	
	<p>
		This page lets site admins create a new mailing list.  It shows
		all the "list wizard" create options.
	</p>
	
	<form action="list_create_submit.jsp" method="post">
		<table border="1">
			<tr>
				<th>List Address</th>
				<td>
					<input type="text" size="60" />
					<div>Example:  announce@somedomain.com</div>
				</td>
			</tr>
			<tr>
				<th>List URL</th>
				<td>
					<input type="text" size="60" />
					<div>Example:  http://somedomain.com/list/announce</div>
					<div>The URL <strong>must</strong> contain the /list/ path</div>
				</td>
			</tr>
			<tr>
				<th>
					Blueprint
					<p>
						Blueprints represent a starting point.  List configuration can
						be changed at any time.
					</p>
				</th>
				<td>
					<table>
						<tr>
							<th><input type="radio" /> Announce-Only List</th>
							<td>
								Create a list which allows only moderators to post.  Normal
								users are not allowd to view the subscriber list.
							</td>
						</tr>
						<tr>
							<th><input type="radio" /> Social List</th>
							<td>
								Create a list suitable for social groups.
								Subscriptions must be approved by moderators
								but any subscriber may post.  Reply-To will be
								set back to the list.
							</td>
						</tr>
						<tr>
							<th><input type="radio" /> Barebones List</th>
							<td>
								Create a list with no additional configuration.  No plugins
								will be added and the default role will have no permissions.
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th></th>
				<td><input type="submit" value="submit" /></td>
			</tr>
		</table>
	</form>

</trim:admin>