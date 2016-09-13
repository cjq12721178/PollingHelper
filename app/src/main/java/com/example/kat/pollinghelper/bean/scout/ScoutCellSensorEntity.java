package com.example.kat.pollinghelper.bean.scout;

import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;

/**
 * Created by KAT on 2016/5/27.
 * 注意，此类虽然和他的邻居们长的很像，但其实还是有本质差别的
 */
public class ScoutCellSensorEntity implements ScoutEntity {

    public ScoutCellSensorEntity(ScoutSensorConfig sensorConfig) {
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

    public ScoutSensorConfig getSensorConfig() {
        return sensorConfig;
    }

    public ScoutCellState getState() {
        return state;
    }

    public void setState(ScoutCellState state) {
        this.state = state;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean checked;
    private ScoutCellState state;
    private ScoutSensorConfig sensorConfig;
    private String name;
}
