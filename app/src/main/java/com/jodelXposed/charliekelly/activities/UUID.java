package com.jodelXposed.charliekelly.activities;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13.04.2016.
 */
public class UUID extends SugarRecord {
    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID(String UUID, String name) {
        this.UUID = UUID;
        this.name = name;
    }

    public UUID() {
    }

    private String UUID;
    private String name;
}
