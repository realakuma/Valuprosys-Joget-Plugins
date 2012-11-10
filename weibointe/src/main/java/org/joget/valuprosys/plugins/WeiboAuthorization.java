/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.plugins;

import org.joget.apps.app.service.AppUtil;

import java.util.Map;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormBuilderPalette;
import org.joget.apps.form.service.FormUtil;
import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;
import weibo4j.http.AccessToken;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;



/**
 *
 * @author tt
 */
public class WeiboAuthorization extends Element implements FormBuilderPaletteElement,PluginWebSupport{

    String str_Authorization="授权";
    String str_Confirm="确认";
    public String getClassName() {
        return getClass().getName();
    }
    
    public String getName(){
        return "WeiboAuthorization";
    }

    public String getDescription(){
        return "WeiboAuthorization Form Element";
    }
    
    public String getVersion(){
        return "1.0.0";
    }
    
    public String getPropertyOptions() {
        //return null;
        return AppUtil.readPluginResource(getClass().getName(), "/properties/WeiboAuthorization.json", null, true, null);
    }
    
    public String getLabel() {
        return "WeiboAuthorization";
    }

    @Override
    public Object execute(Map properties) {
        return super.execute(properties);
    }
    
    @Override
    public String getFormBuilderTemplate() {
        return "<a href='#'>WeiboAuthorization</a>";
    }
    
    @Override
    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    @Override
    public int getFormBuilderPosition() {
        return 100;
    }

    @Override
    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.TextField/images/textField_icon.gif";
    }
    
    @Override
    public String renderTemplate(FormData formData, Map dataModel){
        String template = "WeiboAuthorization.ftl";

        // set value
        //String value = FormUtil.getElementPropertyValue(this, formData);
        Oauth oauth = new Oauth();
        String html ="";
        try{
	String value=oauth.authorize("code");
        dataModel.put("value", value);
        dataModel.put("Weibo_Authorization", str_Authorization);
         dataModel.put("Weibo_confirm", str_Confirm);
        html= FormUtil.generateElementHtml(this, formData, template, dataModel);
        }
        catch(WeiboException ex)
        {
           html=ex.getMessage(); 
        }
        return html;
    }
    
    public void webService(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException{
        String code = request.getParameter("code");
        Oauth oauth = new Oauth(); 
 	try {
            //get access token
            AccessToken at=oauth.getAccessTokenByCode(code);
            //update access_token to config.properties
            LogUtil.info(this.getClassName(), "AccessToken:"+at.getAccessToken());
            WeiboConfig.updateProperties("access_token", at.getAccessToken());
            response.getWriter().write("successed");
	} catch (WeiboException e) {
                 response.getWriter().write(e.getMessage());
	} finally {
    }
  
    } 
}