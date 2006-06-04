<%@include file="/inc/taglibs.jspf" %>

<trim:headerless title="SubEtha Version Information">

	<table>
		<tr>
			<th>Package</th>
			<th>Version</th>
			<th>Implementation</th>
		</tr>
		<c:forEach var="pkg" items="${backend.versions}">
			<tr>
				<td><c:out value="${pkg.name}"/></td>
				<td><c:out value="${pkg.specificationVersion}"/></td>
				<td><c:out value="${pkg.implementationVersion}"/></td>
			</tr>
		</c:forEach>
	</table>
	
</trim:headerless>
