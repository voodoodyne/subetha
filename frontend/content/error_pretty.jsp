<%@page isErrorPage="true" %>
<%@include file="/inc/top_standard.jspf" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%

Throwable t = (Throwable) exception;
java.util.ArrayList exceptions = new java.util.ArrayList();
String output = "";
if(t!=null){
	do{		
		if(exceptions.contains(t)) exceptions.remove(t);
		
		exceptions.add(t);
		
		if (t instanceof ServletException)
			t = ((ServletException)t).getRootCause();
		else if (t instanceof JspException)
			t = ((JspException)t).getRootCause();
		else if (t instanceof org.apache.jasper.JasperException)
			t = ((org.apache.jasper.JasperException)t).getRootCause();
		else
			t = t.getCause();
		
	}while (t != null);	
}

java.util.Collections.reverse(exceptions);
request.setAttribute("exceptions", exceptions);


%>

<trim:plain title="Ooopppsss....">

	<h1>Exception Info:</h1>
	
	<div class="exceptions">
		<c:forEach var="ex" items="${exceptions}" varStatus="Loop">
			<div id="ex${Loop}">
				<h1><c:out value="${ex.message}"/> </h1>
				<br/>
				<span style="text-size:smaller">
					<c:forEach var="stackElement" items="${ex.stackTrace}" >
						<c:out value="${stackElement}"/>
					</c:forEach>
				<span>
			</div>
		</c:forEach>
	</div>
</trim:plain>
