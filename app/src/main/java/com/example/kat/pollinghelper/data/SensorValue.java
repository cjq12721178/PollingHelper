package com.example.kat.pollinghelper.data;

import android.util.Log;

import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorInfo;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by KAT on 2016/6/22.
 */
public class SensorValue {
    public SensorValue() {
        values = new TreeMap<>();
    }

    public static SensorValue from(SensorInfo sensorInfo) {
        SensorValue result = new SensorValue();
        result.dataType = sensorInfo.getDataType();
        result.address = sensorInfo.getMacAddress();
        return result.addValue(sensorInfo);
    }

    public SensorValue addValue(SensorInfo sensorInfo) {
        return addValue(sensorInfo.getTimestamp(), sensorInfo.getValue());
    }

    private SensorValue addValue(long timestamp, double value) {
        values.put(timestamp, value);
        if (values.size() > MAX_ELEMENT_COUNT) {
            values.remove(values.firstKey());
        }
        return this;
    }

    public double getLatestValue() {
        return values.get(values.lastKey());
    }

    public double getValue(long timestamp) {
        Double value = values.get(timestamp);
        return value != null ? value : 0;
    }

    public String getSignificantValue(long timestamp) {
        return dataType.getSignificantValue(getValue(timestamp));
    }

    public String getSignificantValueWithUnit(long timestamp) {
        return dataType.getSignificantValueWithUnit(getValue(timestamp));
    }

    public String getLatestSignificantValue() {
        return dataType.getSignificantValue(getLatestValue());
    }

    public String getLatestSignificantValueWithUnit() {
        return dataType.getSignificantValueWithUnit(getLatestValue());
    }

    public long getLatestTimestamp() {
        return values.lastKey();
    }

    public SensorDataType getDataType() {
        return dataType;
    }

    public String getAddress() {
        return address;
    }

    private SensorDataType dataType;
    private String address;
    //用于控制数据规模
    private static final int MAX_ELEMENT_COUNT = 10;
    private SortedMap<Long, Double> values;
}
