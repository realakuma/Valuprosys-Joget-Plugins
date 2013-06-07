/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.joget.commons.util.StringUtil;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.model.User;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Employment;
import org.joget.plugin.property.service.PropertyUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.model.Organization;
import org.joget.directory.model.Department;
import org.joget.directory.dao.DepartmentDao;
import org.joget.valuprosys.mobile.dao.CusDeptDao;
import org.joget.valuprosys.mobile.model.CusDept;
import org.joget.valuprosys.mobile.dao.CusUserDao;
import org.joget.valuprosys.mobile.model.CusUser;

/**
 *
 * @author realakuma
 */
public class JogetUsersUpdate extends DefaultApplicationPlugin {

    private DirectoryManager directoryManager;
    private User user;
    private Organization organization;
    private UserDao userDao;
    private RoleDao roleDao;
    private Department department;
    private EmploymentDao employmentDao;
    private OrganizationDao organizationDao;
    private DepartmentDao departmentDao;
    private CusUserDao cusUserDao;
    private CusUser cusUser;
    private CusDeptDao cusDeptDao;
    private CusDept cusDept;
    private String getJobUrl = "http://workflow.wowprime.com.cn:88/GetJob";

    @Override
    public Object execute(Map props) {

        PluginManager pluginManager = (PluginManager) props.get("pluginManager");
        directoryManager = (DirectoryManager) pluginManager.getBean("directoryManager");
        userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        roleDao = (RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
        employmentDao = (EmploymentDao) AppUtil.getApplicationContext().getBean("employmentDao");
        organizationDao = (OrganizationDao) AppUtil.getApplicationContext().getBean("organizationDao");
        departmentDao = (DepartmentDao) AppUtil.getApplicationContext().getBean("departmentDao");
        cusDeptDao = (CusDeptDao) AppContext.getInstance().getAppContext().getBean("CusDeptDao");
        cusUserDao = (CusUserDao) AppContext.getInstance().getAppContext().getBean("CusUserDao");
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
            client.getParams().setParameter(
                    HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF8");
            //jsonUrl = WorkflowUtil.processVariable(jsonUrl, "", wfAssignment);
            String jsonUrl = (String) props.get("jsonUrl");
            String import_type = (String) props.get("import_type");

            GetMethod get = null;

            jsonUrl = StringUtil.encodeUrlParam(jsonUrl);


            get = new GetMethod(jsonUrl);



            client.executeMethod(get);

            InputStream in = get.getResponseBodyAsStream();
            String jsonResponse = streamToString(in);
            /*
            if (!jsonResponse.startsWith("[")) {
            jsonResponse = "[" + jsonResponse + "]";
            }
             */
            JSONObject jsonObjSplit = JSONObject.fromObject(jsonResponse);

//find data
            JSONArray jsonEmpArray = jsonObjSplit.getJSONArray("data");
            for (int i = 0; i < jsonEmpArray.size(); i++) {

                if (import_type.equals("GetEmp")) {
                    JSONObject jo = (JSONObject) jsonEmpArray.get(i);
                    user = directoryManager.getUserByUsername(jo.get("email").toString().substring(0, jo.get("email").toString().indexOf("@")));
                    if (user == null) {
                        addUser(jo.get("empName").toString(), jo.get("empHrNo").toString(), "", jo.get("email").toString(), jo.get("empDept").toString(), jo.get("empOrg").toString());
                    }

                    cusUser = cusUserDao.getUserById(jo.get("email").toString().substring(0, jo.get("email").toString().indexOf("@")));
                    if (cusUser == null) {
                        addCusUser(jo.get("empName").toString(), jo.get("empNo").toString(), "", jo.get("email").toString(), "", "");
                    }
                }
                if (import_type.equals("GetOrg")) {
                    JSONObject jo = (JSONObject) jsonEmpArray.get(i);
                    department = departmentDao.getDepartment(jo.get("orgID").toString());
                    if (department == null) {
                        addDepartment(jo.get("deptId").toString(), jo.get("orgName").toString(), jo.get("orgId").toString());
                    }
                    cusDept = cusDeptDao.getDeptById(jo.get("orgID").toString());
                    if (cusDept == null) {
                        addCusDept(jo.get("deptId").toString(), jo.get("orgName").toString(), jo.get("orgId").toString());
                    }

                }
                if (import_type.equals("GetOrgInfo")) {
                    JSONObject jo = (JSONObject) jsonEmpArray.get(i);
                    organization = organizationDao.getOrganization(jo.get("orgIdHR").toString());
                    if (organization == null) {
                        addOrganization(jo.get("orgID").toString(), jo.get("orgName").toString());
                    }


                }
            }


//            Map object = PropertyUtil.getPropertiesValueFromJson(jsonResponse);



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

    protected void addUser(String username, String EmployeeCode, String jobTitle, String Email, String deptId, String orgId) throws IOException {
        User user = new User();
        Set roles = new HashSet();
        Employment emp = new Employment();
        roles.add(roleDao.getRole("ROLE_USER"));
        user.setActive(1);
        user.setId(Email.substring(0, Email.indexOf("@")));
        user.setUsername(Email.substring(0, Email.indexOf("@")));
        user.setFirstName(username);
        user.setRoles(roles);
        user.setEmail(Email);
        emp.setEmployeeCode(EmployeeCode);
        emp.setDepartmentId(deptId);
        HttpClient client = new HttpClient();
        GetMethod get = null;
        getJobUrl = StringUtil.encodeUrlParam(getJobUrl);
        get = new GetMethod(getJobUrl+"?JobNo="+jobTitle);

        client.executeMethod(get);

        InputStream in = get.getResponseBodyAsStream();
        String jsonResponse = streamToString(in);

        /*
        if (!jsonResponse.startsWith("[")) {
        jsonResponse = "[" + jsonResponse + "]";
        }
         */
        JSONObject jsonObjSplit = JSONObject.fromObject(jsonResponse);
        JSONArray jsonEmpArray = jsonObjSplit.getJSONArray("data");
        for (int i = 0; i < jsonEmpArray.size(); i++) {
            JSONObject jo = (JSONObject) jsonEmpArray.get(i);
            emp.setRole(jo.get("JobName").toString());
        }
        emp.setOrganizationId(orgId);
        emp.setUserId(Email.substring(0, Email.indexOf("@")));
        userDao.addUser(user);
        employmentDao.addEmployment(emp);


    }

    protected void addCusUser(String username, String EmployeeCode, String jobTitle, String Email, String deptId, String orgId) {
        CusUser cusUser = new CusUser();

        cusUser.setId(Email.substring(0, Email.indexOf("@")));
        cusUser.setUserId(Email.substring(0, Email.indexOf("@")));
        cusUser.setUserName(username);
        cusUser.setDateCreated(new java.util.Date());
        cusUser.setDateModified(new java.util.Date());
        cusUserDao.addUser(cusUser);
    }

    protected String streamToString(InputStream in) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
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

    protected void addOrganization(String orgId, String orgName) {
        Organization organization = new Organization();
        organization.setId(orgId);
        organization.setName(orgName);
        organization.setDescription(null);

        organizationDao.addOrganization(organization);
    }

    protected void addDepartment(String deptId, String deptName, String orgId) {
        Department department = new Department();
        department.setOrganization(organizationDao.getOrganization(orgId));
        department.setId(deptId);
        department.setName(deptName);
        department.setDescription(null);
        departmentDao.addDepartment(department);
    }

    protected void addCusDept(String deptId, String deptName, String orgId) {
        CusDept cusDept = new CusDept();
        cusDept.setDeptId(deptId);
        cusDept.setDeptName(deptName);
        cusDept.setId(deptId);
        cusDept.setDateCreated(new java.util.Date());
        cusDept.setDateModified(new java.util.Date());
        cusDeptDao.addDept(cusDept);

    }
}
