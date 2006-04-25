<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetFilterForEdit" />

<trim:list title="Edit Filter" listId="${model.listId}">
	<h3><c:out value="${model.filter.name}"/></h3>
	
	<p><c:out value="${model.filter.description}"/></p>

	<form action="filter_save.jsp" method="post">
		<input type="hidden" name="listId" value="${model.listId}" />
		<input type="hidden" name="className" value="${model.className}" />
		<table>
			<c:forEach var="filterParam" items="${model.filter.parameters}">
				<tr>
					<th>
						<c:out value="${filterParam.name}"/>
					</th>
					<td
						<c:if test="${!empty model.errors[filterParam.name]}">
							class="error"
						</c:if>
					>
						<c:choose>
							<c:when test="${filterParam.type.name == 'java.lang.Boolean'}">
								<%-- Booleans get checkboxes --%>
								<input type="checkbox"
										name="<c:out value="form:${filterParam.name}"/>"
										<c:if test="${model.form[filterParam.name]}">
											checked="checked"
										</c:if>
								/>
							</c:when>
							<c:when test="${!empty model.enumValues[filterParam.name]}">
								<%-- Enums will have enumValues and thus get checkboxes --%>
								<select name="<c:out value="form:${filterParam.name}"/>">
									<c:forEach var="enumValue" items="${model.enumValues[filterParam.name]}">
										<option value="<c:out value="${enumValue}"/>"
											<c:if test="${enumValue == model.form[filterParam.name]}">selected="selected"</c:if>
										><c:out value="${enumValue}"/></option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<%-- Everything else we treat as text --%>
								<input type="text"
									name="<c:out value="form:${filterParam.name}"/>"
									value="<c:out value="${model.form[filterParam.name]}"/>" />
							</c:otherwise>
						</c:choose>
						<c:if test="${!empty model.errors[filterParam.name]}">
							<p class="error"><c:out value="${model.errors[filterParam.name]}"/></p>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2"><c:out value="${filterParam.description}"/></td>
				</tr>
			</c:forEach>
		</table>
		<input type="submit" value="Save"/>
	</form>
</trim:list>