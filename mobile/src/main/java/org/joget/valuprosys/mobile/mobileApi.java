package org.joget.valuprosys.mobile;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.PagedList;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowVariable;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.springframework.context.ApplicationContext;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class mobileApi extends DefaultApplicationPlugin
        implements PluginWebSupport {

    public void webService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String processId = StringUtils.defaultIfEmpty(request.getParameter("processId"));
        String activityId = StringUtils.defaultIfEmpty(request.getParameter("activityId"));
        String dataType = StringUtils.defaultIfEmpty(request.getParameter("dataType"));
        String formDefId = StringUtils.defaultIfEmpty(request.getParameter("formDefId"));
        String basePath = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort()
                + "/";
        String callback = StringUtils.defaultIfEmpty(request.getParameter("callback"));
        String result = "";
        String process_name = "";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");

            WorkflowAssignment assignment = null;
            Map fData = null;
            String id = appService.getOriginProcessId(processId);
            AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(processId);

            Form form = null;

            PackageActivityForm activityForm = null;
            FormData formData = new FormData();

            if ((activityId != null) && (!activityId.trim().isEmpty())) {
                assignment = workflowManager.getAssignment(activityId);
            }

            if (assignment != null) {

                activityForm = appService.viewAssignmentForm(appDef, assignment, formData, "");
                form = activityForm.getForm();
                if (formDefId.equals("")) {
                    formDefId = activityForm.getFormId();
                }
                process_name = assignment.getProcessName();

            }


            String temp_result = formService.generateElementJson(form);
            if ((dataType.equals("meta")) || (dataType.equals("all"))) {
                //temp_result = formService.generateElementJson(form);

                //holiday type name handle
                if (formDefId.indexOf("wowprime_leave") != -1) {

                    if (temp_result.indexOf("<div id=\\\"centerleavechoice\\\">1<\\/div>") != -1) {
                        temp_result = temp_result.replace("<div id=\\\"centerleavechoice\\\">1<\\/div>", MobileConst.holiday);
                    }
                    if (temp_result.indexOf("<div id=\\\"centerleavechoice\\\">2<\\/div>") != -1) {
                        temp_result = temp_result.replace("<div id=\\\"centerleavechoice\\\">2<\\/div>", MobileConst.in_turn);
                    }
                    if (temp_result.indexOf("<div id=\\\"centerleavechoice\\\">3<\\/div>") != -1) {
                        temp_result = temp_result.replace("<div id=\\\"centerleavechoice\\\">3<\\/div>", MobileConst.b_t);
                    }
                }

                result = "{\"meta\":[" + temp_result + "]}";
            }

            if ((dataType.equals("data")) || (dataType.equals("all"))) {
                Map map;
                map = null;
                FormRowSet rowSet=null;
                if (!process_name.contains("请购") && !process_name.contains("费用报销")) {
                   rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
                }
                FormRow row = null;

                if ((rowSet != null) && (!rowSet.isEmpty())) {
                    row = (FormRow) rowSet.get(0);
                }

                if (row != null) {
                    map = new HashMap(row);
                } else {
                    map = new HashMap();
                }
                //

                Collection<WorkflowVariable> variableList = workflowManager.getProcessVariableList(processId);

                //setting the workflow variables
                for (WorkflowVariable variable : variableList) {
                    String fina_status = "";
                    String[] strarray = variable.getVal().toString().split(";");
                    for (int i = 0; i < strarray.length; i++) {
                        fina_status = strarray[i];
                    }
                    map.put(variable.getId(), fina_status);
                }


                // setting po sub_form link
                if (process_name.contains("请购")) {
                    map.put(MobileConst.subFormPoDetail, basePath + MobileConst.subFormPoDetailUrl + map.get("productId").toString());
                }

                if (process_name.contains("费用报销")) {
                    map.put(MobileConst.subFormFeeDetail, basePath + MobileConst.subFormFeeDetailUrl + map.get("expenseId").toString());
                }
                /*
                //Element sub_form = FormUtil.findElement(MobileConst.subFormPoDetail, form, formData);
                if (sub_form != null) {
                map.put(MobileConst.subFormPoDetail, basePath+MobileConst.subFormPoDetailUrl+map.get("productFormId").toString());
                JSONObject jsonobject=JSONObject.fromObject(temp_result);
                jsonobject.getJSONArray("elements").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONArray("elements").getJSONObject(1).remove("elements");
                temp_result=jsonobject.toString();
                
                }
                
                sub_form = FormUtil.findElement(MobileConst.subFormFeeDetail, form, formData);
                // setting fee sub_form link
                if (sub_form != null) {
                map.put(MobileConst.subFormFeeDetail, basePath+MobileConst.subFormPoDetailUrl+map.get("expenseId").toString());
                JSONObject jsonobject=JSONObject.fromObject(temp_result);
                jsonobject.getJSONArray("elements").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONArray("elements").getJSONObject(1).remove("elements");
                temp_result=jsonobject.toString();
                }
                 */
                //setting current approver
                map.put(formDefId + MobileConst.Approver, workflowManager.getWorkflowUserManager().getCurrentUsername());

                if (dataType.equals("all")) {
                    result = "{\"meta\":[" + temp_result + "]," + "\"data\":[" + JsonUtil.generatePropertyJsonObject(map).toString() + "]}";
                }

                if (dataType.equals("data")) {
                    result = "{\"data\":[" + JsonUtil.generatePropertyJsonObject(map).toString() + "]}";
                }
            }

            if (dataType.equals("pending")) {
                result = getPendingActivitys(workflowManager, null);
            }

            if ((callback != null) && (!callback.equals(""))) {
                response.getWriter().write(StringEscapeUtils.escapeHtml(callback) + "(" + result + ")");
            } else {
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            System.err.println("[{\"Exception\":\"" + e.getMessage() + "\"}]");
        }
    }

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

    private String getPendingActivitys(WorkflowManager workflowManager, String processId) throws JSONException {
        PagedList<WorkflowAssignment> assignmentList = workflowManager.getAssignmentPendingList(processId, null, null, null, null);
        Integer total = assignmentList.getTotal();
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        for (WorkflowAssignment assignment : assignmentList) {
            Map data = new HashMap();
            data.put("processId", assignment.getProcessId());
            data.put("activityId", assignment.getActivityId());
            data.put("processName", assignment.getProcessName());
            data.put("activityName", assignment.getActivityName());
            data.put("processVersion", assignment.getProcessVersion());
            data.put("dateCreated", dateFormat.format(assignment.getDateCreated()));
            data.put("due", assignment.getDueDate() != null ? assignment.getDueDate() : "-");

            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(assignment.getActivityId());

            data.put("serviceLevelMonitor", WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor));

            data.put("id", assignment.getActivityId());
            data.put("label", assignment.getActivityName());
            data.put("description", assignment.getDescription());

            jsonObject.accumulate("data", data);
        }

        jsonObject.accumulate("total", total);
        jsonObject.accumulate("start", "");
        jsonObject.accumulate("sort", "");
        jsonObject.accumulate("desc", "");

        return jsonObject.toString();
    }
}