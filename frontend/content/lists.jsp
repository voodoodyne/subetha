<%@include file="inc/top_standard.jspf" %>

<trim:admin title="All Lists">
	<h1>All Lists</h1>
	
	<p>
		This page should display a list of all the mailing lists hosted
		on this site.  Each of the lists can be clicked on and managed.
	</p>
	
	<table>
		<tr>
			<th>List</th>
			<th>URL</th>
			<th>Subscribers</th>
		</tr>
		<tr>
			<td><a href="list_admin.jsp">announce@happhour.com</a></td>
			<td><a href="http://www.happhour.com/list/announce">http://www.happhour.com/list/announce</a></td>
			<td>72</td>
		</tr>
		<tr>
			<td><a href="list_admin.jsp">goodgod@goatse.cx</a></td>
			<td><a href="http://www.blah.com/list/goodgod">http://www.blah.com/list/goodgod</a></td>
			<td>2</td>
		</tr>
		<tr>
			<td><a href="list_admin.jsp">barbarians@nethack.org</a></td>
			<td><a href="http://www.nethack.org/list/barbarians">http://www.nethack.org/list/barbarians</a></td>
			<td>9384</td>
		</tr>
	</table>

</trim:admin>