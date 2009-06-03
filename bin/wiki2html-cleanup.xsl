<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="filename"></xsl:param>
	<xsl:param name="baseurl"></xsl:param>
	
<xsl:template match="a/@href[starts-with(.,'/p/subetha/wiki/')"><xsl:value-of select="string-join(translate(.,'/p/subetha/wiki/',''),'.html')"/></xsl:template>

<xsl:template match="body"> 
<body>
	<xsl:apply-templates/>
	<div>
	`	<p><a href="$($baseurl}/${$filename}"><xsl:value-of select="$filename"/></a></p>
	</div>
</body>
</xsl:template>

</xsl:stylesheet>