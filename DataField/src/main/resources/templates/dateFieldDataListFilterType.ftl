<script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
<script type="text/javascript" src="${contextPath}/plugin/org.joget.valuprosys.DateFieldDataListFilterType/js/jquery.blockUI.js"></script>
<link rel="stylesheet" href="${contextPath}/css/jquery-ui-1.8.6.custom.css" />
<!--from:<input id="my_test" name="my_test" type="text" size="10" value="${value!?html}"/>
<br>
to:<input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}"/>
-->
<input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}" placeholder="${label!}" readonly="readonly"/>
<input type="button" id="test" value="Date" />
<script type="text/javascript">
    $(document).ready(function(){
        $('#${name!}').placeholder();
        $('#date_from').datepicker({
                        showOn: "button",
                        buttonImage: "${contextPath}/css/images/calendar.png",
                        buttonImageOnly: true,
                        changeMonth: true,
                        changeYear: true,
                        dateFormat: "yy-mm-dd"});
      $('#date_to').datepicker({
                        showOn: "button",
                        buttonImage: "${contextPath}/css/images/calendar.png",
                        buttonImageOnly: true,
                        changeMonth: true,
                        changeYear: true,
                        dateFormat: "yy-mm-dd"});

        //$('#${name!}').datepicker();
    $('#test').click(function() { 
            $.blockUI({ message: $('#date_select'), css: { width: '275px' } }); 
        }); 
  $('#btn_date_cancel').click(function() { 
            $.unblockUI(); 
            return false; 
        }); 
  $('#btn_date_confirm').click(function() { 
            $('#${name!}').val(' '+$('#date_from').val()+';'+' '+$('#date_to').val()); 
           $.unblockUI(); 
            
        }); 
    });
</script>
<div id="date_select" style="display:none; cursor: default"> 
        <h1>${label!}</h1> 
        from:<input id="date_from" name="date_from" type="text" size="10" value=""/> 
<br>
<br>
        to   :<input id="date_to" name="date_to" type="text" size="10" value=""/>
<br>
<br>
<input type="button" id="btn_date_confirm" value="Yes"/>
<input type="button" id="btn_date_cancel" value="No"/>
</div> 