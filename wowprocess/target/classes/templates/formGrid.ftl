<div class="form-cell" ${elementMetaData!}>

<link href="${request.contextPath}/js/boxy/stylesheets/boxy.css" rel="stylesheet" type="text/css" />
<script type='text/javascript' src='${request.contextPath}/js/boxy/javascripts/jquery.boxy.js'></script>
<script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/js/jquery.enterpriseformgrid.js"></script>
<script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/js/date.js"></script>

<style type="text/css">
    .grid table {
        width: 100%;
    }
    .grid th, .grid td {
        border: solid 1px silver;
        margin: 0px;
    }
    .grid-cell-options {
        width: 10px;
    }
    .grid-row-template {
        display: none;
    }
    .grid-cell input:focus {
        background: #efefef;
        border: 1px solid #a1a1a1;
    }
    .grid-action-edit,
    .grid-action-delete,
    .grid-action-moveup,
    .grid-action-movedown,
    .grid-action-add{
        display:inline-block;
        height:16px;
        width:16px;
    }
    .grid-action-delete{
        background: url(${request.contextPath}/images/v3/property_editor/delete.png) no-repeat;
    }
    .grid-action-moveup{
        display:none;
        background: url(${request.contextPath}/images/v3/property_editor/up.png) no-repeat;
    }
    .grid-action-movedown{
        display:none;
        background: url(${request.contextPath}/images/v3/property_editor/down.png) no-repeat;
    }
    .grid-action-add{
        margin-top:3px;
        background: url(${request.contextPath}/images/v3/property_editor/add.png) no-repeat;
    }
    .grid-action-edit{
        background: url(${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/images/edit.png) no-repeat;
    }
    .grid-action-moveup.disabled{
        background: url(${request.contextPath}/images/v3/property_editor/up_d.png) no-repeat;
    }
    .grid-action-movedown.disabled{
        background: url(${request.contextPath}/images/v3/property_editor/down_d.png) no-repeat;
    }
    .grid-action-edit span,
    .grid-action-delete span,
    .grid-action-moveup span,
    .grid-action-movedown span,
    .grid-action-add span{
        display:none;
    }
    .grid.enableSorting a.grid-action-moveup,
    .grid.enableSorting a.grid-action-movedown{
        display:inline-block;
    }
    .grid.readonly.enableSorting a.grid-action-moveup,
    .grid.readonly.enableSorting a.grid-action-movedown,
    .grid.readonly a.grid-action-delete,
    .grid.readonly a.grid-action-add,
    .grid.disabledAdd a.grid-action-add,
    .grid.disabledDelete a.grid-action-delete{
        display:none;
    }
    .grid.readonly a.grid-action-edit{
        display:inline-block;
        background: url(${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/images/view.png) no-repeat;
    }
</style>
<script type="text/javascript">
    $(document).ready(function() {
        $("#${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}").enterpriseformgrid();
        $("#${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}").enterpriseformgrid("initPopupDialog", {contextPath:'${request.contextPath}', title:'@@form.formgrid.addEntry@@'});
    });

    function ${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}_add(args){
        $("#${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}").enterpriseformgrid("addRow", args);
    }

    function ${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}_edit(args){
        $("#${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}").enterpriseformgrid("editRow", args);
    }
</script>

    <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}${customDecorator}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <div class="form-clear"></div>
    <div id="${elementParamName!}_formgrid_${element.properties.elementUniqueKey!}" name="${elementParamName!}" class="grid form-element <#if element.properties.readonly! == 'true'>readonly</#if> <#if element.properties.enableSorting! == 'true'>enableSorting</#if> <#if element.properties.disabledAdd! == 'true'>disabledAdd</#if> <#if element.properties.disabledDelete! == 'true'>disabledDelete</#if>">
        <input type="hidden" id="formUrl" value="${request.contextPath}/web/app/${appId}/${appVersion}/form/embed?_submitButtonLabel=${buttonLabel!}">
        <input type="hidden" id="json" value="${json!}">
        <input type="hidden" id="height" value="${element.properties.height!}">
        <input type="hidden" id="width" value="${element.properties.width!}">
        <table cellspacing="0" style="width:100%;">
            <tbody>
            <tr>
            <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                <th></th>
            </#if>
            <#list headers?keys as header>
                <#assign width = "">
                <#if headers[header]['width']?? && headers[header]['width'] != "">
                    <#assign width = "width:" + headers[header]['width'] >
                </#if>
                <th id="${elementParamName!}_${header}" style="${width}">${headers[header]['label']!?html}</th>
            </#list>
                <th style="border: 0 none;"></th>
            </tr>
            <tr id="grid-row-template" class="grid-row-template" style="display:none;">
                <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                    <td><span class="grid-cell rowNumber"></span></td>
                </#if>
            <#list headers?keys as header>
                <td><span id="${elementParamName!}_${header}"  name="${elementParamName!}_${header}" column_key="${header}" column_type="${headers[header]['formatType']!?html}" column_format="${headers[header]['format']!?html}" class="grid-cell"></span></td>
            </#list>
                <td style="display:none;"><textarea id="${elementParamName!}_jsonrow"></textarea></td>
            </tr>
            <#list rows as row>
                <tr class="grid-row" id="{elementParamName!}_row_${row_index}">
                    <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                        <td><span class="grid-cell rowNumber">${row_index + 1}</span></td>
                    </#if>
                <#list headers?keys as header>
                    <td><span id="${elementParamName!}_${header}" name="${elementParamName!}_${header}" column_key="${header}" column_type="${headers[header]['formatType']!?html}" column_format="${headers[header]['format']!?html}" class="grid-cell">
                            <#if headers[header]['formatType']! == "html">
                                ${row[header]!}
                            <#elseif headers[header]['formatType']! == "decimal" && headers[header]['format']?? && headers[header]['format']?has_content>
                                <#attempt>
                                    <#assign decimalFormat = "#">
                                    <#if row[header]?? && row[header] != "0" && row[header] != "">
                                        <#assign number=row[header]?number>
                                    <#else>
                                        <#assign number=0>
                                        <#assign decimalFormat = "0">
                                    </#if>
                                    <#assign x=headers[header]['format']?number>
                                    <#if (x! > 0) >
                                        <#list 1..x as i>
                                            <#if decimalFormat! == "#" || decimalFormat! == "0">
                                                <#assign decimalFormat = decimalFormat+".">
                                            </#if>
                                            <#assign decimalFormat = decimalFormat+"0">
                                        </#list>  
                                    </#if>
                                    ${number!?string(decimalFormat)}
                                <#recover>
                                    ${row[header]!?html}
                                </#attempt>
                            <#elseif headers[header]['formatType']! == "date" && headers[header]['format']?? && headers[header]['format']?has_content && row[header]?? && row[header]?has_content>
                                <#attempt>
                                    <#assign dateFormat = headers[header]['format']?split("|")>
                                    <#assign date = row[header]?datetime(dateFormat[0])>
                                    ${date?string(dateFormat[1])}
                                <#recover>
                                    ${row[header]!?html}
                                </#attempt>
                            <#else>
                                ${row[header]!?html}
                            </#if>
                        </span>
                    </td>
                </#list>
                    <td style="display:none;"><textarea id="${elementParamName!}_jsonrow" name="${elementParamName!}_jsonrow_${row_index}">${row['jsonrow']!?html}</textarea></td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
