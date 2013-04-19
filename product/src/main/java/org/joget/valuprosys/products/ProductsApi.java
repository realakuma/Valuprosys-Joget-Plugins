/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.products;

import java.util.Map;
import java.sql.*;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.valuprosys.products.dao.ProductsDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.sql.DataSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.valuprosys.products.model.Products;

/**
 *
 * @author realakuma
 */
public class ProductsApi extends DefaultApplicationPlugin implements PluginWebSupport {
    //private DirectoryManager directoryManager;

    private ProductsDao productdao;
    private Products product;
    Connection conn = null;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String operation = StringUtils.defaultIfEmpty(request.getParameter("operation"));
        String id = StringUtils.defaultIfEmpty(request.getParameter("id"));
        String name = StringUtils.defaultIfEmpty(request.getParameter("name"));
        String description = StringUtils.defaultIfEmpty(request.getParameter("description"));
        String callback = StringUtils.defaultIfEmpty(request.getParameter("callback"));
        String result = "";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        try {
            productdao = (ProductsDao) AppContext.getInstance().getAppContext().getBean("productsDao");
            boolean executed=false;
            if (operation.equals("insert")) {
                Products product = new Products();
                product.setId(id);
                product.setName(name);
                product.setDescription(description);
                if (addProduct(product)) {
                    result = "[{\"INFO\":\"add product successed\"}]";
                } else {
                    result = "[{\"INFO\":\"add product error\"}]";
                }
            }
            if (operation.equals("update")) {
                Products product = new Products();
                product.setId(id);
                product.setName(name);
                product.setDescription(description);
                if (updateProduct(product)){
                    result = "[{\"INFO\":\"update product successed\"}]";
                }else
                {
                    result = "[{\"INFO\":\"update product error\"}]";
                }
                
            }
            if (operation.equals("delete")) {
                Products product = new Products();
                product.setId(id);
                if (deleteProduct(product)){;
                result = "[{\"INFO\":\"delete product successed\"}]";
                }
                else
                {
                    result = "[{\"INFO\":\"delete product error\"}]";
                }
            }

            if (operation.equals("query")) {
                try {
                    // retrieve connection from the default datasource
                    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                    conn = ds.getConnection();
                    // execute SQL query
                    if (!conn.isClosed()) {
                        result = getProductsByCondtion(id, name, conn);
                    }

                    //PreparedStatement stmt = conn.prepareStatement("UPDATE formdata_simpleflow set c_status='#assignment.activityId#' WHERE processId='#assignment.processId#'"); 
                    //stmt.execute();
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
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }

    protected boolean addProduct(Products product) {
        return productdao.addProducts(product);
    }

    protected boolean updateProduct(Products product) {
        return productdao.updateProducts(product);
    }

    protected boolean deleteProduct(Products product) {
        return productdao.deleteProducts(product.getId());
    }

    protected String getProductsByCondtion(String id, String name, Connection conn) {
        String result = "";
        String sql = "select id,c_name,c_description from app_fd_products where 1=1";
        ResultSet rs = null;
        PreparedStatement preStat = null;

        if (id != null && !id.equals("")) {
            sql += " AND id='" + id + "'";
        }

        if (name != null && !name.equals("")) {
            sql += " AND name='" + name + "'";
        }

        try {
            // conn=apps.getJDBCConnection(apps,apps.getSessionId());
            preStat = conn.prepareStatement(sql);
            rs = preStat.executeQuery();
            result = JsonUtil.extractJSONArray(rs);
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
        return result;

    }
}
