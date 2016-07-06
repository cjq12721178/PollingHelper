package com.example.kat.pollinghelper.protocol;

/**
 * Created by KAT on 2016/6/28.
 */
public abstract class SensorInfo {

    public abstract String getFullAddress();

    public long getAddress() {
        return address;
    }

    public byte getDataType() {
        return dataType;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public abstract long getTimestamp();

    public double getValue() {
        return value;
    }

    protected static final String MANUFACTURER_TYPE = "111";
    protected long address;
    protected byte dataType;
    protected float batteryVoltage;
    protected double value;
}
