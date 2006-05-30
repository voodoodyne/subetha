<%@include file="/inc/top_standard.jspf" %>

<trim:list title="Import Messages" listId="${param.listId}">

	<fieldset>
	<legend>MBox File</legend>

	<form action="<c:url value="/import_messages_submit.jsp"/>" method="post"  enctype="multipart/form-data">
		<input type="hidden" name="listId" value="${param.listId}" />
		<p>Please submit a MBOX file.</p>
		<input id="file" name="file" size="65" type="file"/>
				
		<input type="submit" value="Import" />
	</form>
	</fieldset>

</trim:list>