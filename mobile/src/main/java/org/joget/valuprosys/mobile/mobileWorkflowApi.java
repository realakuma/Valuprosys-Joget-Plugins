/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PagedList;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.directory.model.User;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Employment;
import org.joget.directory.model.User;

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

    private UserDao userDao;
    private User userClass;

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String processId = StringUtils.defaultIfEmpty(request.getParameter("processId"));
        String activityId = StringUtils.defaultIfEmpty(request.getParameter("activityId"));
        String Operation = StringUtils.defaultIfEmpty(request.getParameter("Operation"));
        String callback = StringUtils.defaultIfEmpty(request.getParameter("callback"));
        String listType = StringUtils.defaultIfEmpty(request.getParameter("listType"));
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
            userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
            FormService formService;


            if (Operation.equals(MobileConst.CompleteWithVariable)) {
                result = completeWithVariable(request, activityId, appService, workflowManager);
            }

            if (Operation.equals(MobileConst.GetApprovementHistoryList)) {
                result = getApprovmentHistoryList(processId, appService, workflowManager);
            }
            if (Operation.equals(MobileConst.GetAssignmentPendingAndAcceptedList)) {
                result = getAssignmentPendingAndAcceptedList(listType, appService, workflowManager);
            }

            if (callback != null && !callback.equals("")) {
                response.getWriter().write(StringEscapeUtils.escapeHtml(callback) + "(" + result + ")");
            } else {
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            //System.err.println("[{\"Exception\":" + "\"" + e.getMessage() + "\"}]");
            e.printStackTrace();
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

    protected String getApprovmentHistoryList(String processId, AppService appService, WorkflowManager workflowManager) throws IOException, JSONException {

        Collection<WorkflowActivity> activityList = workflowManager.getActivityList(processId, null, MobileConst.getrows, null, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Integer total = 0;
        JSONObject jsonObject = new JSONObject();
        for (WorkflowActivity workflowActivity : activityList) {
            WorkflowActivity activityInfo = workflowManager.getRunningActivityInfo(workflowActivity.getId());
            //userList = workflowManager.getAssignmentResourceIds(workflowActivity.getProcessDefId(), workflowActivity.getProcessId(), workflowActivity.getId());
            if (activityInfo.getNameOfAcceptedUser() != null) {
                //Get Form Data with ActivityId
                AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(workflowActivity.getProcessId());
                String id = appService.getOriginProcessId(workflowActivity.getProcessId());
                PackageActivityForm activityForm = appService.retrieveMappedForm(appDef.getAppId(), appDef.getVersion().toString(), workflowActivity.getProcessDefId(), workflowActivity.getActivityDefId());
                String formDefId = activityForm.getFormId();
                FormData formData = new FormData();
                String primaryKey = appService.getOriginProcessId(workflowActivity.getProcessId());
                formData.setPrimaryKeyValue(primaryKey);
                Form loadForm = appService.viewDataForm(appDef.getId(), appDef.getVersion().toString(), formDefId, null, null, null, formData, null, null);

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
                data.put("dateCompleted", dateFormat.format(activityInfo.getFinishTime()));
                data.put("serviceLevelMonitor", WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor));

                Iterator<Map.Entry<String, String>> it = approveInfo.entrySet().iterator();
                while (it.hasNext()) {
                    //output approvment INFO
                    Map.Entry<String, String> entry = it.next();
                    if (entry.getKey().toString().indexOf(formDefId) != -1) {
                        if (entry.getKey().toString().equals(formDefId + MobileConst.Approver)) {
                            userClass = userDao.getUserById(entry.getValue());
                            if (userClass != null) {
                                data.put("Assignee", userClass.getFirstName());
                            } else {
                                data.put("Assignee", entry.getValue());
                            }
                        } else if (entry.getKey().toString().equals(formDefId + MobileConst.Comment)) {
                            data.put("Comment", entry.getValue());
                        } else {
                            //get the label with value
                            // find the selectbox
                            Element selectbox = FormUtil.findElement(entry.getKey().toString(), loadForm, formData);
                            // get options
                            FormRowSet tmp_rowset = (FormRowSet) selectbox.getProperty("options");
                            if (tmp_rowset != null) {
                                for (FormRow tmp_row : tmp_rowset) {
                                    if (entry.getValue().equals(tmp_row.getProperty("value"))) {
                                        data.put("Result", tmp_row.getProperty("label"));

                                    }
                                }
                            }

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
        //java.io.Writer write=response.getWriter();
        //writeJson(write, jsonObject, callback);
        //return;
        return jsonObject.toString();
    }

    protected String completeWithVariable(HttpServletRequest request, String activityId, AppService appService, WorkflowManager workflowManager) throws IOException, JSONException {

        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        FormRowSet rowSet = null;
        String formDefId = "";
        MobileUtil mu = new MobileUtil();

        //Setting Approve INFO
        if (assignment != null) {
            String processDefId = assignment.getProcessDefId();
            String activityDefId = assignment.getActivityDefId();
            String processId = assignment.getProcessId();
            AppDefinition appDef = appService.getAppDefinitionForWorkflowProcess(processId);


            /*
             //String formDefId = activityForm.getFormId();
             if (assignment.getProcessName().contains("费用报销")) {
             formDefId = "app_fd_wowprime_expense_approval";
             }
             if (assignment.getProcessName().contains("请购")) {
             formDefId = "app_fd_wowprime_product_approval";
             }
             */
            //if (!assignment.getProcessName().contains("请购") || !assignment.getProcessName().contains("费用报销")) {
            PackageActivityForm activityForm = appService.retrieveMappedForm(appDef.getAppId(), appDef.getVersion().toString(), processDefId, activityDefId);
            formDefId = activityForm.getFormId();
            //}

            String id = appService.getOriginProcessId(processId);

            //FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);


            FormRow row = null;
            Date currentDate = new Date();
            if (rowSet == null || rowSet.isEmpty()) {

                if (rowSet == null) {
                    rowSet = new FormRowSet();
                }
                row = new FormRow();
                row.setId(id);
                row.setDateModified(currentDate);
                row.setDateCreated(currentDate);
                rowSet.add(row);

            }

            Map<String, String> workflowApproveINFO = MobileUtil.retrieveApproveINFOFromRequest(request, formDefId);


            if (rowSet != null && !rowSet.isEmpty()) {
                row = rowSet.get(0);
                //setting approve time
                SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                row.setProperty(formDefId + "_time", dateformat1.format(currentDate));
                mu.setFormData(appDef.getAppId(), appDef.getVersion().toString(), id, formDefId, "c_"+formDefId + "_time", dateformat1.format(currentDate), "TEXT");
                Iterator<Map.Entry<String, String>> it = workflowApproveINFO.entrySet().iterator();
                while (it.hasNext()) {
                    //Setting approvment INFO
                    Map.Entry<String, String> entry = it.next();
                    //mu.setFormData(id, formDefId, entry.getKey(), entry.getValue(),"TEXT");
                    row.setProperty(entry.getKey(), entry.getValue());
                    mu.setFormData(appDef.getAppId(), appDef.getVersion().toString(), id, formDefId,MobileConst.column_prefix+entry.getKey(), entry.getValue(), "TEXT");
                    //设置审批人ID
                    if (entry.getKey().equals(formDefId + MobileConst.Approver)) {
                        row.setProperty(entry.getKey(), workflowManager.getWorkflowUserManager().getCurrentUsername());
                        mu.setFormData(appDef.getAppId(), appDef.getVersion().toString(), id, formDefId, MobileConst.column_prefix+entry.getKey(), workflowManager.getWorkflowUserManager().getCurrentUsername(), "TEXT");
                    }
                }
                mu.setFormData(appDef.getAppId(), appDef.getVersion().toString(), id, formDefId, "DateModified", "","DATE");
                mu.setFormData(appDef.getAppId(), appDef.getVersion().toString(), id, formDefId, "DateCreated", "","DATE");
                //mu.setFormData(id, formDefId, "DateModified", "","DATE");
                //mu.setFormData(id, formDefId, "DateCreated", "","DATE");
                // save to form

                //appService.storeFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, rowSet, id);
            }

        }


        //CompleteActivityWithVariables
        appService.getAppDefinitionForWorkflowActivity(activityId);

        if (assignment != null && !assignment.isAccepted()) {
            workflowManager.assignmentAccept(activityId);
            String processId = assignment.getProcessId();


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
            return jsonObject.toString();
        }

        return "assignment is null";

    }

    protected String getAssignmentPendingAndAcceptedList(String listType, AppService appService, WorkflowManager workflowManager) throws JSONException, IOException {
        PagedList<WorkflowAssignment> assignmentList = null;
        if (listType.equals("all")) {
            assignmentList = workflowManager.getAssignmentPendingAndAcceptedList(null, null, null, null, null, null, MobileConst.getrows);

        }
        if (listType.equals("pending")) {
            assignmentList = workflowManager.getAssignmentPendingAndAcceptedList(null, null, null, "dateCreated", true, null, MobileConst.getrows);

        }
        if (listType.equals("accepted")) {
            assignmentList = workflowManager.getAssignmentAcceptedList(null, "dateCreated", true, null, MobileConst.getrows);

        }

        Integer total = assignmentList.getTotal();
        JSONObject jsonObject = new JSONObject();



        for (WorkflowAssignment assignment : assignmentList) {

            MobileUtil mu = new MobileUtil();
            FormRow row = null;
            Collection<Employment> employments = null;
            Employment employment = null;
            WorkflowProcess workflowProcess = workflowManager.getRunningProcessById(assignment.getProcessId());
            Map data = new HashMap();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            userClass = userDao.getUserById(workflowProcess.getRequesterId());
            if (userClass != null) {
                employments = userClass.getEmployments();
            }
            //get only 1st employment record, currently only support 1 employment per user
            if (employments != null) {
                employment = employments.iterator().next();
            }

            data.put("processId", assignment.getProcessId());
            data.put("activityId", assignment.getActivityId());
            data.put("processName", assignment.getProcessName());
            data.put("activityName", assignment.getActivityName());
            data.put("processVersion", assignment.getProcessVersion());
            if (userClass != null) {
                data.put("requestor", userClass.getFirstName());
            } else {
                data.put("requestor", "");
            }
            if (employment != null) {
                data.put("department", employment.getDepartment().getName());
            } else {
                data.put("department", "");
            }
            data.put("dateCreated", dateFormat.format(assignment.getDateCreated()));
            data.put("acceptedStatus", assignment.isAccepted());
            data.put("due", assignment.getDueDate() != null ? assignment.getDueDate() : "-");
            /*
             double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(assignment.getActivityId());

             data.put("serviceLevelMonitor", WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor));
             */
            data.put("id", assignment.getActivityId());
            data.put("label", assignment.getActivityName());
            data.put("description", assignment.getDescription());
            if (!assignment.getProcessName().contains("费用报销") && !assignment.getProcessName().contains("请购") && !assignment.getProcessName().contains("加班")) {
                //if (!assignment.getProcessName().contains("请购") && !assignment.getProcessName().contains("加班")) {
                row = mu.getFormDataByActivityId(assignment.getActivityId());

            }
            if (row != null) {

                data.put("application_type", mu.getOpionValue("app_fd_wowprime_leave_type", "c_type", "id", row.getProperty(MobileConst.leaveType)));


            } else {

                if (assignment.getProcessName().contains("费用报销")) {
                    data.put("order_no", mu.getColumnValue(assignment.getProcessId(), "app_fd_wowprime_expense", "app_fd_wowprime_expense_approval", "c_jdeExpenseNo", "c_expenseId"));
                    DecimalFormat myformat1 = new DecimalFormat("###,###.00");
                    String amount = "";
                    try {
                        amount = myformat1.format(Double.parseDouble(mu.getColumnValue(assignment.getProcessId(), "app_fd_wowprime_expense", "app_fd_wowprime_expense_approval", "c_totalprice", "c_expenseId")));
                    } catch (Exception ex) {
                    }
                    data.put("order_price", amount);
                    data.put("application_type", "费用报销");
                    data.put("company_name", "");
                } else if (assignment.getProcessName().contains("请购")) {
                    DecimalFormat myformat1 = new DecimalFormat("###,###.00");
                    String amount = "";
                    try {
                        amount = myformat1.format(Double.parseDouble(mu.getColumnValue(assignment.getProcessId(), "app_fd_wowprime_product", "app_fd_wowprime_product_approval", "c_totalprice", "productId")));
                    } catch (Exception ex) {
                    }
                    data.put("order_no", mu.getColumnValue(assignment.getProcessId(), "app_fd_wowprime_product", "app_fd_wowprime_product_approval", "c_jdeExpenseNo", "productId"));
                    data.put("order_price", amount);
                    data.put("company_name", mu.getColumnValue(assignment.getProcessId(), "app_fd_wowprime_product", "app_fd_wowprime_product_approval", "c_companyName", "productId"));
                    data.put("application_type", "请购");

                } else if (assignment.getProcessName().contains("加班")) {
                    data.put("order_no", "");
                    data.put("order_price", "");
                    data.put("company_name", "");
                    data.put("application_type", "加班");
                }


            }
            jsonObject.accumulate("data", data);
        }

        jsonObject.accumulate("total", total);
        jsonObject.accumulate("start", "");
        jsonObject.accumulate("sort", "");
        jsonObject.accumulate("desc", "");
        return jsonObject.toString();
    }
}
