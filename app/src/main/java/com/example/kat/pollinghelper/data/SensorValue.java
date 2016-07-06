package com.example.kat.pollinghelper.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KAT on 2016/6/22.
 */
public class SensorValue {
    public SensorValue() {
        values = new HashMap<>();
        latestTimestamp = 0;
    }

    public SensorValue addValue(long timestamp, double value) {
        if (latestTimestamp < timestamp) {
            latestTimestamp = timestamp;
        }
        values.put(timestamp, value);
        if (values.size() > MAX_ELEMENT_COUNT) {
            values.remove(values.keySet().iterator().next());
        }
        return this;
    }

    public double getLatestValue() {
        return values.isEmpty() ? 0.0 : values.get(latestTimestamp);
    }

    public double getValue(long timestamp) {
        return values.get(timestamp);
    }

    //用于控制数据规模
    private static final int MAX_ELEMENT_COUNT = 10;
    private long latestTimestamp;
    private Map<Long, Double> values;
}
