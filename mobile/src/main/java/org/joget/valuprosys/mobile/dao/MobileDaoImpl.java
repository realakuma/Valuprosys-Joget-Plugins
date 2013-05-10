package org.joget.valuprosys.mobile.dao;

import java.util.Collection;
import java.util.List;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.valuprosys.mobile.model.Mobile;


public class MobileDaoImpl extends AbstractSpringDao implements MobileDao {


    public Boolean addMobileDevice(Mobile mobile) {
        try {
            save("Mobile", mobile);
            return true;
        } catch (Exception e) {
            LogUtil.error(MobileDaoImpl.class.getName(), e, "Add Mobile Device Error!");
            return false;
        }
    }

    public Boolean updateMobileDevice(Mobile mobile) {
        try {
            merge("Mobile", mobile);
            return true;
        } catch (Exception e) {
            LogUtil.error(MobileDaoImpl.class.getName(), e, "Update mobile Device Error!");
            return false;
        }
    }

    public Boolean deleteMobileDevice(String id) {
        try {
            Mobile product = getMobileDeviceById(id);
            if (product != null) {
          

                delete("Mobile", product);
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(MobileDaoImpl.class.getName(), e, "Delete Product Error!");
            return false;
        }
    }

   

    public Collection<Mobile> getMobileDeviceByUser(String userId) {
        try {
            Mobile mobile = new Mobile();
            mobile.setUserId(userId);
            List mobiles = findByExample("Mobile", mobile);

           return mobiles;
        } catch (Exception e) {
            LogUtil.error(MobileDaoImpl.class.getName(), e, "Get MobileDevice By user Error!");
        }

        return null;
    }

    

    public Mobile getMobileDeviceById(String Id) {
          try {
            return (Mobile) find("Mobile", Id);
        } catch (Exception e) {
            LogUtil.error(MobileDaoImpl.class.getName(), e, "Get Product Error!");
            return null;
        }
    }

  
}
