<?xml version="1.0" ?>
<VOTABLE version="1.2" xmlns="http://www.ivoa.net/xml/VOTable/v1.2">
    <RESOURCE type="results">
    <#if description?exists><DESCRIPTION>${description}</DESCRIPTION></#if>
    <#if queryInfos?exists>
        <#list queryInfos as queryInfo>
            <INFO<#if queryInfo.id?exists> ID="${queryInfo.id}"</#if> name="${queryInfo.name}" value="${queryInfo.valueAttribute}"<#if queryInfo.xtype?exists> xtype="${queryInfo.xtype}"</#if><#if queryInfo.unit?exists> unit="${queryInfo.unit}"</#if><#if queryInfo.ucd?exists> ucd="${queryInfo.ucd}"</#if><#if queryInfo.utype?exists> utype="${queryInfo.utype}"</#if> />
        </#list>
    </#if>
    <#if queryParams?exists>
        <#list queryParams as queryParam>
            <PARAM<#if queryParam.id?exists> ID="${queryParam.id}"</#if> name="${queryParam.name}" datatype="${queryParam.datatype.value()}" <#if queryParam.xtype?exists> xtype="${queryParam.xtype}"</#if><#if queryParam.unit?exists> unit="${queryParam.unit}"</#if><#if queryParam.ucd?exists> ucd="${queryParam.ucd}"</#if><#if queryParam.utype?exists> utype="${queryParam.utype}"</#if> value="${queryParam.value}"/>
        </#list>
    </#if>
    <#if infos?exists>
        <#list infos as info>
            <INFO<#if info.id?exists> ID="${info.id}"</#if> name="${info.name}" value="${info.valueAttribute}"<#if info.xtype?exists> xtype="${info.xtype}"</#if><#if info.ref?exists> ref="${info.ref}"</#if><#if info.unit?exists> unit="${info.unit}"</#if><#if info.ucd?exists> ucd="${info.ucd}"</#if><#if info.utype?exists> utype="${info.utype}"</#if> />
        </#list>
    </#if>
    
    <#if fields?exists>
    <TABLE<#if nrows?exists> nrows="${nrows}"</#if>>
    </#if>
    <#if fields?exists>
        <#list fields as field> 
            <#if field.DESCRIPTION?exists>
                <FIELD<#if field.id?exists> ID="${field.id}</#if> name="${field.name}"<#if field.ucd?exists> ucd="${field.ucd}"</#if><#if field.utype?exists> utype="${field.utype}"</#if><#if field.ref?exists> ref="${field.ref}"</#if> datatype="${field.datatype.value()}"<#if field.width?exists> width="${field.width}"</#if><#if field.precision?exists> precision="${field.precision}"</#if><#if field.unit?exists> unit="${field.unit}"</#if><#if field.type?exists> type="${field.type}"</#if><#if field.xtype?exists> xtype="${field.xtype}"</#if><#if field.arraysize?exists> arraysize="${field.arraysize}"</#if>>
                    <DESCRIPTION>
                    <#list field.DESCRIPTION.content as description>
                        ${description}
                    </#list>
                    </DESCRIPTION>
                </FIELD>
            <#else>
                <FIELD<#if field.id?exists> ID="${field.id}</#if> name="${field.name}"<#if field.ucd?exists> ucd="${field.ucd}"</#if><#if field.utype?exists> utype="${field.utype}"</#if><#if field.ref?exists> ref="${field.ref}"</#if> datatype="${field.datatype.value()}"<#if field.width?exists> width="${field.width}"</#if><#if field.precision?exists> precision="${field.precision}"</#if><#if field.unit?exists> unit="${field.unit}"</#if><#if field.type?exists> type="${field.type}"</#if><#if field.xtype?exists> xtype="${field.xtype}"</#if><#if field.arraysize?exists> arraysize="${field.arraysize}"</#if> />
            </#if>
        </#list>
    </#if>
    <#if rows?exists>
    <#if nrows != 0>
    <DATA>
        <TABLEDATA>
        <#list rows as row>
        <TR>
            <#list sqlColAlias as sqlcol>
            <#if row["${sqlcol}"] == "null"><TD/><#else>${row["${sqlcol}"]}</TD></#if>
            </#list>
        </TR>
        </#list>
        </TABLEDATA>
    </DATA>
    </#if>
    </#if>
    <#if fields?exists>
    </TABLE>
    </#if>
    </RESOURCE>
</VOTABLE>