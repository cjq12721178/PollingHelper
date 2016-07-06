package com.example.kat.pollinghelper.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KAT on 2016/6/28.
 */
public class SensorBleProtocol extends Protocol {

    public class SensorParameter implements PackageInfo {
        public List<SensorInfo> SensorInfos;
    }

    public SensorBleProtocol() {
        super(DATA_ZONE_LENGTH_LEN +
        BATTERY_INFO_LEN +
        RSSI_LEN +
        CRC16_LEN);
        addressLow = new byte[0xff];
    }

    @Override
    public byte[] assemble(PackageInfo packageInfo) {
        return null;
    }

    @Override
    public SensorParameter analyze(byte[] data) {
        return null;
    }

    private SensorParameter onAnalyze(byte[] dataZone) {
        SensorParameter result = null;
        try {
            if (dataZone.length >= BATTERY_INFO_LEN + RSSI_LEN + SensorBleInfo.SENSOR_DATA_LEN) {
                result = new SensorParameter();
                result.SensorInfos = new ArrayList<>();
                Arrays.fill(addressLow, (byte) 0);
                float battery = NumericConverter.toUInt32(dataZone[dataZone.length - RSSI_LEN - BATTERY_INFO_LEN]) * BATTERY_VOLTAGE_COEFFICIENT;
                for (int beg = 0, end = dataZone.length / SensorBleInfo.SENSOR_DATA_LEN * SensorBleInfo.SENSOR_DATA_LEN;
                        beg < end;beg += SensorBleInfo.SENSOR_DATA_LEN) {
                    result.SensorInfos.add(SensorBleInfo.from(dataZone, beg, address, addressLow, battery));
                }
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public SensorParameter analyze(String broadcastAddress, byte[] broadcastData) {
        byte[] dataZone = null;
        if (broadcastData != null) {
            byte[] addressArray = getBroadcastAddressFromString(broadcastAddress);
            if (addressArray.length == BROADCAST_ADDRESS_LEN && broadcastData.length >= minPackageLength) {
                dataZoneLength = NumericConverter.toUInt16(broadcastData[0]) - CRC16_LEN;
                if (Crc.calc16(Crc.calc16R(0xffff, addressArray, 0, addressArray.length), broadcastData, 0, DATA_ZONE_LENGTH_LEN + dataZoneLength) ==
                        NumericConverter.toUInt16(broadcastData[DATA_ZONE_LENGTH_LEN + dataZoneLength],
                                broadcastData[DATA_ZONE_LENGTH_LEN + dataZoneLength + 1])) {
                    dataZone = Arrays.copyOfRange(broadcastData, DATA_ZONE_LENGTH_LEN, DATA_ZONE_LENGTH_LEN + dataZoneLength);
                    address = NumericConverter.toUInt32(addressArray[1], addressArray[2], addressArray[3], addressArray[4]);
                }
            }
        }
        return onAnalyze(dataZone);
    }

    private byte[] getBroadcastAddressFromString(String src) {
        byte[] dst = null;
        try {
            String[] tmp = src.split(":");
            dst = new byte[tmp.length];
            for (int i = 0;i < tmp.length;++i) {
                dst[i] = Short.valueOf(tmp[i], 16).byteValue();
            }
        } catch (Exception e) {
            dst = null;
        }
        return dst;
    }

    @Override
    public SensorParameter getPackageInfo() {
        return new SensorParameter();
    }

    private static final float BATTERY_VOLTAGE_COEFFICIENT = 0.05f;
    private static final int BROADCAST_ADDRESS_LEN = 6;
    private static final int BATTERY_INFO_LEN = 1;
    private static final int RSSI_LEN = 1;
    //其实不想搞成static的，但一想到每次new SensorBleProtocol的时候都要搞这么个数组就很蛋疼
    private final byte[] addressLow;
    private long address;
}
