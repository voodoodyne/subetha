<?xml version="1.0" encoding="Windows-1251"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
<xsl:output method="text" encoding="UTF-8"/>
<xsl:template match="/">
        <xsl:apply-templates/> 
</xsl:template>

<xsl:template match="a">
  <xsl:value-of select="@href" />
</xsl:template>

<xsl:template match="h1">
  = <xsl:apply-templates /> =
</xsl:template>

<xsl:template match="h2">
  == <xsl:apply-templates /> ==
</xsl:template>

<xsl:template match="h3">
  == <xsl:apply-templates /> ==
</xsl:template>

<xsl:template match="h4">
  === <xsl:apply-templates /> ===
</xsl:template>

<xsl:template match="p">
        <xsl:text xml:space="preserve">
</xsl:text>
        <xsl:apply-templates/>  
        <xsl:text xml:space="preserve">
</xsl:text>
</xsl:template>

<xsl:template match="u"> __<xsl:apply-templates/>__</xsl:template>

<xsl:template match="em"> *<xsl:apply-templates/>*</xsl:template>

<xsl:template match="pre[parent::blockquote]">
{{{
<xsl:apply-templates/>
}}}
</xsl:template>

<xsl:template match="pre">
{{{
<xsl:apply-templates/>
}}}
</xsl:template>


<xsl:template match="li[parent::ul]">
<xsl:text disable-output-escaping="yes"> 
    * </xsl:text> <xsl:apply-templates/>
</xsl:template>

<xsl:template match="li[parent::ol]">
<xsl:text xml:space="preserve">
    </xsl:text>
<xsl:value-of select="position()"/>.<xsl:text xml:space="preserve"> </xsl:text>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="dt[parent::dl]">
<xsl:text disable-output-escaping="yes"> 
    * </xsl:text> <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dd[parent::dl]">
<xsl:text xml:space="preserve">
    </xsl:text>
<xsl:value-of select="position()"/>.<xsl:text xml:space="preserve"> </xsl:text>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
	<xsl:text xml:space="preserve"> </xsl:text><xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
