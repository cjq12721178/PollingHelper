package com.example.kat.pollinghelper.protocol;

import java.util.Map;

/**
 * Created by KAT on 2016/6/28.
 */
public class SensorBleInfo extends SensorInfo {

    private SensorBleInfo() {
    }

    public static SensorBleInfo from(byte[] data, int offset, long address, byte[] addressLow, float voltage) {
        SensorBleInfo sensorBleInfo = null;
        if (data != null && offset >=0 && offset + SENSOR_DATA_LEN <= data.length) {
            int pos = offset;
            sensorBleInfo = new SensorBleInfo();
            sensorBleInfo.dataType = getDataType(data[pos]);
            sensorBleInfo.address = address >> 8;
            sensorBleInfo.arrayAddress = (byte)((address & 0xff) + addressLow[sensorBleInfo.dataType.getValue()]++);
            sensorBleInfo.batteryVoltage = voltage;
            sensorBleInfo.value = Float.intBitsToFloat((int)NumericConverter.toUInt32(data[++pos], data[++pos], data[++pos], data[++pos]));
            sensorBleInfo.time = System.currentTimeMillis();
        }
        return sensorBleInfo;
    }

    public static void setDataTypeMap(Map<Byte, SensorDataType> dataTypeMap,
                                      Map<SensorDataType, Map<Byte, String>> measureNameMap) {
        SensorBleInfo.dataTypeMap = dataTypeMap;
        SensorBleInfo.measureNameMap = measureNameMap;
    }

    @Override
    public String getFullAddress() {
        return MANUFACTURER_TYPE + String.format("-%02X-%06X%02X", dataType.getValue(), address, arrayAddress);
    }

    public static SensorDataType getDataType(byte value) {
        SensorDataType tmp = dataTypeMap.get(value);
        if (tmp == null) {
            tmp = SensorDataType.getNullType(value);
            synchronized (dataTypeMap) {
                dataTypeMap.put(value, tmp);
            }
        }
        return tmp;
    }

    @Override
    public long getTimestamp() {
        return time;
    }

    @Override
    public String getMeasureName() {
        if (arrayAddress == 0)
            return dataType.getName();

        Map<Byte, String> introductionMap = measureNameMap.get(dataType);
        String extraIntroduction = introductionMap != null ? introductionMap.get(arrayAddress) : null;
        return extraIntroduction != null ? dataType.getName() + extraIntroduction : dataType.getName();
    }

    public static final int ADDRESS_LEN = 6;
    public static final int SENSOR_DATA_LEN = 5;
    private static Map<Byte, SensorDataType> dataTypeMap;
    private static Map<SensorDataType, Map<Byte, String>> measureNameMap;
    private long time;
    private byte arrayAddress;
}
