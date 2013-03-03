/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.products;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.base.PluginWebSupport;
import java.util.ArrayList;
import org.joget.valuprosys.products.dao.ProductsDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.joget.valuprosys.products.model.Products;

/**
 *
 * @author realakuma
 */
public class ProductsApi extends DefaultApplicationPlugin implements PluginWebSupport{
    private DirectoryManager directoryManager;
    private ProductsDao productdao;
    private Products product;
    
    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     productdao=(ProductsDao) AppUtil.getApplicationContext().getBean("productsDao");
     
     product=productdao.getProducts("abc");
        // Get Parameter
        String text = request.getParameter("say_something");


        // Write to response
        response.getWriter().write(text);
    }
   
    @Override
    public Object execute(Map props) {
     /*
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
     */
        return null;
     
    }
    public String getName() {
        return "Joget Get Products INFO API";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Get Products INFO";
    }

    public String getLabel() {
        return "Joget Get Products INFO API";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/JogetUsersUpdate.json", null, true, "/messages/JogetUsersUpdate_zh_CN"); 
    }
    /*
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
    */
}
