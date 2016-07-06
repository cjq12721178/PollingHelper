package com.example.kat.pollinghelper.ui.structure;

import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;

/**
 * Created by KAT on 2016/5/27.
 * 注意，此类虽然和他的邻居们长的很像，但其实还是有本质差别的
 */
public class PollingConfigListItemSensorEntity {

    public PollingConfigListItemSensorEntity(PollingSensorConfig sensorConfig) {
        this.sensorConfig = sensorConfig;
        setName(sensorConfig.getName());
        setChecked(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PollingSensorConfig getSensorConfig() {
        return sensorConfig;
    }

    public PollingConfigState getState() {
        return state;
    }

    public void setState(PollingConfigState state) {
        this.state = state;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean checked;
    private PollingConfigState state;
    private PollingSensorConfig sensorConfig;
    private String name;
}
