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
import org.joget.valuprosys.mobile.dao.MobileDao;
import org.joget.valuprosys.mobile.model.Mobile;

public class MobileNotificationsPush extends DefaultApplicationPlugin {

    private MobileDao mobiledao;

    @Override
    public Object execute(Map props) {
        String masterSecret = "1fe1abbbe75968a850a48684";
        String appKey = "0875a71ec3adec567f6dc348";
        //Get FormDefId from properties
        String formDefId = (String) props.get("formDefId");

        //Get record Id from process
        WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String id = appService.getOriginProcessId(wfAssignment.getProcessId());
        String fieldName = "";
        //Check whether record Id is configured to different value in properties grid
        wfAssignment.getAssigneeName();


        String tag = "ffffffff_8be5_dfa9_ffff_ffff99d603a9";

        mobiledao = (MobileDao) AppContext.getInstance().getAppContext().getBean("MobileDao");
        Collection<Mobile> mobiles = mobiledao.getMobileDeviceByUser(wfAssignment.getAssigneeId());
        for (Mobile tm : mobiles) {
            JPushClient jpush = new JPushClient(masterSecret, appKey);
            jpush.sendNotificationWithTag(1, tm.getDeviceNo().replace("-", "_"), "测试", "java api");
        }


        //Load the original Form Data record
        AppDefinition appDef = (AppDefinition) props.get("appDef");
        FormRow row = new FormRow();
        FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
        if (!rowSet.isEmpty()) {
            row = rowSet.get(0);
        }





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
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }
}