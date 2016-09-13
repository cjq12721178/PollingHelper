package com.example.kat.pollinghelper.bean.warn;

import com.example.kat.pollinghelper.bean.config.ScoutItemConfig;

/**
 * Created by KAT on 2016/8/29.
 */
public class ItemWarnInfo {

    public ItemWarnInfo(ScoutItemConfig itemConfig, long warnTime, double currentValue) {
        if (!reset(itemConfig, warnTime, currentValue))
            throw new NullPointerException("ScoutItemConfig = null");
    }

    public long getWarnTime() {
        return warnTime;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public String getCurrentValueWithUnit() {
        String unit = itemConfig.getMeasureUnit();
        return unit != null ? String.valueOf(currentValue) + unit : String.valueOf(currentValue);
    }

    public String getMeasureName() {
        return itemConfig.getMeasureName();
    }

    public double getUpAlarmValue() {
        return itemConfig.getUpAlarm();
    }

    public double getDownAlarmValue() {
        return itemConfig.getDownAlarm();
    }

    public String getAlarm() {
        return currentValue < itemConfig.getDownAlarm() ? getDownAlarm() : getUpAlarm();
    }

    public String getUpAlarm() {
        String unit = itemConfig.getMeasureUnit();
        return unit != null ?
                String.valueOf(itemConfig.getUpAlarm()) + unit + "(上)" :
                String.valueOf(itemConfig.getUpAlarm()) + "(上)";
    }

    public String getDownAlarm() {
        String unit = itemConfig.getMeasureUnit();
        return unit != null ?
                String.valueOf(itemConfig.getDownAlarm()) + unit + "(下)":
                String.valueOf(itemConfig.getDownAlarm()) + "(下)";
    }

    public String getSensorName() {
        return itemConfig.getSensor().getName();
    }

    public boolean reset(ScoutItemConfig itemConfig, long warnTime, double currentValue) {
        if (itemConfig == null)
            return false;
        this.itemConfig = itemConfig;
        this.warnTime = warnTime;
        this.currentValue = currentValue;
        return true;
    }

    private ScoutItemConfig itemConfig;
    private long warnTime;
    private double currentValue;
}
