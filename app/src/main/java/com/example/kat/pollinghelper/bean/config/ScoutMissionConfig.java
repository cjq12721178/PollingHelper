package com.example.kat.pollinghelper.bean.config;

import java.util.ArrayList;

/**
 * Created by KAT on 2016/5/6.
 */
public class ScoutMissionConfig {
    public ScoutMissionConfig(String name) {
        this.name = name;
        items = new ArrayList<>();
    }

    public ScoutMissionConfig() {
        this("");
    }

    public byte[] getDeviceImageData() {
        return deviceImageData;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ScoutItemConfig> getItems() {
        return items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceImageData(byte[] deviceImageData) {
        this.deviceImageData = deviceImageData;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String name;
    private String description;
    private byte[] deviceImageData;
    private ArrayList<ScoutItemConfig> items;
}
