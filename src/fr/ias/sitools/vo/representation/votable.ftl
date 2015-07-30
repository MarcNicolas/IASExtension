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
    <#if params?exists>
        <#list params as param>
            <#if param.DESCRIPTION?exists>
                <PARAM<#if param.id?exists> ID="${param.id}"</#if> name="${param.name}" <#if param.unit?exists> unit="${param.unit}"</#if> datatype="${param.datatype.value()}"<#if param.precision?exists> precision="${param.precision}"</#if><#if param.width?exists> ID="${param.width}"</#if><#if param.xtype?exists> xtype="${param.xtype}"</#if><#if param.ref?exists> ref="${param.ref}"</#if><#if param.ucd?exists> ucd="${param.ucd}"</#if><#if param.utype?exists> utype="${param.utype}"</#if><#if param.arraysize?exists> arraysize="${param.arraysize}"</#if> value="${param.value}">
                    <DESCRIPTION>
                    <#list param.DESCRIPTION.content as description>
                        ${description}
                    </#list>
                    </DESCRIPTION>
                <#if param.VALUES?exists>
                    <VALUES<#if param.VALUES.id?exists> ID="${param.VALUES.id}"</#if><#if param.VALUES.type?exists> type="${param.VALUES.type}"</#if><#if param.VALUES.null?exists> null="${param.VALUES.null}"</#if><#if param.VALUES.ref?exists> ref="${param.VALUES.ref}"</#if>>
                    <#if param.VALUES.OPTION?exists>
                    <#list param.VALUES.OPTION as option>
                        <OPTION<#if option.name?exists> name="${option.name}"</#if> value="${option.value}"/>
                    </#list>
                    </#if>
                    </VALUES>
                </#if>
                </PARAM>
            <#else>
                <PARAM<#if param.id?exists> ID="${param.id}"</#if> name="${param.name}" <#if param.unit?exists> unit="${param.unit}"</#if> datatype="${param.datatype.value()}"<#if param.precision?exists> precision="${param.precision}"</#if><#if param.width?exists> ID="${param.width}"</#if><#if param.xtype?exists> xtype="${param.xtype}"</#if><#if param.ref?exists> ref="${param.ref}"</#if><#if param.ucd?exists> ucd="${param.ucd}"</#if><#if param.utype?exists> utype="${param.utype}"</#if><#if param.arraysize?exists> arraysize="${param.arraysize}"</#if> value="${param.value}"/>
            </#if>
        </#list>
    </#if>
    <#if fields?exists>
        <#list fields as field> 
            <#if field.DESCRIPTION?exists>
                <FIELD<#if field.id?exists> ID="${field.id}</#if> name="${field.name}"<#if field.ucd?exists> ucd="${field.ucd}"</#if><#if field.utype?exists> utype="${field.utype}"</#if><#if field.ref?exists> ref="${field.ref}"</#if><#if field.datatype?exists> datatype="${field.datatype.value()}"</#if><#if field.width?exists> width="${field.width}"</#if><#if field.precision?exists> precision="${field.precision}"</#if><#if field.unit?exists> unit="${field.unit}"</#if><#if field.type?exists> type="${field.type}"</#if><#if field.xtype?exists> xtype="${field.xtype}"</#if><#if field.arraysize?exists> arraysize="${field.arraysize}"</#if>>
                    <DESCRIPTION>
                    <#list field.DESCRIPTION.content as description>
                        ${description}
                    </#list>
                    </DESCRIPTION>
                </FIELD>
            <#else>
                <FIELD<#if field.id?exists> ID="${field.id}</#if> name="${field.name}"<#if field.ucd?exists> ucd="${field.ucd}"</#if><#if field.utype?exists> utype="${field.utype}"</#if><#if field.ref?exists> ref="${field.ref}"</#if><#if field.datatype?exists> datatype="${field.datatype.value()}"</#if><#if field.width?exists> width="${field.width}"</#if><#if field.precision?exists> precision="${field.precision}"</#if><#if field.unit?exists> unit="${field.unit}"</#if><#if field.type?exists> type="${field.type}"</#if><#if field.xtype?exists> xtype="${field.xtype}"</#if><#if field.arraysize?exists> arraysize="${field.arraysize}"</#if> />
            </#if>
        </#list>
    </#if>
    <#if rows?exists>
    <#if nrows != 0>
    <TABLE<#if nrows?exists> nrows="${nrows}"</#if>>
    <DATA>
        <TABLEDATA>
        <#list rows as row>
        <TR>
            <#list sqlColAlias as sqlcol>
            <#if row["${sqlcol}"] == "null"><TD> <#if sqlcol?exists> header : ${sqlcol}</#if></TD><#else><TD>${row["${sqlcol}"]}</TD></#if>
            </#list>
        </TR>
        </#list>
        </TABLEDATA>
    </DATA>
    </TABLE>
    </#if>
    </#if>
    
    </RESOURCE>
</VOTABLE>