<script type="text/javascript">

$(document).ready(function(){
//�������code������Ȩ
  $('#btn_comfire').click(function() { 
  var v_code;
  v_code=$("#txt_code").val();
  $.post("/jw/web/json/plugin/org.joget.valuprosys.plugins.WeiboAuthorization/service",{code:v_code},function(result){
    alert(result);
   },"text");
  });
});
</script>
<div class="form-cell" ${elementMetaData!}>
<a id="${elementParamName!}" name="${elementParamName!}" href="${value!}"  target="_blank">��Ȩ</a><br>
Code:<input id="txt_code" type="text" value=""> <input id="btn_comfire" type="button" value="ȷ��">

</div>