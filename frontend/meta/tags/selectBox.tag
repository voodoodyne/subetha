<%@include file="./inc/taglibs.jspf" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="selected" required="true" %>

<select name="<c:out value="${name}"/>" id="<c:out value="${name}"/>">
	<c:forEach var="item" items="${items}">
		<option value="<c:out value="${item}" />"><c:out value="${item}" /></option>
	</c:forEach>
</select>
<script>
	selectItem("<c:out value="${name}"/>", "<c:out value="${selected}" />");
</script>
