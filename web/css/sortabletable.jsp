<%@ page contentType="text/css" %>
<%! static String fileDateStr = "$Date: 2006-05-04 13:58:25 -0700 (Thu, 04 May 2006) $"; %>
<%@ include file="../inc/check_date.jspf" %>
<%@ include file="../inc/taglibs.jspf" %>

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