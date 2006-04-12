<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:home title="SubEtha Mail">
	<h1>Welcome, <c:out value="${me.name}"/></h1>
	
	<p>
		<strong>Your email addresses:</strong>
	</p>

	<form action="<c:url value="/email_add.jsp"/>" method="post">
	<table>
	<c:forEach var="email" items="${me.emailAddresses}">
		<tr>
			<td><a href="mailto:<c:out value="${email}"/>"><c:out value="${email}"/></a></td>
			<td>
				<c:if test="${auth.authName != email}">
					<input type="submit" name="email_<c:out value="${email}"/>" value="remove" />
				</c:if>
			</td>
		</tr>
	</c:forEach>
	</table>
	</form>

	<form action="<c:url value="/email_remove.jsp"/>" method="post">
	<table>
		<tr>
			<td><input type="text" name="email" value="" /></td>
			<td><input type="submit" value="add" /></td>
		</tr>
	</table>
	</form>

	<p>
		<strong>Your subscriptions:</strong>
	</p>
	
	<c:choose>
		<c:when test="${empty me.subscriptions}">
			<p>You are not subscribed to any lists!</p>
		</c:when>
		<c:otherwise>
			<table class="sort-table" id="lists-table">
			<thead>
				<tr>
					<td>List Name</td>
					<td>List Email</td>
					<td>Role</td>
					<td>Deliver To</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="subs" items="${me.subscriptions}">
				<tr>
					<td><a href="<c:url value="${subs.url}"/>"><c:out value="${subs.name}"/></a></td>
					<td><a href="mailto:<c:out value="${subs.email}"/>"><c:out value="${subs.email}"/></a></td>
					<td><c:out value="${subs.roleName}"/></td>
					<td>
						<select>
							<c:forEach var="eml" items="${me.emailAddresses}">
								<option value="<c:out value="${eml}"/>" 
								<c:if test="${eml == subs.deliverTo}">selected="selected"</c:if>>
								<c:out value="${eml}"/>
								</option>
							</c:forEach>
						</select>
						<input type="submit" value="set" />
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>

<script type="text/javascript">
var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String", "None"]);
</script>

		</c:otherwise>
	</c:choose>
</trim:home>