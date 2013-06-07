package org.joget.valuprosys.mobile.dao;

import org.joget.valuprosys.mobile.model.CusUser;

public interface CusUserDao {

    Boolean addUser(CusUser cususer);

    Boolean updateUser(CusUser cususer);

    Boolean deleteUser(String id);

    CusUser getUserById(String Id);
}
