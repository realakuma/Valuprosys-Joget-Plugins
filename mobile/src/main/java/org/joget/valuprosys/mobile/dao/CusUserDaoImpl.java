package org.joget.valuprosys.mobile.dao;

import java.util.Collection;
import java.util.List;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.valuprosys.mobile.model.CusUser;


public class CusUserDaoImpl extends AbstractSpringDao implements CusUserDao {


    public Boolean addUser(CusUser cususer) {
        try {
            save("CusUser", cususer);
            return true;
        } catch (Exception e) {
            LogUtil.error(CusUserDaoImpl.class.getName(), e, "Add User Error!");
            return false;
        }
    }

    public Boolean updateUser(CusUser cususer) {
        try {
            merge("CusUser", cususer);
            return true;
        } catch (Exception e) {
            LogUtil.error(CusUserDaoImpl.class.getName(), e, "Update User Error!");
            return false;
        }
    }

    public Boolean deleteUser(String id) {
        try {
            CusUser cususer = getUserById(id);
            if (cususer != null) {
          

                delete("CusUser", cususer);
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(CusUserDaoImpl.class.getName(), e, "Delete User Error!");
            return false;
        }
    }

   

    

    public CusUser getUserById(String Id) {
          try {
            return (CusUser) find("CusUser", Id);
        } catch (Exception e) {
            LogUtil.error(CusUserDaoImpl.class.getName(), e, "Get User Error!");
            return null;
        }
    }

  
}
