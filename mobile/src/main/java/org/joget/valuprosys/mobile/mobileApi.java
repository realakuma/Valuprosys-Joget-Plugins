/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.service.FormService;

import org.joget.apps.form.service.FormUtil;

/**
 *
 * @author realakuma
 */
public class mobileApi extends DefaultApplicationPlugin implements PluginWebSupport {
    //private DirectoryManager directoryManager;
    //private WorkflowUserManager workflowUserManager;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String formDefId = "test_1";

        String processId = StringUtils.defaultIfEmpty(request.getParameter("processId"));
        String activityId = StringUtils.defaultIfEmpty(request.getParameter("activityId"));
        String dataType = StringUtils.defaultIfEmpty(request.getParameter("dataType"));

        String callback = StringUtils.defaultIfEmpty(request.getParameter("callback"));
        String result = "";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        try {


            //Service bean
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");
            //WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
            WorkflowAssignment assignment = null;
            Map fData = null;
            String id = appService.getOriginProcessId(processId);
            AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(processId);

            PackageActivityForm paf;

            Form form = null;

            PackageActivityForm activityForm = null;
            FormData formData = new FormData();

            if (activityId != null && !activityId.trim().isEmpty()) {
                assignment = workflowManager.getAssignment(activityId);
            }

            if (assignment != null) {
                // load assignment form
                activityForm = appService.viewAssignmentForm(appDef, assignment, formData, "");
                form = activityForm.getForm();

            }


            if (dataType.equals("meta")) {
                //get form metadata
                result = formService.generateElementJson(form);
            }

            if (dataType.equals("data")) {
                //get form metadata
                //paf=appService.viewAssignmentForm(appDef.getAppId(),appDef.getVersion().toString(), activityId, null, null);
                //formDefId=paf.getFormId();
                FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
                FormRow row = null;

                if (rowSet != null && !rowSet.isEmpty()) {
                    row = rowSet.get(0);   
                }
               
                //Set <String> test=row.stringPropertyNames();
               //Map test=row.propertyNames();
               //formData=(Map)row;
                //result =FormUtil.generatePropertyJsonObject((Map)rowSet).toString();
                result =row.toString();
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
        return "Joget Get Mobile INFO API";
    }

    public String getVersion() {
        return "3.1.0";
    }

    public String getDescription() {
        return "Get Mobile Approvment INFO";
    }

    public String getLabel() {
        return "Joget Get Mobile INFO API";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }
}
