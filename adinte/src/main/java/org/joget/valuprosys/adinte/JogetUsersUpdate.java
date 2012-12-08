/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.adinte;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import java.util.ArrayList;
import org.joget.directory.model.User;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
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
        userDao=(UserDao) AppUtil.getApplicationContext().getBean("userDao");
        roleDao=(RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
        String search_base = (String) props.get("search_base");
        String security_principal = (String) props.get("security_principal");
        String security_credentials=(String) props.get("security_credentials");
        String ldap_url=(String) props.get("ldap_url");
        ArrayList list = new ArrayList();
        ldapTest lt=new ldapTest();
    
       try
    {
        //获得AD中的用户
       list=lt.getADuserList(security_principal, security_credentials, ldap_url, search_base);
       //添加用户
       for (int i=0;i<list.size();i++)
       {

           user=directoryManager.getUserByUsername(list.get(i).toString());
           if (user==null){
           addUser(list.get(i).toString());
           }
       }
    } catch (Exception e) {
      LogUtil.error(this.getName(), e, e.getMessage());
    }
    finally
    {
    }
     
        return null;
     
    }

    public String getName() {
        return "Joget Active Directory Synchronize Tool";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Synchronize User Info From AD to Joget";
    }

    public String getLabel() {
        return "Joget Active Directory Synchronize Tool";
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
}
