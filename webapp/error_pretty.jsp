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
		else
			t = t.getCause();
		
	}while (t != null);	
}

//java.util.Collections.reverse(exceptions);
request.setAttribute("exceptions", exceptions);


%>

<trim:plain title="We've been bad...">

	<center>
	<img src="<c:url value="/img/nurse.jpg" />" />
	<p><small>Images &copy; <a href="http://www.JulianCash.com/photo_use.html">Julian Cash</A> 2003</small></p>
	</center>	

	<h1>Exception Info:</h1>
	
	<div class="exceptions">
		<c:forEach var="ex" items="${exceptions}">
			<div class="exception">
				<h2><c:out value="${ex}"/> </h2>
				
				<div class="stackElements">
					<div class="stackElement">
<pre><c:forEach var="stackElement" items="${ex.stackTrace}" >    <c:out value="${stackElement}"/>
</c:forEach></pre>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</trim:plain>
