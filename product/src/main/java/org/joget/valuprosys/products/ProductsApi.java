/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.products;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.sql.*;
import org.json.JSONException;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.base.PluginWebSupport;
import java.util.ArrayList;
import org.joget.valuprosys.products.dao.ProductsDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.sql.DataSource;
import org.joget.valuprosys.products.model.Products;
import org.joget.valuprosys.products.AppContext;

/**
 *
 * @author realakuma
 */
public class ProductsApi extends DefaultApplicationPlugin implements PluginWebSupport {
    //private DirectoryManager directoryManager;
    //private ProductsDao productdao;
    //private Products product;

    Connection conn = null;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String operation = request.getParameter("operation");
        String id = request.getParameter("id");
        String name=request.getParameter("name");
        String result = "";
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0); 
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

            conn = ds.getConnection();

            // execute SQL query
            if (!conn.isClosed()) {
                if (operation != null) {
                    if (operation.equals("query")) {
                        result = getProductsByCondtion(id,name,conn);
                    }
                }
                //PreparedStatement stmt = conn.prepareStatement("UPDATE formdata_simpleflow set c_status='#assignment.activityId#' WHERE processId='#assignment.processId#'"); 
                //stmt.execute();
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }


        //productdao=(ProductsDao)AppContext.getInstance().getAppContext().getBean("productsDao");
        //productdao=(ProductsDao) AppUtil.getApplicationContext().getBean("productsDao");
        //product=productdao.getProducts("abc");
        // Get Parameter
        // String text = request.getParameter("say_something");


        // Write to response
     
        response.getWriter().write(result);
    }

    @Override
    public Object execute(Map props) {
        /*
        PluginManager pluginManager = (PluginManager) props.get("pluginManager");
        directoryManager = (DirectoryManager) pluginManager.getBean("directoryManager");
        userDao=(UserDao) AppUtil.getApplicationContext().getBean("userDao");
        roleDao=(RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
        String search_base = (String) props.get("search_base");
        String security_principal = (String) props.get("security_principal");
        String security_credentials=(String) props.get("security_credentials");
        String ldap_url=(String) props.get("ldap_url");
        ArrayList list = new ArrayList();
        ldapTest lt=new ldapTest();
        
        try
        {
        //获得AD中的用户
        list=lt.getADuserList(security_principal, security_credentials, ldap_url, search_base);
        //添加用户
        for (int i=0;i<list.size();i++)
        {
        
        user=directoryManager.getUserByUsername(list.get(i).toString());
        if (user==null){
        addUser(list.get(i).toString());
        }
        }
        } catch (Exception e) {
        LogUtil.error(this.getName(), e, e.getMessage());
        }
        finally
        {
        }
         */
        return null;

    }

    public String getName() {
        return "Joget Get Products INFO API";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Get Products INFO";
    }

    public String getLabel() {
        return "Joget Get Products INFO API";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/JogetUsersUpdate.json", null, true, "/messages/JogetUsersUpdate_zh_CN");
    }

    protected String getProductsByCondtion(String id, String name,Connection conn) {
        String result = "";
        String sql = "select id,name,description from valu_products where 1=1";
        ResultSet rs = null;
        PreparedStatement preStat = null;
        
        if (id!=null)
        sql += " AND id='"+id+"'";
        
        if (name!=null)
        sql += " AND name='"+name+"'";

        try {
            // conn=apps.getJDBCConnection(apps,apps.getSessionId());
            preStat = conn.prepareStatement(sql);
            rs = preStat.executeQuery();
            result = JsonUtil.extractJSONArray(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            result=e.getMessage();
            
        } catch (JSONException e)
        {
            result=e.getMessage();
        } 
        finally {

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
        return result;

    }
}
