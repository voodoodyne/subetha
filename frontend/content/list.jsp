<%@include file="/inc/top_standard.jspf" %>

<c:if test="${auth.loggedIn}">
	<c:set var="me" value="${backend.accountMgr.self}"/>
</c:if>

<t:action var="sub" type="org.subethamail.web.action.GetMySubscription" />

<c:set var="list" value="${sub.list}" />

<trim:list title="List Overview" sub="${sub}">
	
	<p><c:out value="${list.description}" /></p>
	
	<c:choose>
		<c:when test="${sub.subscribed}">
			<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${sub.list.id}" />
				<p>
					<c:choose>
						<c:when test="${empty sub.deliverTo}">
							You have disabled delivery of mail from this list.
						</c:when>
						<c:otherwise>
							Messages from this list will be delivered to <strong><c:out value="${sub.deliverTo}"/></strong>.
						</c:otherwise>
					</c:choose>
					Change to
					<select name="deliverTo">
						<option value="">Disable Delivery</option>
						<c:forEach var="email" items="${me.emailAddresses}">
							<option value="<c:out value="${email}"/>"><c:out value="${email}"/></option>
						</c:forEach>
					</select><input type="submit" value="Change" />
				</p>
			</form>
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
			<p>
				Maybe the interface should allow them to subscribe an additional
				email address too?  It would automatically add the address to the
				current account.
			</p>
		</c:when>
		<c:otherwise>
			<p>
				If you are already subscribed, you may log in to be presented
				with additional options.  Use the form at the top of the page.
			</p>
			<form action="<c:url value="/subscribe_anon.jsp"/>" method="post">
				<input type="hidden" name="listId" value="${sub.list.id}" />
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
		</c:otherwise>
	</c:choose>
</trim:list>