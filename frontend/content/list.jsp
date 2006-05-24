<%@include file="/inc/top_standard.jspf" %>

<c:if test="${auth.loggedIn}">
	<c:set var="me" value="${backend.accountMgr.self}"/>
</c:if>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />
<t:action var="list" type="org.subethamail.web.action.GetListSettings" />

<trim:list title="List Overview" listId="${param.listId}">
	
	<p><c:out value="${sub.list.description}" /></p>
	
	<c:choose>
		<c:when test="${sub.subscribed}">
			<c:set var="role" value="${sub.role}"/>
			<c:set var="perms" value="${f:wrapPerms(sub.perms)}" />
			
			<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${sub.list.id}" />
				<input type="hidden" name="goto" value="/list.jsp?listId=${sub.list.id}" />
				<p>
					<c:choose>
						<c:when test="${empty sub.deliverTo}">
							You have disabled delivery of mail from this list.
						</c:when>
						<c:otherwise>
							Messages from this list will be delivered to <strong><c:out value="${sub.deliverTo}"/></strong>.
						</c:otherwise>
					</c:choose>
				</p>
				<p>
					Change to
					<select name="deliverTo">
						<option value="">Disable Delivery</option>
						<c:forEach var="email" items="${me.emailAddresses}">
							<option value="<c:out value="${email}"/>"><c:out value="${email}"/></option>
						</c:forEach>
					</select><input type="submit" value="Change" />
				</p>
				<fieldset>	<legend>Your Permissions</legend>
					<table class="permissions" align="center">
						<tr>
							<c:forEach var="perm" items="${backend.allPermissions}">
								<th style="writing-mode: tb-rl">
									<img src="<c:url value="/perm_img?perm=${perm}"/>" alt="<c:out value="${perm.pretty}"/>" />
								</th>
							</c:forEach>
						</tr>
						<tr>
							<c:forEach var="perm" items="${backend.allPermissions}">
								<td>
									<c:if test="${f:contains(role.permissions, perm)}">
										<img src="<c:url value="/img/check.gif"/>" alt="Yes" />
									</c:if>
								</td>
							</c:forEach>
						</tr>
					</table>
				</fieldset>	
				
			</form>
			
			<fieldset><legend>Tasks</legend>
				
				<c:if test="${perms.APPROVE_SUBSCRIPTIONS}">
					<t:action var="heldSubs" type="org.subethamail.web.action.GetHeldSubscriptions" />
					<c:if test="${!empty heldSubs}">
						<h2>Held Subscriptions</h2>
						<c:url var="listHeldSubsUrl" value="/held_subs.jsp">
							<c:param name="listId" value="${param.listId}"/>
						</c:url>
						There are <a href="${listHeldSubsUrl}"><c:out value="${fn:length(heldSubs)}"/> held subscriptions waiting...</a>
						<ul>
							<c:forEach var="sub" items="${heldSubs}" varStatus="loop">
								<li><c:out value="${sub}"/></li>
							</c:forEach>>
						</ul>
					</c:if>
				</c:if>
				
				<br/>
				
				<c:if test="${perms.APPROVE_MESSAGES}">
					<t:action var="heldMsgs" type="org.subethamail.web.action.GetHeldMessages" />
					<c:if test="${!empty heldMsgs}">
						<h2>Held Messages</h2>
						<c:url var="listHeldMsgsUrl" value="/held_msgs.jsp">
							<c:param name="listId" value="${param.listId}"/>
						</c:url>
						There are <a href="${listHeldMsgsUrl}"><c:out value="${fn:length(heldMsgs)}"/> held messsages waiting.</a>.
						<ul>
							<c:forEach var="msg" items="${heldMsgs}" varStatus="loop">
								<li><c:out value="${msg.subject}"/></li>
							</c:forEach>
						</ul>
					</c:if>
				</c:if>
			</fieldset>
		</c:when>
		<c:when test="${auth.loggedIn}">
			<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${sub.list.id}" />
				
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
				Otherwise, please enter your name and email address below to
				subscribe to the list.
			</p>
			<form action="<c:url value="/subscribe_anon.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${sub.list.id}" />
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