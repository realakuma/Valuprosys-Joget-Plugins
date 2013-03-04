/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.products;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.sql.*;
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

    private ProductsDao productdao;
    private Products product;
    Connection conn = null;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String operation = request.getParameter("operation");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String result = "";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        try {
            productdao = (ProductsDao) AppContext.getInstance().getAppContext().getBean("productsDao");
            if (operation.equals("insert")) {
                Products product = new Products();
                product.setId(id);
                product.setName(name);
                product.setDescription(description);
                addProduct(product);
                result = "add product successed";
            }
            if (operation.equals("update")) {
                Products product = new Products();
                product.setId(id);
                product.setName(name);
                product.setDescription(description);
                updateProduct(product);
                result = "update product successed";
            }
            if (operation.equals("delete")) {
                Products product = new Products();
                product.setId(id);
                deleteProduct(product);
                result = "update product successed";
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
            response.getWriter().write(result);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
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

        productdao.addProducts(product);
        return true;
    }

    protected boolean updateProduct(Products product) {
        productdao.updateProducts(product);
        return true;
    }

    protected boolean deleteProduct(Products product) {
        productdao.deleteProducts(product.getId());
        return true;
    }

    protected String getProductsByCondtion(String id, String name, Connection conn) {
        String result = "";
        String sql = "select id,name,description from valu_products where 1=1";
        ResultSet rs = null;
        PreparedStatement preStat = null;

        if (id != null) {
            sql += " AND id='" + id + "'";
        }

        if (name != null) {
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
