<%@page contentType="text/css" %>
<%@ page import='java.text.SimpleDateFormat' %>
<%@ page import='java.util.Date' %>
<%
Date fileDate = null;
try {
	SimpleDateFormat sdf = new SimpleDateFormat("y-M-d H:m:s Z");
	// FIXME: fix this to be $Date$ when jeff fixes svn. =)
	fileDate = sdf.parse("2006-03-15 02:33:03 -0500 (Wed, 15 Mar 2006)");
} catch (java.text.ParseException pe) {
	// ignore
}

//	set expires in 1 year.
java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
cal.add(java.util.GregorianCalendar.YEAR, 1);
response.setDateHeader("Expires", cal.getTimeInMillis());

long modSince = request.getDateHeader("If-Modified-Since");
if (fileDate != null && modSince > 0 && modSince < fileDate.getTime()) {
	response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	return;
}

//	 Add headers, for caching and such...
if (fileDate != null) {
	response.setDateHeader("Date", fileDate.getTime());
	response.setDateHeader("Last-Modified", fileDate.getTime());
}

//	 Set some cache control parameters to get as much cached as possible.
//	 this is for proxy caching... mostly.
response.setHeader("Cache-Control", "public, max-age=360000");
%>
<%@include file="../inc/taglibs.jspf" %>
.sort-table {
	font:		Icon;
	border:		1px Solid ThreeDShadow;
	background:	Window;
	color:		WindowText;
}

.sort-table thead {
	background:	ButtonFace;
}

.sort-table td {
	padding:	2px 5px;
}

.sort-table thead td {
	border:			1px solid;
	border-color:	ButtonHighlight ButtonShadow
					ButtonShadow ButtonHighlight;
	cursor:			default;
}

.sort-table thead td:active {
	border-color:	ButtonShadow ButtonHighlight
					ButtonHighlight ButtonShadow;
	padding:		3px 4px 1px 6px;
}

.sort-table thead td[_sortType=None]:active {
	border-color:	ButtonHighlight ButtonShadow
					ButtonShadow ButtonHighlight;
	padding:		2px 5px;
}

.sort-arrow {
	width:					11px;
	height:					11px;
	background-position:	center center;
	background-repeat:		no-repeat;
	margin:					0 2px;
}

.sort-arrow.descending {
	background-image:		url("<c:url value="/img/downsimple.png"/>");

}

.sort-arrow.ascending {
	background-image:		url("<c:url value="/img/upsimple.png"/>");
}