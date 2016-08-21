package com.jodelXposed.models;

public class UDI {

    public boolean active = false;
    public String udi = "";

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }
}
