<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetSiteSettings" />

<trim:main title="Site Settings">
	<h1>Site Settings</h1>
	
	<form action="<c:url value="/site_settings_submit.jsp"/>" method="post" class="form-inline">
		<table class="sort-table">
			<thead>
				<tr>
					<td>Key</td>
					<td>Value</td>
					<td>Description</td>
					<td>Type</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="configData" items="${model.configData}" varStatus="loop">
					<c:choose>
						<c:when test="${loop.index % 2 == 0}">
							<c:set var="color" value="a"/>
						</c:when>
						<c:otherwise>
							<c:set var="color" value="b"/>
						</c:otherwise>
					</c:choose>
					<tr class="${color}">
						<th>
							<c:out value="${configData.id}"/>
						</th>
						<td>
							<input type="text" name="<c:out value="${configData.id}" />" 
									value="<c:out value="${configData.value}" />" size="30" />
						</td>
						<td>
							<c:out value="${configData.description}" />
						</td>
						<td>
							<c:out value="${configData.type.name}" />
						</td>
					</tr>
				
				</c:forEach>
			</tbody>
		</table>
		<input type="submit" name="submit" value="Save" />
	</form>
</trim:main>