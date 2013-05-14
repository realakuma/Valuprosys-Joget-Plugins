package org.joget.valuprosys.mobile.model;

import java.io.Serializable;
import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class Notification implements Serializable{

    @NotBlank
    @RegExp(value = "^[0-9a-zA-Z_-]+$")
    private String id;
    @NotBlank
    private int NotificationId;
    private String NotificationSender;
    private String NotificationReceiver;
    private String NotificationContent;
    private Date dateCreated;
    private Date dateModified;
    public int NotificationId() {
        return NotificationId;
    }

    public void setId(String id) {
        this.NotificationId = NotificationId;
    }

    public String getNotificationSender() {
        return NotificationSender;
    }

    public void setNotificationSender(String NotificationSender) {
        this.NotificationSender = NotificationSender;
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

    public String getNotificationReceiver() {
        return NotificationReceiver;
    }

    public void setNotificationReceiver(String NotificationReceiver) {
        this.NotificationReceiver = NotificationReceiver;
    }

    public String getNotificationContent() {
        return NotificationContent;
    }

    public void setNotificationContent(String NotificationContent) {
        this.NotificationContent = NotificationContent;
    }
}
