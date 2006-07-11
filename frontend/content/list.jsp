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
	
	<h3><c:out value="${list.description}" /></h3>
	
	<ul>
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
			<li><a href="${archiveUrl}">${listStats.archiveCount}</a> archived messages</li>
		</c:if>
		
		<c:if test="${list.subscriptionHeld}">
			<li>Subscription requests will be reviewed by moderators</li>
		</c:if>

	<%-- This is a work in progress
		<li>List owners are:
			<ul>
				<li>Jeff Schnitzer &lt;jeff@infohazard.org&gt;</li>
				<li>Jon Stevens &lt;jon@latchkey.com&gt;</li>
			</ul>
		</li>
	--%>
	</ul>
	
	<c:if test="${perms.EDIT_SETTINGS || perms.APPROVE_MESSAGES || perms.APPROVE_SUBSCRIPTIONS}">
		<fieldset>
			<legend>Administration</legend>
			
			<c:if test="${perms.EDIT_SETTINGS}">
				<div style="float: right">
					<form action="<c:url value="/list_settings.jsp"/>" method="get">
						<input type="hidden" name="listId" value="${list.id}" />
						<input type="submit" value="Edit List Settings" />
					</form>
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
					<th>Welcome message for new subscribers:</th>
					<td><c:out value="${list.welcomeMessage}"/></td>
				</tr>
				<tr>
					<th>Require approval for new subscriptions?</th>
					<td>${list.subscriptionHeld}</td>
				</tr>
			</table>
		</fieldset>
	</c:if>
	
	<c:choose>
		<c:when test="${myList.subscribed}">
			<p>
				You are subscribed to this list.
			</p>
			
			<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${list.id}" />
				<input type="hidden" name="goto" value="/list.jsp?listId=${list.id}" />
				<p>
					Mail will be delivered to 
					<select name="deliverTo">
						<option value="">Disable Delivery</option>
						<c:forEach var="email" items="${me.emailAddresses}">
							<option value="<c:out value="${email}"/>"
								<c:if test="${email == myList.deliverTo}">selected="selected"</c:if>
							><c:out value="${email}"/></option>
						</c:forEach>
					</select><input type="submit" value="Set" />
				</p>
			</form>
			
			<form action="<c:url value="/unsubscribe_me.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${list.id}" />
				<p>
					You may <input type="submit" value="Unsubscribe" />
				</p>
			</form>
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
							<input id="deliverTo" name="deliverTo" value="<c:out value="${model.deliverTo}"/>" type="text" size="60" />
							<c:if test="${!empty model.errors.deliverTo}">
								<p class="error"><c:out value="${model.errors.deliverTo}"/></p>
							</c:if>
						</td>
					</tr>
					<tr>
						<th><label for="name">Your Name:</label></th>
						<td <c:if test="${!empty model.errors.name}">class="error"</c:if> >
							<input id="name" name="name" value="<c:out value="${model.name}"/>" type="text" size="60" />
							<c:if test="${!empty model.errors.name}">
								<p class="error"><c:out value="${model.errors.name}"/></p>
							</c:if>
						</td>
					</tr>
					<tr>
						<th></th>
						<td><input type="submit" value="Subscribe" /></td>
					</tr>
				</table>
			</form>
		</c:otherwise>
	</c:choose>
</trim:list>