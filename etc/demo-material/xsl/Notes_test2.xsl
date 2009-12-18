<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:tei="http://www.tei-c.org/ns/1.0"
version="1.0">

<xsl:output encoding="UTF-8"
            method="xml"
            indent="yes"
            doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>


<xsl:template match="notecoll">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/xml;charset=utf-8" />
    <title>Nootit</title>
  </head>
<body>
	<h1>Huomautuksia teokseen <xsl:value-of select="./@source"/></h1>
	<xsl:apply-templates/>
</body>
</html>
</xsl:template>


<xsl:template match="notes">
	<h1><xsl:value-of select="./@type"/>huomautuksia</h1>
  <div> 
	<xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="note">
  <xsl:variable name="name"> 
    <xsl:value-of select="./@xml:id"/>
  </xsl:variable>
    <a name="{$name}"></a><p> 
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="lemma"> 
  <xsl:variable name="href"> 
    <xsl:value-of select="../@xml:id"/>
  </xsl:variable>
	<a href="nummisuutarit_demo2.xml#{$href}" target="r1"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="text">
	; <xsl:apply-templates/>
</xsl:template>

<xsl:template match="baseform">
	; [<xsl:apply-templates/>]
</xsl:template>

<xsl:template match="meaning">
	<i>; '<xsl:apply-templates/>'</i>
</xsl:template>

<xsl:template match="subsource">
	; <xsl:apply-templates/><br/>
</xsl:template>

<xsl:template match="citation">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="description">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="source">
	; <xsl:apply-templates/>
</xsl:template>

<xsl:template match="classification">
	<br/><xsl:apply-templates/>
</xsl:template>

<xsl:template match="edited">
	<br/><span style="color:gray"><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="phase">
	; <xsl:apply-templates/>
</xsl:template>

<xsl:template match="comment">
	<br/><xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
	<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
