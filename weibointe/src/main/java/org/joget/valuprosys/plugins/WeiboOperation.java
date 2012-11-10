package org.joget.valuprosys.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.http.AccessToken;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

public class WeiboOperation extends Element
  implements FormBuilderPaletteElement, PluginWebSupport
{
  private String AU_ERROR = "微博授权期限已过，请运行相关流程重新授权";
  private String str_comment = "评论";
  private String str_repost = "转发";
  private String str_sender = "发布人";
  private String str_content = "内容";
  private String str_close = "关闭";

  public String getClassName() { return getClass().getName(); }

  public String getName()
  {
    return "WeiboOperation";
  }

  public String getDescription() {
    return "WeiboOperation Form Element";
  }

  public String getVersion() {
    return "1.0.0";
  }

  public String getPropertyOptions()
  {
    return AppUtil.readPluginResource(getClass().getName(), "/properties/WeiboOperation.json", null, true, null);
  }

  public String getLabel() {
    return "WeiboOperation";
  }

  public Object execute(Map properties)
  {
    return super.execute(properties);
  }

  public String getFormBuilderTemplate()
  {
    return "<a href='#'>WeiboOperation</a>";
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
    String template = "WeiboOperation.ftl";

    String value = FormUtil.getElementPropertyValue(this, formData);
    Map temp = new HashMap();
    temp.put("id", "null");
    ArrayList al = new ArrayList();
    al.add(temp);
    Oauth oauth = new Oauth();
    Weibo weibo = new Weibo();
    Timeline tm = new Timeline();
    weibo.setToken(WeiboConfig.getValue("access_token"));
    dataModel.put("value", value);
    dataModel.put("statuses", al);
    dataModel.put("error", "");
    dataModel.put("comment", this.str_comment);
    dataModel.put("repost", this.str_repost);
    dataModel.put("sender", this.str_sender);
    dataModel.put("content", this.str_content);
    dataModel.put("close", this.str_close);
    String html = "";
    try
    {
      StatusWapper status = tm.getFriendsTimeline();
      for (Status s : status.getStatuses()) {
        LogUtil.info(getName(), s.getText());
      }

      dataModel.put("statuses", status.getStatuses());
      html = FormUtil.generateElementHtml(this, formData, template, dataModel);
    }
    catch (WeiboException ex) {
      if (ex.getErrorCode() == 21332)
      {
        dataModel.put("error", this.AU_ERROR);
      }
      html = FormUtil.generateElementHtml(this, formData, template, dataModel);
    }

    return html;
  }

  public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String code = request.getParameter("code");
    Oauth oauth = new Oauth();
    try
    {
      AccessToken at = oauth.getAccessTokenByCode(code);

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