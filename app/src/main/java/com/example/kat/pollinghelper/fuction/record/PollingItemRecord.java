package com.example.kat.pollinghelper.fuction.record;

import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;

/**
 * Created by KAT on 2016/5/9.
 */
public class PollingItemRecord {
    public PollingItemRecord(long id, PollingItemConfig itemConfig) {
        this.id = id;
        this.itemConfig = itemConfig;
    }

    public long getId() {
        return id;
    }

    public PollingItemConfig getItemConfig() {
        return itemConfig;
    }

    public double getValue() {
        return value;
    }

    public boolean isOutOfAlarm() {
        return isOutOfDownAlarm() || isOutOfUpAlarm();
    }

    public boolean isOutOfUpAlarm() {
        return value > itemConfig.getUpAlarm();
    }

    public boolean isOutOfDownAlarm() {
        return value < itemConfig.getDownAlarm();
    }

    public void setValue(double value) {
        this.value = value;
    }

    private final long id;
    private final PollingItemConfig itemConfig;
    private double value;
}
