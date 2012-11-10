/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.plugins;

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

import weibo4j.util.WeiboConfig;
import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;
import weibo4j.Comments;

public class WeiboSubmit extends DefaultApplicationPlugin {

    @Override
    public Object execute(Map props) {
       Timeline tm = new Timeline();
       Comments cm = new Comments();
        //Get FormDefId from properties
        String formDefId = (String) props.get("formDefId");

        //Get record Id from process
        WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String id = appService.getOriginProcessId(wfAssignment.getProcessId());
 String fieldName="";
        //Check whether record Id is configured to different value in properties grid
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
        
        //submit Weibo
//        LogUtil.info("WeiboSubmit", Thread.currentThread().getContextClassLoader().toString());
//        Properties props_test = new Properties(); 
//        	try {
//			props_test.load(WeiboSubmit.class.getResourceAsStream("wb_config.properties"));
//                       LogUtil.info("props_test", props_test.getProperty("baseURL")); 
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        String access_token =WeiboConfig.getValue("access_token");
		//String statuses = row.getProperty("weibo_content","value")
        LogUtil.info("access_token", access_token);
        String statuses = row.getProperty(fieldName);
        String [] str_weibo_content=statuses.split("!@@!");
        
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
        //发微博
        if (str_weibo_content[1].trim().equals(""))
        {
        	
		try {
			Status status = tm.UpdateStatus(statuses);
		} catch (WeiboException e) {
			e.printStackTrace();
		}    
        }
        //转发微博
        if (str_weibo_content[1].trim().equals("repost"))
        {
      		try {
                            tm.Repost(str_weibo_content[2].trim(),str_weibo_content[0].trim(),0);
		} catch (WeiboException e) {
			e.printStackTrace();
		} 
        }
        //评论微博
        if (str_weibo_content[1].trim().equals("comment"))
        {
      		try {
                    
                   cm.createComment(str_weibo_content[0].trim(),str_weibo_content[2].trim());
		} catch (WeiboException e) {
			e.printStackTrace();
		} 

        }
		
        /*
        //Set the updated fields and it抯 value to the loaded data
        for (Object o : fields) {
            Map mapping = (HashMap) o;
            String fieldName = mapping.get("field").toString();
            String fieldValue = mapping.get("value").toString();
            row.setProperty(fieldName, fieldValue);
        }

        //Store the updated data
        rowSet.set(0, row);
        appService.storeFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, rowSet, id);
        */
        return null;
     
    }

    public String getName() {
        return "Weibo Submit Tool";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Submit A Weibo in a process.";
    }

    public String getLabel() {
        return "Weibo Submit Tool";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/formDataUpdateTool.json", null, true, "/messages/formDataUpdateTool"); 
    }
}