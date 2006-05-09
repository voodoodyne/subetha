<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="me" value="${backend.accountMgr.self}"/>

<trim:home title="SubEtha Mail">
	<h1>Welcome, <c:out value="${me.name}"/></h1>
	
	<p>
		<strong>Your email addresses:</strong>
	</p>

	
	<table class="sort-table" id="emails-table">
		<thead>
			<tr>
				<td>Email</td>
				<td>Action</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="email" items="${me.emailAddresses}" varStatus="loop">
				<c:choose>
					<c:when test="${loop.index % 2 == 0}">
						<c:set var="color" value="a"/>
					</c:when>
					<c:otherwise>
						<c:set var="color" value="b"/>
					</c:otherwise>
				</c:choose>
				<tr class="${color}">
					<td><a href="mailto:<c:out value="${email}"/>"><c:out value="${email}"/></a></td>
					<td>
						<c:choose>
							<c:when test="${auth.authName != email}">
								<form action="<c:url value="/email_remove.jsp"/>" method="post">
									<input type="hidden" name="email" value="<c:out value="${email}"/>" />
									<input type="submit" value="Remove" style="width: 5em" />
								</form>
							</c:when>
							<c:otherwise>
								Logged In
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
			<form action="<c:url value="/email_add.jsp"/>" method="post">
				<tr class="a">
					<td><input type="text" name="email" value="" /></td>
					<td><input type="submit" value="Add" style="width: 5em" /></td>
				</tr>
				<c:if test="${!empty model.errors.email}">
				<tr>
					<td><p class="error"><c:out value="${model.errors.email}"/></p></td>
				</tr>
				</c:if>
			</form>
		</tbody>
	</table>

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
					<td>Unsubscribe</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="subs" items="${me.subscriptions}" varStatus="loop">
				<c:choose>
					<c:when test="${loop.index % 2 == 0}">
						<c:set var="color" value="a"/>
					</c:when>
					<c:otherwise>
						<c:set var="color" value="b"/>
					</c:otherwise>
				</c:choose>
				<tr class="${color}">
					<td><a href="<c:url value="${subs.url}"/>"><c:out value="${subs.name}"/></a></td>
					<td><a href="mailto:<c:out value="${subs.email}"/>"><c:out value="${subs.email}"/></a></td>
					<td><c:out value="${subs.roleName}"/></td>
					<td>
					<form action="<c:url value="/subscribe_me.jsp"/>" method="post">
						<input type="hidden" name="listId" value="<c:out value="${subs.id}"/>" />
						<select name="deliverTo">
							<option value="">Disable Delivery</option>
							<c:forEach var="eml" items="${me.emailAddresses}">
								<option value="<c:out value="${eml}"/>" 
								<c:if test="${eml == subs.deliverTo}">selected="selected"</c:if>>
								<c:out value="${eml}"/>
								</option>
							</c:forEach>
						</select>
						<input type="submit" value="Set" />
					</form>
					</td>
					<td>
					<form action="<c:url value="/unsubscribe_me.jsp"/>" method="post">
						<input type="hidden" name="listId" value="<c:out value="${subs.id}"/>" />
						<input type="submit" value="Unsubscribe" onclick="return confirm('Are you sure you want to unsubscribe from this list?');" />
					</form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>

<script type="text/javascript">
var st = new SortableTable(document.getElementById("emails-table"), ["None", "None"]);
st.onsort = st.tableRowColors;

var st1 = new SortableTable(document.getElementById("lists-table"), ["String", "String", "String", "None", "None"]);
st1.onsort = st1.tableRowColors;
</script>

		</c:otherwise>
	</c:choose>
</trim:home>