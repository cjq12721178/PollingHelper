package com.example.kat.pollinghelper.protocol;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by KAT on 2016/6/21.
 */
public class SensorUdpInfo extends SensorInfo {

    private SensorUdpInfo() {
    }

    public static SensorUdpInfo from(byte[] sensorData) {
        return from(sensorData, 0);
    }

    public static SensorUdpInfo from(byte[] data, int offset) {
        SensorUdpInfo sensorInfo = null;
        if (data != null && offset >=0 && offset + SENSOR_DATA_LEN <= data.length &&
                data[offset + SENSOR_DATA_LEN - 1] == Crc.calc8(data, offset, SENSOR_DATA_LEN - 1)) {
            int pos = offset;
            sensorInfo = new SensorUdpInfo();
            sensorInfo.address = NumericConverter.toUInt16(data[pos], data[++pos]);
            sensorInfo.dataType = getDataType(data[++pos]);
            pos += 4;
            sensorInfo.value = generateValue(sensorInfo.dataType, data[pos], data[++pos]);
            sensorInfo.batteryVoltage = generateVoltage(data[++pos], sensorInfo.address);
            pos += 2;
            sensorInfo.timestamp = new GregorianCalendar(YEAR_ADJUST, data[pos] & 0x0f, data[++pos], data[++pos], data[++pos], data[++pos]);
        }
        return sensorInfo;
    }

    public static void setDataTypeMap(Map<Byte, SensorDataType> dataTypeMap) {
        SensorUdpInfo.dataTypeMap = dataTypeMap;
    }

    private static double generateValue(SensorDataType dataType, byte d4, byte d5) {
        switch (dataType.getPattern()) {
            case DT_STATUS:return d5 == 0x10 || d5 == 1 ? 1 : 0;
            case DT_COUNT:return NumericConverter.toUInt16(d5);
            case DT_ANALOG:
            default:return (dataType.isSigned() ?
                    NumericConverter.toInt32(d4, d5) :
                    NumericConverter.toUInt16(d4, d5)) * dataType.getCoefficient();
        }
    }

    private static float generateVoltage(byte byteVoltage, long address) {
        return address < 32768 ? byteVoltage / VOLTAGE_DOWN_CONVERSION_VALUE :
                (byteVoltage != 0 ? VOLTAGE_UP_CONVERSION_VALUE / byteVoltage : 0f);
    }

    @Override
    public String getFullAddress() {
        return MANUFACTURER_TYPE + String.format("-%02X-%04X", dataType.getValue(), address);
    }

    @Override
    public String getMacAddress() {
        return String.format("%04X", address);
    }

    public static SensorDataType getDataType(byte value) {
        SensorDataType tmp = dataTypeMap.get(value);
        if (tmp == null) {
            tmp = SensorDataType.getEmptyType(value);
            synchronized (dataTypeMap) {
                dataTypeMap.put(value, tmp);
            }
        }
        return tmp;
    }

    @Override
    public long getTimestamp() {
        return timestamp.getTimeInMillis();
    }

    @Override
    public String getMeasureName() {
        return dataType.getName();
    }

    //用于判断在Set<SensorValue>中是否存在相同FullAddress的SensorValue
//    @Override
//    public int hashCode() {
//        String fullAddress = getFullAddress();
//        return fullAddress == null ? 0 : fullAddress.hashCode();
//    }

    public static final int ADDRESS_LEN = 4;
    public static final int SENSOR_DATA_LEN = 16;
    private static final int YEAR_ADJUST = GregorianCalendar.getInstance().get(Calendar.YEAR);
    private static final float VOLTAGE_UP_CONVERSION_VALUE = 307.2f;
    private static final float VOLTAGE_DOWN_CONVERSION_VALUE = 20.0f;
    private static Map<Byte, SensorDataType> dataTypeMap;
    private GregorianCalendar timestamp;
}
