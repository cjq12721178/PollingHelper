package com.example.kat.pollinghelper.protocol;

/**
 * Created by KAT on 2016/6/28.
 */
public class SensorBleInfo extends SensorInfo {

    public static SensorBleInfo from(byte[] data, int offset, long address, byte[] addressLow, float voltage) {
        SensorBleInfo sensorBleInfo = null;
        if (data != null && offset >=0 && offset + SENSOR_DATA_LEN <= data.length) {
            int pos = offset;
            sensorBleInfo = new SensorBleInfo();
            sensorBleInfo.dataType = data[pos];
            sensorBleInfo.address = address + addressLow[sensorBleInfo.dataType]++;
            sensorBleInfo.batteryVoltage = voltage;
            sensorBleInfo.value = Float.intBitsToFloat((int)NumericConverter.toUInt32(data[++pos], data[++pos], data[++pos], data[++pos]));
            sensorBleInfo.time = System.currentTimeMillis();
        }
        return sensorBleInfo;
    }

    @Override
    public String getFullAddress() {
        return MANUFACTURER_TYPE + String.format("-%02X-%08X", dataType, address);
    }

    @Override
    public long getTimestamp() {
        return time;
    }

    public static final int SENSOR_DATA_LEN = 5;
    private long time;
}
