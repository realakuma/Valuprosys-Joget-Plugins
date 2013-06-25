/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.EnvironmentVariable;

/**
 *
 * @author akuma
 */
public class MobileUtil {

    Connection conn = null;
    // retrieve connection from the default datasource
    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
    FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");

    /**
     * Read form's Approve INFO from request parameters and populate into a Map
     * @param request,prefix
     * @return
     */
    public static Map<String, String> retrieveApproveINFOFromRequest(HttpServletRequest request, String prefix) {
        Map<String, String> variables = new HashMap<String, String>();

        if (request != null) {
            Enumeration<String> enumeration = request.getParameterNames();
            //loop through all parameters to get the workflow variables
            while (enumeration.hasMoreElements()) {
                String paramName = enumeration.nextElement();
                if (paramName.startsWith(prefix)) {
                    variables.put(paramName, request.getParameter(paramName));
                }
            }
        }
        return variables;
    }

    public FormRow getFormDataByActivityId(String activityId) {


        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        //Get Form Data with ActivityId
        AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
        String id = appService.getOriginProcessId(assignment.getProcessId());
        PackageActivityForm activityForm = appService.retrieveMappedForm(appDef.getAppId(), appDef.getVersion().toString(), assignment.getProcessDefId(), assignment.getActivityDefId());
        String formDefId = activityForm.getFormId();
        FormData formData = new FormData();
        String primaryKey = appService.getOriginProcessId(assignment.getProcessId());
        formData.setPrimaryKeyValue(primaryKey);
        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
        FormRow row = null;
        if (rowSet != null && !rowSet.isEmpty()) {
            row = rowSet.get(0);
        }
        return row;
    }

    public static String getEnvVarByName(String envVariable, AppDefinition appDef, EnvironmentVariableDao environmentVariableDao) {
        String envValue = "";
        EnvironmentVariable env = environmentVariableDao.loadById(envVariable, appDef);



        if (env != null && env.getValue() != null && env.getValue().trim().length() > 0) {
            envValue = env.getValue();
        }
        return envValue;
    }

    public static void setEnvVar(String envVariable, String envVarValue, AppDefinition appDef, EnvironmentVariableDao environmentVariableDao) {
        EnvironmentVariable env = environmentVariableDao.loadById(envVariable, appDef);

        env.setValue(envVarValue);
        environmentVariableDao.update(env);

    }

    public String getOpionValue(String tableName, String nameColumn, String valueColumn, String value){
        String result = "";

        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            conn = ds.getConnection();
            // execute SQL query
            if (!conn.isClosed()) {
                String sql = "select " + nameColumn + " from " + tableName + " where 1=1";
                ResultSet rs = null;
                PreparedStatement preStat = null;

                if (value != null && !value.equals("")) {
                    sql += " AND " + valueColumn + "='" + value + "'";
                }

                preStat = conn.prepareStatement(sql);
                rs = preStat.executeQuery();
                while (rs.next()) {
                    result = rs.getString(1);
                }
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
        return result;
    }
}
