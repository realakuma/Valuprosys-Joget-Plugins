package org.joget.valuprosys.mobile.dao;

import java.util.Collection;
import org.joget.valuprosys.mobile.model.Mobile;

public interface MobileDao {

    Boolean addMobileDevice(Mobile products);

    Boolean updateMobileDevice(Mobile products);

    Boolean deleteMobileDevice(String id);

    Collection<Mobile> getMobileDeviceByUser(String userId);

    Mobile getMobileDeviceById(String Id);
}
