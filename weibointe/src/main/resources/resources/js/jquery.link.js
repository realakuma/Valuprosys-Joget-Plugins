(function( $ ){

    var methods = {

        init: function(option) {
            return this.each(function(){
                var setting=$.extend({
                    url:"#"
                },option)
                if(typeof(setting.variable)  !=  "undefined"
                    &&typeof(setting.variableName)  !=  "undefined"
                    &&setting.variable.length>0){
                    try{
                        var url=setting.url;
                        setting.url+="?";
                        var a=$(this);
                        for(var i=0;i<setting.variableName.length;i++){
                                $("#"+setting.variable[i]).blur(function(){
                                    a.LinkFunction("init1",{
                                    url:url,
                                    variable:setting.variable,
                                    variableName:setting.variableName
                                    });
                                });
                            if(i==0){
                                setting.url+=setting.variableName[i]+"="+$("#"+setting.variable[i]).val();
                            }else {
                                setting.url+="&"+setting.variableName[i]+"="+$("#"+setting.variable[i]).val();
                            }
                        }
                    }catch(e){
                        setting.url=e.name + ": " + e.message;
                    } 
                }
                 $(this).attr("href",setting.url);
            });
        },
        init1: function(option) {
            return this.each(function(){
                var setting=$.extend({
                    url:"#"
                },option)
                if(typeof(setting.variable)  !=  "undefined"
                    &&typeof(setting.variableName)  !=  "undefined"
                    &&setting.variable.length>0){
                    try{
                        setting.url+="?";
                        for(var i=0;i<setting.variableName.length;i++){
                            if(i==0){

                                setting.url+=setting.variableName[i]+"="+$("#"+setting.variable[i]).val();
                            }else {
                                setting.url+="&"+setting.variableName[i]+"="+$("#"+setting.variable[i]).val();
                            }
                        }
                    }catch(e){
                        setting.url=e.name + ": " + e.message;
                    } 
                    
                }
                $(this).attr("href",setting.url);
            });
        }
    };

    $.fn.LinkFunction = function( method ) {

        if ( methods[method] ) {
            return methods[method].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' +  method + ' does not exist on jQuery.formgrid' );
        }

    };

})( jQuery );