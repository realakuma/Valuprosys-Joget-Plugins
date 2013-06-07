package org.joget.valuprosys.mobile.model;
import org.joget.valuprosys.mobile.model.*;

import java.io.Serializable;
import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class CusUser implements Serializable{

    @NotBlank
    @RegExp(value = "^[0-9a-zA-Z_-]+$")
    private String id;
    @NotBlank
    private String userId;
    private String userName;
    private Date dateCreated;
    private Date dateModified;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}


