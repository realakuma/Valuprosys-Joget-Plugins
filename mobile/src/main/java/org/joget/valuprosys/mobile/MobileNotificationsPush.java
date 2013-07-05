/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import cn.jpush.api.JPushClient;
import cn.jpush.api.DeviceEnum;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Employment;
import org.joget.directory.model.User;
import org.joget.valuprosys.mobile.dao.MobileDao;
import org.joget.valuprosys.mobile.model.Mobile;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;

public class MobileNotificationsPush extends DefaultApplicationPlugin {

    private MobileDao mobiledao;
    private UserDao userDao;
    private User userClass;

    @Override
    public Object execute(Map props) {
        String masterSecret = "1fe1abbbe75968a850a48684";
        String appKey = "0875a71ec3adec567f6dc348";
        String sendNo = "";
        //Get FormDefId from properties
        String formDefId = (String) props.get("formDefId");
        String toParticipantId = (String) props.get("toParticipantId");
        String enabled = (String) props.get("enabled");


        if (!enabled.equals("yes")) {
            return null;
        }

        //init compantents
        mobiledao = (MobileDao) AppContext.getInstance().getAppContext().getBean("MobileDao");
        userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");

        AppDefinition appDef = (AppDefinition) props.get("appDef");
        masterSecret = MobileUtil.getEnvVarByName("masterSecret", appDef, environmentVariableDao);
        appKey = MobileUtil.getEnvVarByName("appKey", appDef, environmentVariableDao);
        sendNo = MobileUtil.getEnvVarByName("sendNo", appDef, environmentVariableDao);


        String id = appService.getOriginProcessId(wfAssignment.getProcessId());
        WorkflowProcess workflowProcess = workflowManager.getRunningProcessById(wfAssignment.getProcessId());
        Collection<Mobile> mobiles = null;
        //FormRow row = this.mobileutil.getFormDataByActivityId(wfAssignment.getActivityId());
        //String tag = "ffffffff_8be5_dfa9_ffff_ffff99d603a9";
        //get form data
        FormRow row = new FormRow();
        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
        if (!rowSet.isEmpty()) {
            row = rowSet.get(0);
        }

        Collection<String> userList = null;
        userList = WorkflowUtil.getAssignmentUsers(workflowProcess.getPackageId(), wfAssignment.getProcessDefId(), wfAssignment.getProcessId(), wfAssignment.getProcessVersion(), wfAssignment.getActivityId(), "", toParticipantId);

        for (String user : userList) {
            mobiles = mobiledao.getMobileDeviceByUser(user);
            userClass = userDao.getUserById(workflowProcess.getRequesterId());

            Collection<Employment> employments = userClass.getEmployments();

            //get only 1st employment record, currently only support 1 employment per user
            Employment employment = employments.iterator().next();

            for (Mobile tm : mobiles) {
                MobileUtil mu = new MobileUtil();
                
                LogUtil.info("userId:", tm.getUserId());
                LogUtil.info("deviceNo:", tm.getDeviceNo());
                if (wfAssignment.getProcessName().contains("请假")) {
                    if (tm.getDeviceType().equals(MobileConst.Andriod)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.Android);
                        jpush.sendNotificationWithTag(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + mu.getOpionValue("app_fd_wowprime_leave_type", "c_type", "id", row.getProperty(MobileConst.leaveType)));
                    }
                    if (tm.getDeviceType().equals(MobileConst.IOS)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.IOS);
                        jpush.sendNotificationWithAlias(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + mu.getOpionValue("app_fd_wowprime_leave_type", "c_type", "id", row.getProperty(MobileConst.leaveType)));

                    }
                }
                if (wfAssignment.getProcessName().contains("加班")) {
                    if (tm.getDeviceType() .equals(MobileConst.Andriod)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.Android);
                        jpush.sendNotificationWithTag(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "加班");
                    }
                    if (tm.getDeviceType().equals(MobileConst.IOS)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.IOS);
                        jpush.sendNotificationWithAlias(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "加班");

                    }
                }
                if (wfAssignment.getProcessName().contains("费用")) {
                    if (tm.getDeviceType().equals(MobileConst.Andriod)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.Android);
                        jpush.sendNotificationWithTag(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "费用报销");
                    }
                    if (tm.getDeviceType() .equals(MobileConst.IOS)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.IOS);
                        jpush.sendNotificationWithAlias(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "费用报销");

                    }
                }
                if (wfAssignment.getProcessName().contains("请购")) {
                    if (tm.getDeviceType().equals(MobileConst.Andriod)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.Android);
                        jpush.sendNotificationWithTag(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "请购");
                    }
                    if (tm.getDeviceType() .equals(MobileConst.IOS)) {
                        JPushClient jpush = new JPushClient(masterSecret, appKey,DeviceEnum.IOS);
                        jpush.sendNotificationWithAlias(Integer.parseInt(sendNo), tm.getDeviceNo(), "来自" + employment.getDepartment().getName() + "的" + userClass.getFirstName() + "的审批请求", "申请类型:" + "请购");

                    }
                }
                //sendNo++
                MobileUtil.setEnvVar("sendNo", String.valueOf(Integer.parseInt(sendNo) + 1), appDef, environmentVariableDao);

            }
        }








        //Load the original Form Data record
        /*
        AppDefinition appDef = (AppDefinition) props.get("appDef");
        FormRow row = new FormRow();
        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
        if (!rowSet.isEmpty()) {
        row = rowSet.get(0);
        }
         * */






        return null;

    }

    public String getName() {
        return "Mobile Notifications Push Tool";
    }

    public String getVersion() {
        return "3.1.0";
    }

    public String getDescription() {
        return "Push Notificaitons to Assignee";
    }

    public String getLabel() {
        return "Mobile Notifications Push Tool";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/NotificationPush.json", null, true, "");
    }
}