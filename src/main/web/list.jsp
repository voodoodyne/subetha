<%@include file="/inc/top_standard.jspf" %>

<c:if test="${auth.loggedIn}">
	<c:set var="me" value="${backend.accountMgr.self}"/>
</c:if>

<t:action var="myList" type="org.subethamail.web.action.GetMyListRelationship" />
<c:set var="perms" value="${myList.perms}"/>
	
<t:action var="list" type="org.subethamail.web.action.GetList" />

<t:action var="listStats" type="org.subethamail.web.action.GetListStats">
	<t:param name="perms" value="${myList.rawPerms}" />
</t:action>

<trim:list title="List Overview" listId="${param.listId}">
	
	<fieldset>
		<legend>Description</legend>
		
		<p>${f:escapeText(list.description)}</p>
	</fieldset>
	
	<fieldset>
		<legend>List Information</legend>
		<ul>
			<li>Public URL: <a href="${f:escapeText(list.url)}">${f:escapeText(list.url)}</a></li>
			<c:if test="${listStats.subscriberCount > 0}">
				<c:url var="subscribersUrl" value="/list_subscribers.jsp">
					<c:param name="listId" value="${list.id}"/>
				</c:url>
				<li><a href="${subscribersUrl}">${listStats.subscriberCount}</a> subscribers</li>
			</c:if>
			
			<c:if test="${listStats.archiveCount > 0}">
				<c:url var="archiveUrl" value="/archive.jsp">
					<c:param name="listId" value="${list.id}"/>
				</c:url>
				<c:url var="exportUrl" value="/export/${list.id}/${list.name}.zip"/>		
				<li><a href="${archiveUrl}">${listStats.archiveCount}</a> messages (<a href="${exportUrl}">Download all</a>) </li>			
			</c:if>
			
			<c:if test="${list.subscriptionHeld}">
				<li>Subscription requests will be reviewed by moderators</li>
			</c:if>
		</ul>
	</fieldset>	
	<c:if test="${perms.EDIT_SETTINGS || perms.APPROVE_MESSAGES || perms.APPROVE_SUBSCRIPTIONS}">
		<fieldset>
			<legend>Administration</legend>
			
			<c:if test="${perms.EDIT_SETTINGS}">
				<div style="float: right">
					<form action="<c:url value="/list_settings.jsp"/>" method="get">
						<input type="hidden" name="listId" value="${list.id}" />
						<input type="submit" value="Edit List Settings" style="width: 120px"/>
					</form>
					<c:if test="${auth.siteAdmin}">
						<form action="<c:url value="/list_delete.jsp"/>" method="get">
							<input type="hidden" name="listId" value="${list.id}" />
							<input type="submit" value="Delete List"  style="width: 120px"/>
						</form>
					</c:if>
				</div>
			</c:if>
			<c:if test="${listStats.heldSubscriptionCount > 0 || listStatus.heldMessageCount > 0}">
				<ul>
					<c:if test="${listStats.heldSubscriptionCount > 0}">
						<c:url var="listHeldSubsUrl" value="/held_subs.jsp">
							<c:param name="listId" value="${list.id}"/>
						</c:url>
						<li><a href="${listHeldSubsUrl}">${listStats.heldSubscriptionCount}</a> held subscriptions</li>
					</c:if>
					<c:if test="${listStatus.heldMessageCount > 0}">
						<c:url var="listHeldMsgsUrl" value="/held_msgs.jsp">
							<c:param name="listId" value="${list.id}"/>
						</c:url>
						<li><a href="${listHeldMsgsUrl}">${listStats.heldMessageCount}</a> held messages</li>
					</c:if>
				</ul>
			</c:if>
			<table>
				<tr>
					<th valign="top">Welcome message for new subscribers:</th>
					<td>${f:escapeText(list.welcomeMessage)}</td>
				</tr>
				<tr>
					<th>Require approval for new subscriptions?</th>
					<td>${list.subscriptionHeld}</td>
				</tr>
			</table>
		</fieldset>
	</c:if>

	<fieldset>
		<legend>Status</legend>
		
		<c:choose>
			<c:when test="${myList.subscribed}">
				<table><tr><td>
					<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
						<input type="hidden" name="listId" value="${list.id}" />
						<input type="hidden" name="goto" value="/list.jsp?listId=${list.id}" />
						<p>
							You are subscribed and mail will be delivered to 
							<select name="deliverTo">
								<option value="">Disable Delivery</option>
								<c:forEach var="email" items="${me.emailAddresses}">
									<option value="<c:out value="${email}"/>"
										<c:if test="${email == myList.deliverTo}">selected="selected"</c:if>
									><c:out value="${email}"/></option>
								</c:forEach>
							</select><input type="submit" value="Update" />
						</p>
					</form>
					</td><td>
					<form action="<c:url value="/unsubscribe_me.jsp"/>" method="post">
						<input type="hidden" name="listId" value="${list.id}" />
						<p>
							Or you may <input type="submit" value="Unsubscribe" />
						</p>
					</form>
					</td></tr>
				</table>
			</c:when>
			<c:when test="${auth.loggedIn}">
				<p>
					You are not subscribed to this list.
				</p>
				<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
					<input type="hidden" name="listId" value="${list.id}" />
					
					Deliver To:
					<select name="deliverTo">
						<c:forEach var="email" items="${me.emailAddresses}">
							<option value="<c:out value="${email}"/>"><c:out value="${email}"/></option>
						</c:forEach>
						<option value="">No Delivery</option>
					</select>
					<input type="submit" value="Subscribe" />
				</form>
			</c:when>
			<c:otherwise>
				<p>
					If you are already subscribed, you may log in to be presented
					with additional options. Use the form at the top of the page. 
					Otherwise, enter your name and email address below to
					subscribe.
				</p>
				<form action="<c:url value="/subscribe_anon.jsp"/>" method="post">
					<input type="hidden" name="listId" value="${list.id}" />
					<table>
						<tr>
							<th><label for="deliverTo">Your Email Address:</label></th>
							<td <c:if test="${!empty model.errors.deliverTo}">class="error"</c:if> >
								<input id="deliverTo" name="deliverTo" value="<c:out value="${model.deliverTo}"/>" type="text" size="40"
									onkeyup="enableDoubleFields('deliverTo', 'name', 'subSubmit')" />
								<c:if test="${!empty model.errors.deliverTo}">
									<p class="error"><c:out value="${model.errors.deliverTo}"/></p>
								</c:if>
							</td>
						</tr>
						<tr>
							<th><label for="name">Your Name:</label></th>
							<td <c:if test="${!empty model.errors.name}">class="error"</c:if> >
								<input id="name" name="name" value="<c:out value="${model.name}"/>" type="text" size="40"
									onkeyup="enableDoubleFields('deliverTo', 'name', 'subSubmit')" />
								<c:if test="${!empty model.errors.name}">
									<p class="error"><c:out value="${model.errors.name}"/></p>
								</c:if>
							</td>
						</tr>
						<tr>
							<th></th>
							<td><input type="submit" value="Subscribe" id="subSubmit" /></td>
						</tr>
					</table>
					<script type="text/javascript">
						document.getElementById('subSubmit').disabled=true;
					</script>
				</form>
			</c:otherwise>
		</c:choose>
	</fieldset>
</trim:list>