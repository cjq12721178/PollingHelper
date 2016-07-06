package com.example.kat.pollinghelper.protocol;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by KAT on 2016/6/21.
 */
public class SensorUdpInfo extends SensorInfo {
    public enum DataPattern {
        DT_ANALOG,
        DT_BOOL
    }

    static {
        dataPatterns = new HashMap<>();
        coefficients = new HashMap<>();
        establishDataPatternMap();
        establishCoefficientsMap();
    }

    private static void establishCoefficientsMap() {
        coefficients.put((byte) 0x05,0.0078125);
        coefficients.put((byte) 0x40,0.5);
        coefficients.put((byte) 0x4F,1.0);
        coefficients.put((byte) 0x4E,1.0);
        coefficients.put((byte) 0x4F,1.0);
        coefficients.put((byte) 0x10,1.0);
        coefficients.put((byte) 0x12,1.0);
        coefficients.put((byte) 0x13,1.0);
        coefficients.put((byte) 0x14,1.0);
        coefficients.put((byte) 0x15,0.1);
        coefficients.put((byte) 0x11,1.0);
        coefficients.put((byte) 0x10,1.0);
        coefficients.put((byte) 0x16,1.0);
        coefficients.put((byte) 0x20,0.1);
        coefficients.put((byte) 0x59,0.01);
        coefficients.put((byte) 0x54,0.1);
        coefficients.put((byte) 0x57,0.01);
        coefficients.put((byte) 0x55,0.1);
        coefficients.put((byte) 0x58,1.0E-4);
        coefficients.put((byte) 0x21,1.0);
        coefficients.put((byte) 0x22,1.0);
        coefficients.put((byte) 0x23,1.0);
        coefficients.put((byte) 0x24,1.0);
        coefficients.put((byte) 0x25,1.0);
        coefficients.put((byte) 0x20,0.1);
        coefficients.put((byte) 0x10,0.01);
        coefficients.put((byte) 0xA0,1.0);
        coefficients.put((byte) 0xA1,1.0);
        coefficients.put((byte) 0xA3,0.0001);
        coefficients.put((byte) 0xA4,0.01);
        coefficients.put((byte) 0xA5,0.01);
        coefficients.put((byte) 0xB0,1.0);
        coefficients.put((byte) 0xB1,1.0);
        coefficients.put((byte) 0xB3,0.0001);
        coefficients.put((byte) 0xB4,0.01);
        coefficients.put((byte) 0xB5,0.01);
        coefficients.put((byte) 0xC0,1.0);
        coefficients.put((byte) 0xC1,1.0);
        coefficients.put((byte) 0xC3,0.0001);
        coefficients.put((byte) 0xC4,0.01);
        coefficients.put((byte) 0xC5,0.01);
        coefficients.put((byte) 0xA6,0.1);
        coefficients.put((byte) 0xA7,0.01);
        coefficients.put((byte) 0xA8,0.01);
        coefficients.put((byte) 0xA9,0.01);
        coefficients.put((byte) 0x04,0.0078125);
        coefficients.put((byte) 0x61,0.1);
        coefficients.put((byte) 0x62,0.5);
        coefficients.put((byte) 0x44,0.05);
        coefficients.put((byte) 0x60,0.025);
        coefficients.put((byte) 0x42,0.001);
        coefficients.put((byte) 0x65,0.001);
        coefficients.put((byte) 0x66,0.001);
        coefficients.put((byte) 0x67,0.001);
        coefficients.put((byte) 0xBE,1.0);
        coefficients.put((byte) 0xBF,1.0);
        coefficients.put((byte) 0xB1,1.0);
        coefficients.put((byte) 0x1E,0.05);
        coefficients.put((byte) 0x75,0.1);
    }

    private static void establishDataPatternMap() {
        dataPatterns.put((byte) 0x05, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x0A, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x10, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x12, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x13, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x14, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x15, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x16, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x20, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x21, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x22, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x23, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x24, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x25, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x26, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x27, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x28, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x29, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2A, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2B, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2C, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2D, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2E, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x2F, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x40, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x42, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x55, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x58, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x59, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x65, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x66, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x67, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x75, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x77, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x79, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x45, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x47, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x50, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x33, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x34, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x43, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x30, DataPattern.DT_BOOL);
        dataPatterns.put((byte) 0x01, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x02, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x03, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x04, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x06, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x07, DataPattern.DT_ANALOG);
        dataPatterns.put((byte) 0x08, DataPattern.DT_ANALOG);
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
            sensorInfo.dataType = data[++pos];
            pos += 4;
            sensorInfo.value = generateValue(sensorInfo.dataType, NumericConverter.toUInt16(data[pos], data[++pos]));
            sensorInfo.batteryVoltage = generateVoltage(data[++pos], sensorInfo.address);
            pos += 2;
            sensorInfo.timestamp = new GregorianCalendar(YEAR_ADJUST, data[pos] & 0x0f, data[++pos], data[++pos], data[++pos], data[++pos]);
        }
        return sensorInfo;
    }

    private static double generateValue(byte dataType, int rawValue) {
        if (dataPatterns.get(dataType) == DataPattern.DT_BOOL) {
            if (rawValue == 0x10 || rawValue == 1) {
                return 1.0;
            }
            return 0.0;
        }
        Double coefficient = coefficients.get(dataType);
        return coefficient != null ? coefficient * rawValue : rawValue;
    }

    private static float generateVoltage(byte byteVoltage, long address) {
        return address < 32768 ? byteVoltage / VOLTAGE_DOWN_CONVERSION_VALUE :
                (byteVoltage != 0 ? VOLTAGE_UP_CONVERSION_VALUE / byteVoltage : 0f);
    }

    @Override
    public String getFullAddress() {
        return MANUFACTURER_TYPE + String.format("-%02X-%04X", dataType, address);
    }

    @Override
    public long getTimestamp() {
        return timestamp.getTimeInMillis();
    }

    //用于判断在Set<SensorValue>中是否存在相同FullAddress的SensorValue
//    @Override
//    public int hashCode() {
//        String fullAddress = getFullAddress();
//        return fullAddress == null ? 0 : fullAddress.hashCode();
//    }

    public static final int SENSOR_DATA_LEN = 16;
    private static final Map<Byte, Double> coefficients;
    private static final Map<Byte, DataPattern> dataPatterns;
    private static final int YEAR_ADJUST = GregorianCalendar.getInstance().get(Calendar.YEAR);
    private static final float VOLTAGE_UP_CONVERSION_VALUE = 307.2f;
    private static final float VOLTAGE_DOWN_CONVERSION_VALUE = 20.0f;
    private GregorianCalendar timestamp;
}
