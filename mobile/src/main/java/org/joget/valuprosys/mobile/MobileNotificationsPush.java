/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

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

public class MobileNotificationsPush extends DefaultApplicationPlugin {

    @Override
    public Object execute(Map props) {
        //Get FormDefId from properties
        String formDefId = (String) props.get("formDefId");

        //Get record Id from process
        WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String id = appService.getOriginProcessId(wfAssignment.getProcessId());
        String fieldName = "";
        //Check whether record Id is configured to different value in properties grid
        wfAssignment.getAssigneeName();
        Object[] fields = (Object[]) props.get("fields");
        for (Object o : fields) {
            Map mapping = (HashMap) o;
            fieldName = mapping.get("field").toString();
            if (FormUtil.PROPERTY_ID.equals(fieldName)) {
                String fieldValue = mapping.get("value").toString();
                id = fieldValue;
                break;
            }
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