package com.example.kat.pollinghelper.structure.config;

/**
 * Created by KAT on 2016/5/4.
 */
public class ScoutSensorConfig {
    public ScoutSensorConfig(String name) {
        this.name = name;
    }

    public ScoutSensorConfig() {
        this("");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return getName();
    }

    private String name;
    private String description;
    private String address;
}
