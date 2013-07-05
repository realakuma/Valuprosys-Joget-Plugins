package org.joget.valuprosys.wowprocess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.DynamicDataSourceManager;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.util.WorkflowUtil;
import org.joget.commons.util.LogUtil;
import java.sql.DriverManager;

public class DatabaseUpdateTool extends DefaultApplicationPlugin {

    public String getName() {
        return "Valuprosys Database Update Tool";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Executes SQL INSERT and UPDATE statement on MySQL, Oracle or SQL Server database and commit";
    }

    public Object execute(Map properties) {
        Object result = null;
        try {
            String query = (String) properties.get("query");
            String driver = "";
            DataSource ds = null;
            String datasource = (String) properties.get("jdbcDatasource");
            if (datasource != null && "default".equals(datasource)) {
                // use current datasource
                ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                driver = DynamicDataSourceManager.getProperty("workflowDriver");

            } else {
                Properties props = new Properties();
                String driverClassName = (String) properties.get("driverClassName");
                String url = (String) properties.get("url");
                String username = (String) properties.get("username");
                String password = (String) properties.get("password");

                driver = driverClassName;

                // use custom datasource
                props.put("driverClassName", driverClassName);
                props.put("url", url);
                props.put("username", username);
                props.put("password", password);
                ds = createDataSource(props);
            }

            WorkflowAssignment wfAssignment = (WorkflowAssignment) properties.get("workflowAssignment");

            Map<String, String> replace = new HashMap<String, String>();
            if (driver.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
                replace.put("\\\\", "\\\\");
                replace.put("'", "\\'");
            } else {
                replace.put("'", "''");
            }

            query = WorkflowUtil.processVariable(query, null, wfAssignment, "regex", replace);

            result = executeQuery(ds, query, datasource);

            return result;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error executing plugin", e);
            return null;
        }
    }

    protected DataSource createDataSource(Properties props) throws Exception {
        DataSource ds = BasicDataSourceFactory.createDataSource(props);
        return ds;
    }

    protected boolean executeQuery(DataSource ds, String sql, String datasource) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            if (datasource != null && "default".equals(datasource)) {
                String driver = DynamicDataSourceManager.getProperty("workflowDriver");
                String url = DynamicDataSourceManager.getProperty("workflowUrl");
                String user = DynamicDataSourceManager.getProperty("workflowUser");;
                String password = DynamicDataSourceManager.getProperty("workflowPassword");
                Class.forName(driver);
                con = DriverManager.getConnection(url, user, password);
            } else {
                con = ds.getConnection();
            }
            stmt = con.createStatement();
            boolean result = stmt.execute(sql);
            LogUtil.info("sql", sql);

            con.commit();
            return result;
        } catch (Exception e) {
            return false;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public String getLabel() {
        return "Valuprosys Database Update Tool";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/app/databaseUpdateTool.json", null, true, null);
    }
}
