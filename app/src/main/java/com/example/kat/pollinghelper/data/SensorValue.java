package com.example.kat.pollinghelper.data;

import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorInfo;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by KAT on 2016/6/22.
 */
public class SensorValue {

    //用于外部模块获取传感器数据
    public interface ValueReceiver<T> {
        //初始化数据接受者，并将其返回给SensorValue，size为数据总数
        T start(int size);
        //前二者为传感器关键数据拷贝，后者为startReceive返回的数据接受者
        void receive(long timeStamp, double value, T receiver);
    }

    public interface OnValueChangedListener {
        void onValueChanged(long timeStamp, double value);
    }

    private SensorValue() {
        values = new TreeMap<>();
        createTime = System.currentTimeMillis();
    }

    public static SensorValue from(SensorInfo sensorInfo, ScoutSensorConfig sensorConfig) {
        SensorValue result = new SensorValue();
        result.dataType = sensorInfo.getDataType();
        result.fullAddress = sensorInfo.getFullAddress();
        result.macAddress = sensorInfo.getMacAddress();
        result.setMeasureName(sensorInfo, sensorConfig);
        return result.addValue(sensorInfo);
    }

    public SensorValue addValue(SensorInfo sensorInfo) {
        return addValue(sensorInfo.getTimestamp(), sensorInfo.getValue());
    }

    private SensorValue addValue(long timestamp, double value) {
        synchronized (values) {
            values.put(timestamp, value);
            if (values.size() > MAX_ELEMENT_COUNT) {
                values.remove(values.firstKey());
            }
        }
        if (onValueChangedListener != null) {
            onValueChangedListener.onValueChanged(timestamp, value);
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

    public String getMacAddress() {
        return macAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public <T> T getValues(ValueReceiver<T> valueReceiver) {
        if (valueReceiver == null)
            return null;

        T receiver;
        synchronized (values) {
            receiver = valueReceiver.start(values.size());
            for (Map.Entry<Long, Double> element :
                    values.entrySet()) {
                valueReceiver.receive(element.getKey(), element.getValue(), receiver);
            }
        }
        return receiver;
    }

    public String getMeasureName() {
        return measureName;
    }

    protected void setMeasureName(SensorInfo sensorInfo, ScoutSensorConfig sensorConfig) {
        measureName = sensorConfig == null ?
                (sensorInfo == null ?
                        measureName :
                        sensorInfo.getMeasureName()) :
                sensorConfig.getName();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setOnValueChangedListener(OnValueChangedListener l) {
        onValueChangedListener = l;
    }

    private final long createTime;
    private SensorDataType dataType;
    private String macAddress;
    private String fullAddress;
    private String measureName;
    //用于控制数据规模
    private static final int MAX_ELEMENT_COUNT = 10;
    private TreeMap<Long, Double> values;
    private OnValueChangedListener onValueChangedListener;
}
