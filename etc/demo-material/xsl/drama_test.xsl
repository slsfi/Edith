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


<xsl:template match="tei:TEI">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/xml;charset=utf-8" />
    <title>Testi</title>
  </head>
<body>
	<xsl:apply-templates/>
</body>
</html>
</xsl:template>


<xsl:template match="tei:teiHeader">
  <p> 
	<!--xsl:apply-templates/-->
  </p>
</xsl:template>


<xsl:template match="tei:byline">
  <p> 
	<xsl:apply-templates/>
  </p>
</xsl:template>


<xsl:template match="tei:docAuthor">
: <i><xsl:apply-templates/></i>

</xsl:template>


<!-- CAST -->

 <xsl:template match="tei:castGroup">
    <ul>
      <xsl:apply-templates />
    </ul>
  </xsl:template>

  <xsl:template match="tei:castItem">
    <li>
      <xsl:apply-templates />
    </li>
  </xsl:template>

  <xsl:template match="tei:castList">
    <xsl:if test="tei:head">
      <p>
        <b>
          <xsl:for-each select="tei:head">
            <xsl:apply-templates />
          </xsl:for-each>
        </b>
      </p>
    </xsl:if>
    <ul>
      <xsl:apply-templates />
    </ul>
  </xsl:template>


  <xsl:template match="tei:castList/tei:head" />


<!-- /CAST -->

 
<xsl:template match="tei:head">
  <h2>
	<xsl:apply-templates/>
  </h2>
</xsl:template>
 
<xsl:template match="tei:speaker">
	<i><xsl:apply-templates/>: </i>
</xsl:template>
 

<xsl:template match="tei:sp">
  <xsl:variable name="id"> 
    <xsl:value-of select="./@xml:id"/>
  </xsl:variable>
<p><a name="{$id}"/>
	<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="tei:p">
	<xsl:apply-templates/>
</xsl:template>

<!-- REF -->

<xsl:template match="tei:ref">
  <xsl:variable name="href"> 
    <xsl:value-of select="./@target"/>
  </xsl:variable>
  <xsl:variable name="name"> 
    <xsl:value-of select="./@xml:id"/>
  </xsl:variable>
	<a name="{$name}" href="nootit.xml#{$href}" target="r2"><xsl:apply-templates/></a>
</xsl:template>

<!-- /REF -->

<!-- PAGEBREAK -->

<xsl:template match="tei:pb">
  <xsl:variable name="facs"> 
    <xsl:value-of select="./@facs"/>
  </xsl:variable>
  <xsl:variable name="n"> 
    <xsl:value-of select="./@n"/>
  </xsl:variable>
	<hr/><center><a name="sivu_{$n}">sivu <xsl:value-of  select="./@n"/></a> -  <a name="sivu_{$n}" href="{$facs}" target="r2">Faksimileen</a></center><br/>
</xsl:template>

<!-- /PAGEBREAK -->

<!-- CORR -->

<xsl:template match="tei:choice">
  <xsl:variable name="id">
    <xsl:value-of select="./@id"/>
  </xsl:variable>
  <xsl:variable name="resp"> 
    <xsl:value-of select="tei:corr/@resp"/>
  </xsl:variable>
  <xsl:variable name="source"> 
    <xsl:value-of select="tei:corr/@source"/>
  </xsl:variable>

<span type="corr" title="korjaus: {$resp} {$source}">
<small>[<s><xsl:value-of select="tei:sic"/></s>]</small>
<a name="{$id}"><xsl:value-of select="tei:corr"/></a></span> 

</xsl:template>

<xsl:template match="tei:app">
<!--xsl:value-of select="tei:lem"/-->
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="tei:rdg">
  <xsl:variable name="wit">
    <xsl:value-of select="./@wit"/>
  </xsl:variable>

 <small> <span style="background-color:gray"> [<xsl:value-of select="./@wit"/>: <xsl:apply-templates/>]</span></small>

</xsl:template>

<!-- /CORR -->

<!-- MISC -->

<xsl:template match="tei:gap/tei:desc">
	<font color="gray"> <xsl:apply-templates/> </font>
</xsl:template>

<!-- /MISC -->

<xsl:template match="*">
	<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
