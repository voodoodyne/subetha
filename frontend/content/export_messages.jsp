<%@include file="/inc/top_standard.jspf" %>

<t:action var="list" type="org.subethamail.web.action.GetList" />

<trim:list title="Export Messages" listId="${list.id}">

	<c:url var="exportMboxUrl" value="/export/${list.id}/MBOX/${list.name}.mbox"/>		
	<c:url var="exportRfc2822Url" value="/export/${list.id}/${list.name}.zip"/>		

	<fieldset>
		<legend>Export Messages</legend>
		<p> Download in format: 
			<ul>
				<li><a href="${exportRfc2822Url}">RFC2822 Zip</a></li>
				<li><a href="${exportMboxUrl}">MBOX File</a></li>
			</ul>
		</p>
	</fieldset>

</trim:list>