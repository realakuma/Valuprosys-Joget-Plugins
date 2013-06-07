package org.joget.valuprosys.mobile.dao;
import org.joget.valuprosys.mobile.model.CusDept;
import org.joget.valuprosys.mobile.dao.*;
import org.joget.valuprosys.mobile.model.*;
import java.util.Collection;


public interface CusDeptDao {

    Boolean addDept(CusDept cususer);

    Boolean updateDept(CusDept cususer);

    Boolean deleteDept(String id);

    CusDept getDeptById(String Id);
}
