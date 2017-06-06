<?xml version="1.0" encoding="UTF-8" ?>

<!--    Document   : ResourceExtractor.xsl    Created on : November 8, 2004,
5:42 PM    Author     : Aurelien STEBE    Description:        Extract the
Title and AccessURL nodes from        a VOResource XML input and outputs it as
a VOTABLE.-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
                xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
                xmlns:vot="http://www.ivoa.net/xml/VOTable/v1.1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                exclude-result-prefixes="vr ri vot">

    <xsl:output method="xml" indent="yes"
                doctype-system="http://us-vo.org/xml/VOTable.dtd"/>

    <xsl:param name="serviceType"/>

    <!-- The main VOTABLE structure -->
    <xsl:template match="/">
        <VOTABLE version="1.0">
            <RESOURCE type="results">
                <DESCRIPTION>Simple Spectrum Access Services</DESCRIPTION>
                <INFO name="QUERY_STATUS" value="OK"/>
                <TABLE>
                    <FIELD ID="Title" arraysize="*" datatype="char" ucd="VOX:Image_Title"/>
                    <FIELD ID="URL" arraysize="*" datatype="char" ucd="DATA_LINK"/>
                    <FIELD ID="Type" arraysize="*" datatype="char" ucd="TYPE"/>
                    <DATA>
                        <TABLEDATA>
                            <xsl:apply-templates/>
                        </TABLEDATA>
                    </DATA>
                </TABLE>
            </RESOURCE>
        </VOTABLE>
    </xsl:template>

    <!-- The v1.0 Resource matching template -->
    <xsl:template match="ri:Resource">
        <xsl:if test="capability[substring-after(@xsi:type, ':') = $serviceType]/interface[substring-after(@xsi:type, ':') = 'ParamHTTP']/accessURL != ''">
            <TR>
                <TD>
                    <xsl:value-of select="normalize-space(title)"/>
                </TD>
                <TD>
                    <xsl:choose>
                        <xsl:when test="contains(capability[substring-after(@xsi:type, ':') = $serviceType]/interface[substring-after(@xsi:type, ':') = 'ParamHTTP']/accessURL, '?')">
                            <xsl:value-of
select="normalize-space(capability[substring-after(@xsi:type, ':') = $serviceType]/interface[substring-after(@xsi:type, ':') = 'ParamHTTP']/accessURL)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of
select="concat(normalize-space(capability[substring-after(@xsi:type, ':') = $serviceType]/interface[substring-after(@xsi:type, ':') = 'ParamHTTP']/accessURL), '?')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </TD>
                <TD>
                    <xsl:value-of select="normalize-space(content/type)"/>
                </TD>
            </TR>
        </xsl:if>
    </xsl:template>

    <!-- The VOT Resource matching template -->
    <xsl:template match="vot:RESOURCE">
        <xsl:for-each select="vot:TABLE/vot:DATA/vot:TABLEDATA/vot:TR">
            <xsl:if test="vot:TD[count(../../../vot:FIELD[@ID = 'accessURL']/preceding-sibling::*)+1] != ''">
                <TR>
                    <TD>
                        <xsl:value-of select="normalize-space(vot:TD[count(../../../vot:FIELD[@ID = 'title']/preceding-sibling::*)+1])"/>
                    </TD>
                    <TD>
                        <xsl:choose>
                            <xsl:when test="contains(vot:TD[count(../../../vot:FIELD[@ID = 'accessURL']/preceding-sibling::*)+1], '?')">
                                <xsl:value-of
select="normalize-space(vot:TD[count(../../../vot:FIELD[@ID = 'accessURL']/preceding-sibling::*)+1])"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of
select="concat(normalize-space(vot:TD[count(../../../vot:FIELD[@ID = 'accessURL']/preceding-sibling::*)+1]), '?')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </TD>
                    <TD>
                        <xsl:value-of select="normalize-space(vot:TD[count(../../../vot:FIELD[@ID = 'type']/preceding-sibling::*)+1])"/>
                    </TD>
                </TR>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- The text node NULL template -->
    <xsl:template match="text()"/>

</xsl:stylesheet>