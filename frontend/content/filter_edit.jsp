<%@include file="/inc/top_standard.jspf" %>

<t:action var="model" type="org.subethamail.web.action.GetFilterForEdit" />

<trim:list title="Edit Filter" listId="${model.listId}">
	<h3><c:out value="${model.filter.name}"/></h3>
	
	<p><c:out value="${model.filter.description}"/></p>

	<form action="<c:url value="/filter_save.jsp"/>" method="post" class="form-inline">
		<input type="hidden" name="listId" value="${model.listId}" />
		<input type="hidden" name="className" value="${model.className}" />
		<table border="1">
			<c:forEach var="filterParam" items="${model.filter.parameters}" varStatus="loop">
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
										value="true"
										<c:if test="${model.form[filterParam.name]}">
											checked="checked"
										</c:if>
								/>
							</c:when>
							<c:when test="${filterParam.type.name == 'java.lang.Character'}">
								<%-- Character gets a input text size=3 maxlength=1 --%>
								<input type="text"
									name="<c:out value="form:${filterParam.name}"/>"
									value="<c:out value="${model.form[filterParam.name]}"/>"
									size="3" maxlength="1" />
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
							<c:when test="${filterParam.textLines > 1}">
								<%-- Treat as textarea --%>
								<textarea 
									name="<c:out value="form:${filterParam.name}"/>" 
									rows="${filterParam.textLines}"
									cols="60"
								><c:out value="${model.form[filterParam.name]}"/></textarea>
							</c:when>
							<c:otherwise>
								<%-- Everything else we treat as one-line text --%>
								<input type="text"
									name="<c:out value="form:${filterParam.name}"/>"
									value="<c:out value="${model.form[filterParam.name]}"/>"
									size="45" />
							</c:otherwise>
						</c:choose>
						<c:if test="${!empty model.errors[filterParam.name]}">
							<p class="error"><c:out value="${model.errors[filterParam.name]}"/></p>
						</c:if>
						<br />
						<c:out value="${filterParam.description}"/>

						<c:if test="${filterParam.expanded}">
							<br /><br />
							<table class="sort-table" id="lists-table">
								<thead>
									<tr>
										<td>Variable</td>
										<td>Description</td>
									</tr>
								</thead>
								<c:forEach var="docs" items="${filterParam.documentation}" varStatus="loop">
									<c:choose>
										<c:when test="${loop.index % 2 == 0}">
											<c:set var="color" value="a"/>
										</c:when>
										<c:otherwise>
											<c:set var="color" value="b"/>
										</c:otherwise>
									</c:choose>
									<tr class="${color}">
										<th><c:out value="${docs.key}"/></th>
										<td><c:out value="${docs.value}"/></td>
									</tr>			
								</c:forEach>			
							</table>
						</c:if>

					</td>
				</tr>
			</c:forEach>
		</table>
		<input type="submit" value="Save"/>
	</form>
	<form action="<c:url value="/filters.jsp"/>" method="post" class="form-inline">
		<input type="hidden" name="listId" value="${model.listId}" />
		<input type="submit" value="Cancel"/>
	</form>
	
</trim:list>