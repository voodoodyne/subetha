<%@include file="/inc/top_standard.jspf" %>

<t:action var="filters" type="org.subethamail.web.action.GetFilters" />

<trim:list title="Filters" listId="${param.listId}">
	<h3>Available Filters</h3>
	
	<table>
		<c:forEach var="filter" items="${filters.available}">
			<tr>
				<td>
					<form action="<c:url value="/filter_edit.jsp"/>" method="get">
						<input type="hidden" name="listId" value="${param.listId}" />
						<input type="hidden" name="className" value="${filter.className}" />
						<input type="submit" value="Enable" />
					</form>
				</td>
				<td><strong><c:out value="${filter.name}"/></strong></td>
				<td><c:out value="${filter.description}"/></td>
			</tr>
		</c:forEach>
	</table>
	
	
	<h3>Enabled Filters</h3>

	<c:if test="${empty filters.enabled}">
		<p>
			No filters have been enabled yet. 
		</p>
	</c:if>
	
	<c:if test="${! empty filters.enabled}">

		<table class="filters">
			<tr>
				<th>Action</th>
				<th>Filter</th>
				<th>Parameters</th>
			</tr>
			<c:forEach var="filter" items="${filters.enabled}" varStatus="loop">
				<c:choose>
					<c:when test="${loop.index % 2 == 0}">
						<c:set var="color" value="a"/>
					</c:when>
					<c:otherwise>
						<c:set var="color" value="b"/>
					</c:otherwise>
				</c:choose>
				<tr class="${color}">
					<td>
						<form action="<c:url value="/filter_edit.jsp"/>" method="get">
							<input type="hidden" name="listId" value="${param.listId}" />
							<input type="hidden" name="className" value="${filter.className}" />
							<input type="submit" value="Edit" style="width:5em" />
						</form>
						<form action="<c:url value="/filter_delete.jsp"/>" method="get">
							<input type="hidden" name="listId" value="${param.listId}" />
							<input type="hidden" name="className" value="${filter.className}" />
							<input type="submit" value="Disable" style="width:5em" onclick="return confirm('Are you sure you want to disable this filter?');" />
						</form>
					</td>
					<td>
						<div><strong><c:out value="${filter.name}"/></strong></div>
						<div><c:out value="${filter.description}"/></div>
					</td>
					<td>
						<table border="1">
							<c:forEach var="argEntry" items="${filter.arguments}">
								<tr>
									<th align="left" valign="top"><c:out value="${argEntry.key}"/></th>
									<td align="left" valign="top"><pre><c:out value="${argEntry.value}"/></pre></td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:forEach>
		</table>

	</c:if>

</trim:list>