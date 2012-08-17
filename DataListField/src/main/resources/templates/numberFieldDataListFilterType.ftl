<script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
<script type="text/javascript" src="${contextPath}/plugin/org.joget.valuprosys.NumberFieldDataListFilterType/js/jquery.blockUI.js"></script>
<link rel="stylesheet" href="${contextPath}/css/jquery-ui-1.8.6.custom.css" />
<!--from:<input id="my_test" name="my_test" type="text" size="10" value="${value!?html}"/>
<br>
to:<input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}"/>
-->
<input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}" placeholder="${label!}" readonly="readonly"/>
<input type="button" id="test" value="Number" />
<script type="text/javascript">
    $(document).ready(function(){
        $('#${name!}').placeholder();
        
        //$('#${name!}').datepicker();
    $('#test').click(function() { 
            $.blockUI({ message: $('#date_select'), css: { width: '275px' } }); 
        }); 
  $('#btn_number_cancel').click(function() { 
            $.unblockUI(); 
            return false; 
        }); 
  $('#btn_number_confirm').click(function() { 
            $('#${name!}').val(' '+$('#number_from').val()+';'+' '+$('#number_to').val()); 
           $.unblockUI(); 
            
        }); 
    });
</script>
<div id="date_select" style="display:none; cursor: default"> 
        <h1>${label!}</h1> 
        from:<input id="number_from" name="number_from" type="text" size="10" value=""/> 
<br>
<br>
        to   :<input id="number_to" name="number_to" type="text" size="10" value=""/>
<br>
<br>
<input type="button" id="btn_number_confirm" value="Yes"/>
<input type="button" id="btn_number_cancel" value="No"/>
</div> 