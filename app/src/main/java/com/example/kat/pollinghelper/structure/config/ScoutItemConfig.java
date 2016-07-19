package com.example.kat.pollinghelper.structure.config;

import java.util.Date;

/**
 * Created by KAT on 2016/5/9.
 */
public class ScoutItemConfig {
    public ScoutItemConfig(long id) {
        this.id = id;
    }

    public ScoutItemConfig() {
        this(new Date().getTime());
    }

    public String getMeasureName() {
        return measureName;
    }

    public String getDescription() {
        return description;
    }

    public ScoutSensorConfig getSensor() {
        return sensor;
    }

    public double getUpAlarm() {
        return upAlarm;
    }

    public double getDownAlarm() {
        return downAlarm;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSensor(ScoutSensorConfig sensor) {
        this.sensor = sensor;
    }

    public void setUpAlarm(double upAlarm) {
        this.upAlarm = upAlarm;
    }

    public void setDownAlarm(double downAlarm) {
        this.downAlarm = downAlarm;
    }

    public long getId() {
        return id;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    private final long id;
    private String measureName;
    private String measureUnit;
    private String description;
    private ScoutSensorConfig sensor;
    private double upAlarm;
    private double downAlarm;
}
