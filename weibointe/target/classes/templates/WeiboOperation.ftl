<script type="text/javascript" src="${request.contextPath}/plugin/org.joget.valuprosys.plugins.WeiboOperation/js/jquery.blockUI.js"></script>
<link rel="stylesheet" href="${request.contextPath}/css/jquery-ui-1.8.6.custom.css" />
<script type="text/javascript">
         function fn_weibo_post()
         {
             //new_weibo
              $.blockUI({ message: $('#weibo_post'), css: { width: '350px' } });
             
         }
         function fn_weibo_repost(weibo_id)
         {
             //repost
              $('#weibo_statusid').val(weibo_id);
              $('#weibo_operation_flag').val("repost");
              
              $('#btn_Weibo_confirm').val("${repost}");
              $.blockUI({ message: $('#weibo_post'), css: { width: '350px' } });
         }
         function fn_weibo_comment(weibo_id)
         {
             //comment
              $('#weibo_statusid').val(weibo_id);
              $('#weibo_operation_flag').val("comment");
             $('#btn_Weibo_confirm').val("${comment}");
              $.blockUI({ message: $('#weibo_post'), css: { width: '350px' } });
         }

 $(document).ready(function(){         
    $('#btn_Weibo_cancel').click(function() { 
            $.unblockUI(); 
            return false; 
        });
    $('#btn_Weibo_confirm').click(function() {
            $.unblockUI(); 
            var statusid=$('#weibo_statusid').val();
           $('#${elementParamName!}').val($('#weibo_post_content').val()+' !@@!'+$('#weibo_operation_flag').val()+' !@@!'+$('#weibo_statusid').val()+' !@@!'+$('#div_'+statusid).text());
            return false; 
        });
     })
</script>
<div style="width:800;overflow:auto">  
<table id="weibo_content_table"> 
<#list statuses as status>
<#if status.id!="null">
<tr>
    <td>
    ${sender}:${status.user.name}<br>
    ${content}:${status.text}
    </td>
</tr>    
<tr>
    <td>
    <#if status.thumbnailPic!="">
<a href="${status.originalPic}"><image src="${status.thumbnailPic}"/></a>
</#if>
</td>
</tr>

<tr>
    <td>
    <a id="weibo_post_${status.id}" name="weibo_post_${status.id}" href="#" onclick="fn_weibo_repost(${status.id})">${repost}</a>  <a id="weibo_comment_${status.id}"  href="#" onclick="fn_weibo_comment(${status.id})">${comment}</a><br>
</td>
</tr>
<div id="div_${status.id}" style="display:none; cursor: default">
${status.text}
</div>

</#if>
</#list>
</table>
</div>
<div class="form-cell" ${elementMetaData!}>
<span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <textarea id="${elementParamName!}" name="${elementParamName!}" cols="${element.properties.cols!}"  rows="${element.properties.rows!}" <#if error??>class="form-error-cell"</#if> readonly>${value!?html}${error!} </textarea>
</div>
<div id="weibo_post" style="display:none; cursor: default">
    <textarea id="weibo_post_content" name="weibo_post_content" cols="${element.properties.cols!}"  rows="${element.properties.rows!}"></textarea>
    <input id="weibo_statusid" type="hidden" value=""/>
    <input id="weibo_operation_flag" type="hidden" value=""/>
    <input type="button" id="btn_Weibo_confirm" value="Yes"/>
    <input type="button" id="btn_Weibo_cancel" value="${close}"/>
</div>