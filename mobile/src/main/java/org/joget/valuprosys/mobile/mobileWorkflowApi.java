/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author akuma
 */
/**
 *
 * @author realakuma
 */
public class mobileWorkflowApi extends DefaultApplicationPlugin implements PluginWebSupport {
    //private DirectoryManager directoryManager;
    //private WorkflowUserManager workflowUserManager;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String formDefId = "test_1";

        String processId = StringUtils.defaultIfEmpty(request.getParameter("processId"));
        String activityId = StringUtils.defaultIfEmpty(request.getParameter("activityId"));
        String Operation = StringUtils.defaultIfEmpty(request.getParameter("Operation"));
        String loginAs = StringUtils.defaultIfEmpty(request.getParameter("loginAs"));

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
            WorkflowUserManager userManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");


            if (Operation.equals(MobileConst.CompleteWithVariable)) {




                WorkflowAssignment assignment = workflowManager.getAssignment(activityId);


                //Setting Approve INFO
                if (assignment != null) {
                    String processDefId = assignment.getProcessDefId();
                    String activityDefId = assignment.getActivityDefId();
                    AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(processId);
                    PackageActivityForm activityForm = appService.retrieveMappedForm(appDef.getAppId(), appDef.getVersion().toString(), processDefId, activityDefId);

                    if (activityForm != null) {
                        formDefId = activityForm.getFormId();

                        String id = appService.getOriginProcessId(processId);

                        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);

                        FormRow row = null;

                        Map<String, String> workflowApproveINFO = MobileUtil.retrieveApproveINFOFromRequest(request, formDefId);


                        if (rowSet != null && !rowSet.isEmpty()) {
                            row = rowSet.get(0);

                            Iterator<Map.Entry<String, String>> it = workflowApproveINFO.entrySet().iterator();
                            while (it.hasNext()) {
                                //Setting approvment INFO
                                Map.Entry<String, String> entry = it.next();
                                row.setProperty(entry.getKey(), entry.getValue());
                            }
                            // save to form
                            appService.storeFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, rowSet, id);
                        }
                    }
                }


                //CompleteActivityWithVariables
                appService.getAppDefinitionForWorkflowActivity(activityId);

                if (assignment != null && !assignment.isAccepted()) {
                    workflowManager.assignmentAccept(activityId);
                }

                Map<String, String> workflowVariableMap = AppUtil.retrieveVariableDataFromRequest(request);
                workflowManager.assignmentComplete(activityId, workflowVariableMap);

                LogUtil.info(getClass().getName(), "Assignment " + activityId + " completed");
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("assignment", assignment.getAssigneeId());
                jsonObject.accumulate("status", "completed");
                jsonObject.accumulate("processId", processId);
                jsonObject.accumulate("activityId", activityId);

                // check for automatic continuation
                String processDefId = assignment.getProcessDefId();
                String activityDefId = assignment.getActivityDefId();
                String packageId = WorkflowUtil.getProcessDefPackageId(processDefId);
                String packageVersion = WorkflowUtil.getProcessDefVersion(processDefId);
                boolean continueNextAssignment = appService.isActivityAutoContinue(packageId, packageVersion, processDefId, activityDefId);
                if (continueNextAssignment) {
                    WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(processId);
                    if (nextAssignment != null) {
                        jsonObject.accumulate("nextActivityId", nextAssignment.getActivityId());
                    }
                }
                result = jsonObject.toString();
            }

            if (Operation.equals(MobileConst.GetApprovementHistoryList)) {
                List<String> userList = new ArrayList<String>();
                Collection<WorkflowActivity> activityList = workflowManager.getActivityList(processId, null, MobileConst.getrows, null, null);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Integer total=0;
                JSONObject jsonObject = new JSONObject();
                for (WorkflowActivity workflowActivity : activityList) {
                    WorkflowActivity activityInfo = workflowManager.getRunningActivityInfo(workflowActivity.getId());
                    //userList = workflowManager.getAssignmentResourceIds(workflowActivity.getProcessDefId(), workflowActivity.getProcessId(), workflowActivity.getId());
                    if (activityInfo.getNameOfAcceptedUser() != null) {
                        //Get Form Data with ActivityId
                        AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(workflowActivity.getProcessId());
                        String id = appService.getOriginProcessId(workflowActivity.getProcessId());
                        PackageActivityForm activityForm = appService.retrieveMappedForm(appDef.getAppId(), appDef.getVersion().toString(), workflowActivity.getProcessDefId(), workflowActivity.getActivityDefId());
                        formDefId = activityForm.getFormId();
                        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
                        FormRow row = null;
                        if (rowSet != null && !rowSet.isEmpty()) {
                            row = rowSet.get(0);
                        }
                        double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(workflowActivity.getId());

                        Map approveInfo = new HashMap(row);
                        Map data = new HashMap();

                        data.put("id", workflowActivity.getId());
                        data.put("name", workflowActivity.getName());
                        data.put("state", workflowActivity.getState());
                        data.put("dateCreated", dateFormat.format(workflowActivity.getCreatedTime()));
                        data.put("dateCompleted",  dateFormat.format(activityInfo.getFinishTime()));
                        data.put("serviceLevelMonitor", WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor));

                        Iterator<Map.Entry<String, String>> it = approveInfo.entrySet().iterator();
                        while (it.hasNext()) {
                            //output approvment INFO
                            Map.Entry<String, String> entry = it.next();
                            if (entry.getKey().toString().indexOf(formDefId)!=-1) {
                                if (entry.getKey().toString().equals(formDefId+MobileConst.Approver))
                                {
                                    data.put("Assignee", entry.getValue());
                                }
                                 if (entry.getKey().toString().equals(formDefId+MobileConst.Comment))
                                {
                                    data.put("Comment", entry.getValue());
                                }
                            
                            }
                        }

                        jsonObject.accumulate("data", data);
                        total++;
                    }
                }

                jsonObject.accumulate("total", total);
                jsonObject.accumulate("start", null);
                jsonObject.accumulate("sort", null);
                jsonObject.accumulate("desc", null);
                //jsonObject.write(writer);
                result = jsonObject.toString();
            }
            //get form metadata
            //paf=appService.viewAssignmentForm(appDef.getAppId(),appDef.getVersion().toString(), activityId, null, null);
            //formDefId=paf.getFormId();
            //FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
            //FormRow row = null;
            //if (rowSet != null && !rowSet.isEmpty()) {
            //    row = rowSet.get(0);
            //}
            //Set <String> test=row.stringPropertyNames();
            //Map test=row.propertyNames();
            //formData=(Map)row;
            //result =FormUtil.generatePropertyJsonObject((Map)rowSet).toString();
            //result = row.toString();




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
        return "Joget Mobile Workflow API";
    }

    public String getVersion() {
        return "3.1.0";
    }

    public String getDescription() {
        return "Joget Mobile Workflow API";
    }

    public String getLabel() {
        return "Joget Mobile Workflow API";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }
}