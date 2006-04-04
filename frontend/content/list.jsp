<%@include file="/inc/top_standard.jspf" %>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />

<c:set var="list" value="${sub.list}" />

<trim:list title="${list.email}" sub="${sub}">
	<h1><c:out value="${list.name}" /></h1>
	<h2><c:out value="${list.email}" /></h2>
	
	<p><c:out value="${list.description}" /></p>
	
	<p>
		The list of actions available on the LHS will be restricted
		by the permissions of the user.
	</p>
	
	<p>
		If user is logged in and subscribed, they should see their
		delivery options.  If user is logged in and not subscribed,
		they should see:
	</p>
	
	<form action="subscribe.jsp" method="post">
		<select>
			<option>bob@subgenius.com</option>
			<option>marvin@siriuscybernetics.com</option>
			<option>Disable Delivery</option>
		</select>
		<input type="submit" value="Subscribe" />
	</form>
	
	<p>
		Maybe the interface should allow them to subscribe an additional
		email address too?  It would automatically add the address to the
		current account.
	</p>
	
	<p>
		If they are not logged in, and subscription is allowed, they should see:
	</p>

	<form action="subscribe_anonymous.jsp" method="post">
		<table>
			<tr>
				<th>Your Email Address:</th>
				<td><input type="text" size="60" /></td>
			</tr>
			<tr>
				<th>Your Name:</th>
				<td><input type="text" size="60" /></td>
			</tr>
			<tr>
				<th></th>
				<td><input type="submit" value="Subscribe" /></td>
			</tr>
		</table>
	</form>

</trim:list>