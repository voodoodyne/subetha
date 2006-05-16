<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.auth.AuthRequired" />

<c:set var="me" value="${backend.accountMgr.self}"/>

<c:choose>
	<c:when test="${empty model.errors.name}">	<%-- First time visiting, or possibly a pw set error --%>
		<c:set var="myName" value="${backend.accountMgr.self.name}"/>
	</c:when>
	<c:otherwise>
		<c:set var="myName" value="${model.name}"/>
	</c:otherwise>
</c:choose>

<trim:home title="My Profile">

	<h1>My Profile</h1>

	<form action="<c:url value="/user_changename.jsp"/>" method="post">
		<table>
			<tr>
				<td><strong>Name</strong></td>
				<td
					<c:if test="${!empty model.errors.name}">
						class="error"
					</c:if>			
				>
					<input type="text" name="name" value="<c:out value="${myName}"/>">
					
					<c:if test="${!empty model.errors.name}">
						<p class="error"><c:out value="${model.errors.name}"/></p>
					</c:if>
				</td>
				<td><input type="submit" value="Save" /></td>
			</tr>
		</table>
	</form>
	
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
	<script type="text/javascript">
	var st = new SortableTable(document.getElementById("emails-table"), ["None", "None"]);
	st.onsort = st.tableRowColors;
	</script>

</trim:home>
