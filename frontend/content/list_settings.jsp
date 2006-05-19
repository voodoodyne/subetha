<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetListSettings" />

<trim:list title="Settings" listId="${param.listId}">

	<form action="<c:url value="/list_settings_submit.jsp"/>" method="post">
		<input type="hidden" name="listId" value="${param.listId}" />
		<table>
			<tr>
				<th><label for="name">Short Name</label></th>
				<td
					<c:if test="${!empty model.errors.name}">
						class="error"
					</c:if>
				>
					<input id="name" name="name" type="text" size="60" value="${model.name}" />
					
					<c:if test="${!empty model.errors.name}">
						<p class="error"><c:out value="${model.errors.name}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="description">Description</label></th>
				<td
					<c:if test="${!empty model.errors.description}">
						class="error"
					</c:if>
				>
					<textarea id="description" name="description" rows="5" cols="60" style="width:95%"
					><c:out value="${model.description}"/></textarea>
					
					<c:if test="${!empty model.errors.description}">
						<p class="error"><c:out value="${model.errors.description}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="holdSubs">Hold Subscriptions</label></th>
				<td>
					<input id="holdSubs" name="holdSubs" type="checkbox" value="true"
						<c:if test="${model.holdSubs}">checked="checked"</c:if>
					/>
					Subscriptions must be approved by moderators
				</td>
			</tr>
			<c:if test="${auth.siteAdmin}">
				<tr class="b">
					<td colspan="2" align="center">Site Administrators Only</td>
				</tr>
				<tr class="a">
					<th><label for="email">List Address</label></th>
					<td
						<c:if test="${!empty model.errors.email}">
							class="error"
						</c:if>
					>
						<input id="email" name="email" type="text" size="60" value="${model.email}" />
						<div>Example:  announce@somedomain.com</div>
						<div id="email-error" style="color: red"></div>
	
						<c:if test="${!empty model.errors.email}">
							<p class="error"><c:out value="${model.errors.email}"/></p>
						</c:if>
					</td>
				</tr>
				<tr class="a">
					<th><label for="url">List URL</label></th>
					<td
						<c:if test="${!empty model.errors.url}">
							class="error"
						</c:if>
					>
						<input id="url" name="url" type="text" size="60" value="${model.url}" />
						<div>Example:  http://somedomain.com<strong>/se/list/</strong>announce</div>
						<div>The URL <strong>must</strong> contain /se/list/ after the domain</div>
	
						<c:if test="${!empty model.errors.url}">
							<p class="error"><c:out value="${model.errors.url}"/></p>
						</c:if>
					</td>
				</tr>
			</c:if>
		</table>
		<input type="submit" value="Save" />
	</form>

</trim:list>