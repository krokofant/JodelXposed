package com.jodelXposed.models;

import java.util.List;

public class Beta {

    private List<String> notificationList;

    public void setNotificationList(List<String> notificationList) {
        this.notificationList = notificationList;
    }

    public List<String> getNotificationList() {
        return notificationList;
    }

    public boolean active = false;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
