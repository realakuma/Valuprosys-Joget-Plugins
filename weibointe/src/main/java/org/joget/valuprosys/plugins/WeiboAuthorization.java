package org.joget.valuprosys.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import weibo4j.Oauth;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

public class WeiboAuthorization extends Element
  implements FormBuilderPaletteElement, PluginWebSupport
{
  String str_Authorization = "授权";
  String str_Confirm = "确认";

  public String getClassName() { return getClass().getName(); }

  public String getName()
  {
    return "WeiboAuthorization";
  }

  public String getDescription() {
    return "WeiboAuthorization Form Element";
  }

  public String getVersion() {
    return "1.0.0";
  }

  public String getPropertyOptions()
  {
    return AppUtil.readPluginResource(getClass().getName(), "/properties/WeiboAuthorization.json", null, true, null);
  }

  public String getLabel() {
    return "WeiboAuthorization";
  }

  public Object execute(Map properties)
  {
    return super.execute(properties);
  }

  public String getFormBuilderTemplate()
  {
    return "<a href='#'>WeiboAuthorization</a>";
  }

  public String getFormBuilderCategory()
  {
    return "Basic";
  }

  public int getFormBuilderPosition()
  {
    return 100;
  }

  public String getFormBuilderIcon()
  {
    return "/plugin/org.joget.apps.form.lib.TextField/images/textField_icon.gif";
  }

  public String renderTemplate(FormData formData, Map dataModel)
  {
    String template = "WeiboAuthorization.ftl";

    Oauth oauth = new Oauth();
    String html = "";
    try {
      String value = oauth.authorize("code");
      dataModel.put("value", value);
      dataModel.put("Weibo_Authorization", this.str_Authorization);
      dataModel.put("Weibo_confirm", this.str_Confirm);
      html = FormUtil.generateElementHtml(this, formData, template, dataModel);
    }
    catch (WeiboException ex)
    {
      html = ex.getMessage();
    }
    return html;
  }

  public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String code = request.getParameter("code");
    Oauth oauth = new Oauth();
    try
    {
      AccessToken at = oauth.getAccessTokenByCode(code);

      LogUtil.info(getClassName(), "AccessToken:" + at.getAccessToken());
      WeiboConfig.updateProperties("access_token", at.getAccessToken());
      response.getWriter().write("successed");
    } catch (WeiboException e) {
      response.getWriter().write(e.getMessage());
    }
    finally
    {
    }
  }
}