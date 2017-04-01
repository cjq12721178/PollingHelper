package com.example.kat.pollinghelper.data;

import android.text.TextUtils;

import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.protocol.SensorInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/5/4.
 */
public class DataStorage {

    public interface OnDataListener {
        void onInit(Collection<SensorValue> sensorCollection);
        void onUpdate(SensorValue newSensor);
    }

    public DataStorage() {
        sensorValues = new HashMap<>();
    }

    public boolean setSensorValueListener(String sensorFullAddress, SensorValue.OnValueChangedListener l) {
        if (l == null)
            return false;
        SensorValue target = sensorValues.get(sensorFullAddress);
        if (target == null)
            return false;
        target.setOnValueChangedListener(l);
        return true;
    }

    public void clearAllSensorValueListener() {
        synchronized (sensorValues) {
            for (SensorValue sensor :
                    sensorValues.values()) {
                sensor.setOnValueChangedListener(null);
            }
        }
    }

    public double getRealTimeData(String sensorFullAddress) {
        SensorValue target = sensorValues.get(sensorFullAddress);
        return target != null ? target.getLatestValue() : 0;
    }

    public void receiveSensorInfo(List<SensorInfo> sensorInfos,
                                  List<ScoutSensorConfig> sensorConfigs) {
        if (sensorInfos != null) {
            for (SensorInfo sensorInfo :
                    sensorInfos) {
                addSensorValue(sensorInfo, sensorConfigs);
            }
        }
    }

    private void addSensorValue(SensorInfo sensorInfo,
                                List<ScoutSensorConfig> sensorConfigs) {
        if (sensorInfo != null) {
            synchronized (sensorValues) {
                String fullAddress = sensorInfo.getFullAddress();
                SensorValue sensorValue = sensorValues.get(fullAddress);
                if (sensorValue != null) {
                    sensorValue.addValue(sensorInfo);
                } else {
                    //根据传感器配置修改数据浏览界面传感器测量名称，效率不高，有待改进
                    sensorValue = SensorValue.from(sensorInfo, findSensorConfigForAddress(sensorConfigs, fullAddress));
                    sensorValues.put(fullAddress, sensorValue);
                    if (onDataListener != null) {
                        onDataListener.onUpdate(sensorValue);
                    }
                }
            }
        }
    }

    private ScoutSensorConfig findSensorConfigForAddress(List<ScoutSensorConfig> sensorConfigs, String sensorAddress) {
        if (sensorConfigs == null || TextUtils.isEmpty(sensorAddress))
            return null;
        for (ScoutSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorConfig.getAddress().equals(sensorAddress))
                return sensorConfig;
        }
        return null;
    }

    public void updateSensorMeasureName(List<ScoutSensorConfig> sensorConfigs) {
        if (sensorConfigs != null) {
            synchronized (sensorValues) {
                for (SensorValue sensorValue :
                        sensorValues.values()) {
                    sensorValue.setMeasureName(null,
                            findSensorConfigForAddress(sensorConfigs, sensorValue.getFullAddress()));
                }
            }
        }
    }

    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
        if (onDataListener != null) {
            synchronized (sensorValues) {
                onDataListener.onInit(sensorValues.values());
            }
        }
    }

    private OnDataListener onDataListener;
    private Map<String, SensorValue> sensorValues;
}
