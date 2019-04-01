package com.example.haniup;

import java.io.Serializable;

public class UserObject implements Serializable {

private String uid , name , phone , notificationKey;
private Boolean selected = false;



    public UserObject(String uid) {
        this.uid = uid;
    }

    public UserObject(String uid , String phone , String name) {
        this.uid = uid;

        this.phone = phone;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}

