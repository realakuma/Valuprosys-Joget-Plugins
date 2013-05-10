/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.util.Map;
import java.sql.*;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.valuprosys.mobile.dao.MobileDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.sql.DataSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.valuprosys.mobile.model.Mobile;
import java.util.UUID;

/**
 *
 * @author realakuma
 */
public class MobileDeviceApi extends DefaultApplicationPlugin implements PluginWebSupport {
    //private DirectoryManager directoryManager;

    private MobileDao mobiledao;
    private Mobile mobile;
    Connection conn = null;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String operation = StringUtils.defaultIfEmpty(request.getParameter("operation"));
        String userId = StringUtils.defaultIfEmpty(request.getParameter("userId"));
        String deviceNo = StringUtils.defaultIfEmpty(request.getParameter("deviceNo"));
        String deviceType = StringUtils.defaultIfEmpty(request.getParameter("deviceType"));
        String callback = StringUtils.defaultIfEmpty(request.getParameter("callback"));
        String result = "";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        try {
            mobiledao = (MobileDao) AppContext.getInstance().getAppContext().getBean("MobileDao");
            
            boolean isExists = false;
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            conn = ds.getConnection();
            // execute SQL query
            if (!conn.isClosed()) {
                isExists = getMobileByCondtion(userId, deviceNo, deviceType, conn);
            }
            if (!isExists) {
                Mobile mobile = new Mobile();
                mobile.setId(UUID.randomUUID().toString());
                mobile.setUserId(userId);
                mobile.setDeviceNo(deviceNo);
                mobile.setDeviceType(deviceType);
                mobile.setDateCreated(new java.util.Date());
                mobile.setDateModified(new java.util.Date());
                
                if (addMobile(mobile)) {
                    result = "[{\"INFO\":\"add mobileDevice successed\"}]";
                } else {
                    result = "[{\"INFO\":\"add mobileDevice error\"}]";
                }
            }        
            if (callback != null && !callback.equals("")) {
                response.getWriter().write(StringEscapeUtils.escapeHtml(callback) + "(" + result + ")");
            } else {
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            System.err.println("[{\"Exception\":" + "\"" + e.getMessage() + "\"}]");
        }
    }

    @Override
    public Object execute(Map props) {

        return null;

    }

    public String getName() {
        return "Joget MobileDevice INFO API";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return " MobileDevice INFO";
    }

    public String getLabel() {
        return "Joget MobileDevice INFO API";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }

    protected boolean addMobile(Mobile mobile) {
        return mobiledao.addMobileDevice(mobile);
    }

    protected boolean updateMobile(Mobile mobile) {
        return mobiledao.updateMobileDevice(mobile);
    }

    protected boolean deleteMobile(Mobile mobile) {
        return mobiledao.deleteMobileDevice(mobile.getId());
    }

    protected boolean getMobileByCondtion(String userId, String deviceNo, String deviceType, Connection conn) {
        String result = "";
        String sql = "select c_user_Id,c_device_no,c_device_type from app_fd_Mobiles where 1=1";
        ResultSet rs = null;
        boolean isExists = false;
        PreparedStatement preStat = null;

        if (userId != null && !userId.equals("")) {
            sql += " AND c_user_id='" + userId + "'";
        }

        if (deviceNo != null && !deviceNo.equals("")) {
            sql += " AND c_device_no='" + deviceNo + "'";
        }
        if (deviceType != null && !deviceType.equals("")) {
            sql += " AND c_device_type='" + deviceType + "'";
        }
        try {
            // conn=apps.getJDBCConnection(apps,apps.getSessionId());
            preStat = conn.prepareStatement(sql);
            rs = preStat.executeQuery();
            isExists = rs.next();
            //result = JsonUtil.extractJSONArray(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            result = e.getMessage();

        } catch (Exception e) {
            result = e.getMessage();
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }

                if (preStat != null) {
                    preStat.close();
                }


            } catch (SQLException e) {
            }

        }
        return isExists;

    }
}
