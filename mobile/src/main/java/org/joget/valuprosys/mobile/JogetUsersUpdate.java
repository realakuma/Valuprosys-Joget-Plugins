/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import java.util.ArrayList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.joget.commons.util.StringUtil;
import org.joget.directory.model.User;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.plugin.property.service.PropertyUtil;

/**
 *
 * @author realakuma
 */
public class JogetUsersUpdate extends DefaultApplicationPlugin {

    private DirectoryManager directoryManager;
    private User user;
    private UserDao userDao;
    private RoleDao roleDao;

    @Override
    public Object execute(Map props) {

        PluginManager pluginManager = (PluginManager) props.get("pluginManager");
        directoryManager = (DirectoryManager) pluginManager.getBean("directoryManager");
        userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        roleDao = (RoleDao) AppUtil.getApplicationContext().getBean("roleDao");


        /*
        String search_base = (String) props.get("search_base");
        String security_principal = (String) props.get("security_principal");
        String security_credentials=(String) props.get("security_credentials");
        String ldap_url=(String) props.get("ldap_url");
        ArrayList list = new ArrayList();
        ldapTest lt=new ldapTest();
         */
        try {

            HttpClient client = new HttpClient();

            //jsonUrl = WorkflowUtil.processVariable(jsonUrl, "", wfAssignment);
            String jsonUrl = (String) props.get("jsonUrl");
            GetMethod get = null;

            jsonUrl = StringUtil.encodeUrlParam(jsonUrl);

            get = new GetMethod(jsonUrl);
            client.executeMethod(get);
            InputStream in = get.getResponseBodyAsStream();
            String jsonResponse = streamToString(in);

            Map object = PropertyUtil.getPropertiesValueFromJson(jsonResponse);

            //获得AD中的用户
        /*
            list=lt.getADuserList(security_principal, security_credentials, ldap_url, search_base)
            
            //添加用户
            for (int i=0;i<list.size();i++)
            {
            
            user=directoryManager.getUserByUsername(list.get(i).toString());
            if (user==null){
            addUser(list.get(i).toString());
            }
             * 
            
            }*/
        } catch (Exception e) {
            LogUtil.error(this.getName(), e, e.getMessage());
        } finally {
        }

        return null;

    }

    public String getName() {
        return "Valuprosys Joget Wow users  Synchronize Tool";
    }

    public String getVersion() {
        return "3.1.0";
    }

    public String getDescription() {
        return "Synchronize User Info From JSON API";
    }

    public String getLabel() {
        return "Valuprosys Joget Wow users  Synchronize Tool";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/JogetUsersUpdate.json", null, true, "/messages/JogetUsersUpdate_zh_CN");
    }

    protected void addUser(String username) {
        User user = new User();
        Set roles = new HashSet();
        roles.add(roleDao.getRole("ROLE_USER"));
        user.setActive(1);
        user.setId(username);
        user.setUsername(username);
        user.setFirstName(username);
        user.setRoles(roles);
        userDao.addUser(user);
    }

    protected String streamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LogUtil.error(getClass().getName(), e, "");
            }
        }

        return sb.toString();
    }
}
