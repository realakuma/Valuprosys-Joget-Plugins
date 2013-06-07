package org.joget.valuprosys.mobile.dao;
import org.joget.valuprosys.mobile.dao.CusUserDao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.valuprosys.mobile.model.CusDept;


public class CusDeptDaoImpl extends AbstractSpringDao implements CusDeptDao {


    public Boolean addDept(CusDept cusdept) {
        try {
            save("CusDept", cusdept);
            return true;
        } catch (Exception e) {
            LogUtil.error(CusDeptDaoImpl.class.getName(), e, "Add Dept Error!");
            return false;
        }
    }

    public Boolean updateDept(CusDept cusdept) {
        try {
            merge("CusDept", cusdept);
            return true;
        } catch (Exception e) {
            LogUtil.error(CusDeptDaoImpl.class.getName(), e, "Update Dept Error!");
            return false;
        }
    }

    public Boolean deleteDept(String id) {
        try {
            CusDept cusdept = getDeptById(id);
            if (cusdept != null) {
          

                delete("CusDept", cusdept);
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(CusDeptDaoImpl.class.getName(), e, "Delete Dept Error!");
            return false;
        }
    }

   

    

    public CusDept getDeptById(String Id) {
          try {
            return (CusDept) find("CusDept", Id);
        } catch (Exception e) {
            LogUtil.error(CusDeptDaoImpl.class.getName(), e, "Get Dept Error!");
            return null;
        }
    }

 
  
}
