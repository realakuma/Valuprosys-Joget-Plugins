<script type="text/javascript">

$(document).ready(function(){
//根据相关code进行授权
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
<a id="${elementParamName!}" name="${elementParamName!}" href="${value!}"  target="_blank">授权</a><br>
Code:<input id="txt_code" type="text" value=""> <input id="btn_comfire" type="button" value="确认">

</div>