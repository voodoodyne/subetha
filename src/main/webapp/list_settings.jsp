<%@include file="/inc/top_standard.jspf" %>

<t:action type="org.subethamail.web.action.CheckPermission">
	<t:param name="perm" value="EDIT_SETTINGS" />
</t:action>

<t:action var="model" type="org.subethamail.web.action.GetListSettings" />

<c:set var="contextPath" value="${backend.contextPath}" />


<trim:list title="List Settings" listId="${param.listId}">

	<form action="<c:url value="/list_settings_submit.jsp"/>" method="post" class="form-inline">
		<input type="hidden" name="listId" value="${param.listId}" />

	<fieldset>
		<legend>General Settings</legend>
		
		<table>
			<tr>
				<th><label for="name">Short Name</label></th>
				<td
					<c:if test="${!empty model.errors.name}">
						class="error"
					</c:if>
				>
					<input id="name" name="name" type="text" size="61" value="${model.name}" />
					
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
					<textarea id="description" name="description" rows="5" cols="60"
					><c:out value="${model.description}"/></textarea>
					
					<c:if test="${!empty model.errors.description}">
						<p class="error"><c:out value="${model.errors.description}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><label for="welcomeMessage">Welcome Message</label></td>
				<td
					<c:if test="${!empty model.errors.welcomeMessage}">
						class="error"
					</c:if>
				>
					<textarea id="welcomeMessage" name="welcomeMessage" rows="5" cols="60"
					><c:out value="${model.welcomeMessage}"/></textarea>
					
					<c:if test="${!empty model.errors.welcomeMessage}">
						<p class="error"><c:out value="${model.errors.welcomeMessage}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="holdSubs">Hold Subscriptions</label></th>
				<td>
					<input id="holdSubs" name="holdSubs" type="checkbox" value="true"
						<c:if test="${model.holdSubs}">checked="checked"</c:if>
					/>
					<label for="holdSubs">Subscriptions must be approved by moderators</label>
				</td>
			</tr>
		</table>
		</fieldset>

		<c:if test="${auth.siteAdmin}">
		<fieldset>
		<legend>Site Administrators Only</legend>
		<table>
			<tr>
				<th><label for="email">List Address</label></th>
				<td
					<c:if test="${!empty model.errors.email}">
						class="error"
					</c:if>
				>
					<input id="email" name="email" type="text" size="61" value="${model.email}" />
					<div>Example:  announce@somedomain.com</div>
					<div id="email-error" style="color: red"></div>

					<c:if test="${!empty model.errors.email}">
						<p class="error"><c:out value="${model.errors.email}"/></p>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><label for="url">List URL</label></th>
				<td
					<c:if test="${!empty model.errors.url}">
						class="error"
					</c:if>
				>
					<input id="url" name="url" type="text" size="61" value="${model.url}" />
					<div>Example:  http://example.com${contextPath}announce</div>
					<div>The URL can be <strong>any</strong> url that actually resolves to the SubEtha
					server.  Avoid URLs that might conflict with normal server operation (eg, "list.jsp").</div>

					<c:if test="${!empty model.errors.url}">
						<p class="error"><c:out value="${model.errors.url}"/></p>
					</c:if>
				</td>
			</tr>
		</table>
		</fieldset>
		</c:if>

		<input type="submit" value="Save" />
	</form>
	<form action="<c:url value="/list.jsp"/>" method="post" class="form-inline">
		<input type="hidden" name="listId" value="${param.listId}" />
		<input type="submit" value="Cancel" />
	</form>
	<script type="text/javascript">
		document.getElementById('name').focus();
	</script>

</trim:list>