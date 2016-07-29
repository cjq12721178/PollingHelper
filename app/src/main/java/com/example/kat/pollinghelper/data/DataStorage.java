package com.example.kat.pollinghelper.data;

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

    public double getRealTimeData(String sensorFullAddress) {
        SensorValue target = sensorValues.get(sensorFullAddress);
        return target != null ? target.getLatestValue() : 0;
    }

    public void receiveSensorInfo(List<SensorInfo> sensorInfos) {
        if (sensorInfos != null) {
            for (SensorInfo sensorInfo :
                    sensorInfos) {
                addSensorValue(sensorInfo);
            }
        }
    }

    private void addSensorValue(SensorInfo sensorInfo) {
        if (sensorInfo != null) {
            synchronized (sensorValues) {
                String fullAddress = sensorInfo.getFullAddress();
                SensorValue sensorValue = sensorValues.get(fullAddress);
                if (sensorValue != null) {
                    sensorValue.addValue(sensorInfo);
                } else {
                    sensorValue = SensorValue.from(sensorInfo);
                    sensorValues.put(fullAddress, sensorValue);
                    if (onDataListener != null) {
                        onDataListener.onUpdate(sensorValue);
                    }
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
