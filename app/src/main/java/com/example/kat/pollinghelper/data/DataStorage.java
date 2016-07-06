package com.example.kat.pollinghelper.data;

import com.example.kat.pollinghelper.protocol.BaseStationUdpProtocol;
import com.example.kat.pollinghelper.protocol.SensorInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/5/4.
 */
public class DataStorage {

    public DataStorage() {
        sensorValues = new HashMap<>();
    }

    public double getRealTimeData(String sensorFullAddress) {
        SensorValue target = sensorValues.get(sensorFullAddress);
        return target != null ? target.getLatestValue() : 0;
    }

    public void receiveUdpData(BaseStationUdpProtocol.BaseStationInfo baseStationInfo) {
        if (baseStationInfo != null && baseStationInfo.CommandCode == BaseStationUdpProtocol.COMMAND_CODE_REQUEST_DATA) {
            for (SensorInfo sensorInfo :
                    baseStationInfo.SensorInfos) {
                addSensorValue(sensorInfo);
            }
        }
    }

    public void receiveSensorInfo(List<SensorInfo> sensorInfos) {
        if (sensorInfos != null) {
            for (SensorInfo sensorInfo :
                    sensorInfos) {
                addSensorValue(sensorInfo);
            }
        }
    }
    
    public void receiveBleData(byte[] data) {
        
    }
    
    private void saveData(byte[] originalCommunicationData) {
        
    }

    private void addSensorValue(SensorInfo sensorInfo) {
        if (sensorInfo != null) {
            synchronized (sensorValues) {
                String fullAddress = sensorInfo.getFullAddress();
                if (sensorValues.containsKey(fullAddress)) {
                    sensorValues.get(fullAddress).addValue(sensorInfo.getTimestamp(), sensorInfo.getValue());
                } else {
                    sensorValues.put(fullAddress, new SensorValue().addValue(sensorInfo.getTimestamp(), sensorInfo.getValue()));
                }
            }
        }
    }

    //先不删了，以后做研究用吧
    private int secondaryHash(Object key) {
        try {
            return (int)Collections.class.getMethod("secondaryHash", Object.class).invoke(null, new Object[] {key});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Map<String, SensorValue> sensorValues;
}
