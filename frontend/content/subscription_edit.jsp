<%@include file="/inc/top_standard.jspf" %>

<t:action var="myList" type="org.subethamail.web.action.GetMyListRelationship" />
<c:set var="perms" value="${myList.perms}" />

<t:action var="model" type="org.subethamail.web.action.GetSubscriptionForEdit" />

<c:if test="${perms.EDIT_ROLES}">
	<t:action var="listRoles" type="org.subethamail.web.action.GetRoles">
		<t:param name="listId" value="${param.listId}"/>
	</t:action>
</c:if>

<trim:list title="Edit Subscription" listId="${param.listId}">

		<fieldset>
			<legend>Subscriber Information</legend>
			<form action="<c:url value="/subscription_submit.jsp"/>" method="post" class="form-inline">
				<input type="hidden" name="personId" value="${param.personId}" />
				<input type="hidden" name="listId" value="${param.listId}" />
		
				<table class="sideHeader">
					<tr>
						<th>Name</th>
						<td>
							<c:out value="${model.data.name}" />
						</td>
					</tr>
					<tr>
						<th>Deliver To</th>
						<td>
							<c:choose>
								<c:when test="${perms.EDIT_SUBSCRIPTIONS}">
									<select name="deliverTo">
										<option value="">Disable Delivery</option>
										<c:forEach var="eml" items="${model.data.emailAddresses}">
											<option value="<c:out value="${eml}"/>" 
												<c:if test="${eml == model.deliverTo}">selected="selected"</c:if>
											>
												<c:out value="${eml}"/>
											</option>
										</c:forEach>
									</select>
								</c:when>
								<c:otherwise>
									<c:out value="${model.deliverTo}" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<c:if test="${perms.VIEW_ROLES}">
						<tr>
							<th>Role</th>
							<td>
								<c:choose>
									<c:when test="${perms.EDIT_ROLES}">
										<select name="roleId">
											<c:forEach var="role" items="${listRoles.roles}" varStatus="loop">
												<option value="${role.id}"
													<c:if test="${role.id == model.roleId}">selected="selected"</c:if>
												><c:out value="${role.name}"/></option>
											</c:forEach>
										</select>
									</c:when>
									<c:otherwise>
										<c:out value="${model.data.role.name}" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
					<c:if test="${perms.VIEW_NOTES}">
						<tr>
							<th>Note</th>
							<td
								<c:if test="${!empty model.errors.note}">class="error"</c:if>
							>
								<c:choose>
									<c:when test="${perms.EDIT_NOTES}">
										<textarea id="note" name="note" rows="5" cols="60" style="width:95%"
										><c:out value="${model.note}" /></textarea> 
									
										<c:if test="${!empty model.errors.note}">
											<p class="error"><c:out value="${model.errors.note}" /></p>
										</c:if>
									</c:when>
									<c:otherwise>
										<div class="note">
											${f:escapeText(model.data.note)}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</table>
				<input type="submit" value="Save" />
			</form>
			<form action="<c:url value="/list_subscribers.jsp"/>" method="post" class="form-inline">
				<input type="hidden" name="listId" value="${param.listId}" />
				<input type="submit" value="Cancel" />
			</form>

		</fieldset>

</trim:list>