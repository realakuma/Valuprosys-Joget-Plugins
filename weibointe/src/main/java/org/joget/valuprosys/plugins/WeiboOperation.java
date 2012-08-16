/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.plugins;

import org.joget.apps.app.service.AppUtil;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormBuilderPalette;
import org.joget.apps.form.service.FormUtil;
import weibo4j.Oauth;
import weibo4j.util.WeiboConfig;
import weibo4j.http.AccessToken;
import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author byf
 */
public class WeiboOperation extends Element implements FormBuilderPaletteElement, PluginWebSupport {

    private String AU_ERROR="微博授权期限已过，请运行相关流程重新授权";
    public String getClassName() {
        return getClass().getName();
    }

    public String getName() {
        return "WeiboOperation";
    }

    public String getDescription() {
        return "WeiboOperation Form Element";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String getPropertyOptions() {
        //return null;
        return AppUtil.readPluginResource(getClass().getName(), "/properties/WeiboOperation.json", null, true, null);
    }

    public String getLabel() {
        return "WeiboOperation";
    }

    @Override
    public Object execute(Map properties) {
        return super.execute(properties);
    }

    @Override
    public String getFormBuilderTemplate() {
        return "<a href='#'>WeiboOperation</a>";
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
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "WeiboOperation.ftl";

        // set property value
        String value = FormUtil.getElementPropertyValue(this, formData);
        Map<String,String> temp =new HashMap<String,String>();
        temp.put("id", "null");
        ArrayList al= new ArrayList();
        al.add(temp);
        Oauth oauth = new Oauth();
        Weibo weibo = new Weibo();
        Timeline tm = new Timeline();
        weibo.setToken(WeiboConfig.getValue("access_token"));
        dataModel.put("value", value);
        dataModel.put("statuses",  al);
        dataModel.put("error", "");
        
        String html = "";


        try {
            StatusWapper status = tm.getFriendsTimeline();
            for (Status s : status.getStatuses()) {
                LogUtil.info(this.getName(), s.getText());
                 }
             
            dataModel.put("statuses", status.getStatuses());
            html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        } catch (WeiboException ex) {
            
            if (ex.getErrorCode()==21332) //过期
            {
             dataModel.put("error", AU_ERROR);
            }
            html = FormUtil.generateElementHtml(this, formData, template, dataModel);
            
        }
        
        return html;
    }

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        Oauth oauth = new Oauth();
        try {
            //get access token
            AccessToken at = oauth.getAccessTokenByCode(code);
            //update access_token to config.properties
            WeiboConfig.updateProperties("access_token", at.getAccessToken());
            response.getWriter().write("successed");
        } catch (WeiboException e) {
            response.getWriter().write(e.getMessage());
        } finally {
        }

    }
}