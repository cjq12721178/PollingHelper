package com.example.kat.pollinghelper.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KAT on 2016/6/21.
 */
public class BaseStationUdpProtocol extends Protocol {

    public class BaseStationInfo implements PackageInfo {
        public List<SensorInfo> SensorInfos;
        public byte CommandCode;
    }

    public BaseStationUdpProtocol() {
        super(START_CHARACTER.length +
                baseStationAddress.length +
                END_CHARACTER.length +
                DATA_ZONE_LENGTH_LEN +
                COMMAND_CODE_LEN +
                CRC16_LEN);
        dataZoneLength = 0x01;
        commandCode = COMMAND_CODE_NONE;
    }

    @Override
    public BaseStationInfo getPackageInfo() {
        return new BaseStationInfo();
    }

    protected boolean preAnalyze(byte[] data) {
        boolean result = false;
        //判断数据是否为空，以及数据长度是否大于最小数据长度
        if (data != null && data.length >= minPackageLength) {
            //记录数据域长度
            dataZoneLength = NumericConverter.toUInt16(data[START_CHARACTER.length + baseStationAddress.length]) - COMMAND_CODE_LEN;
            //计算实际数据长度
            int realDataLength = calculatePredicalDataLength(dataZoneLength);
            //检查起始符和结束符
            if (isStartCharacterCorrect(data) && isEndCharacterCorrect(data, realDataLength)) {
                //计算CRC16并校验
                int crc16 = Crc.calc16(data, START_CHARACTER.length, baseStationAddress.length + DATA_ZONE_LENGTH_LEN + COMMAND_CODE_LEN + dataZoneLength);
                if (isCrc16Correct(data, realDataLength, crc16)) {
                    commandCode = (byte)(data[START_CHARACTER.length + baseStationAddress.length + DATA_ZONE_LENGTH_LEN] -
                            FIXED_DIFFERENCE_FROM_COMMAND_TO_RESPONSE);
                    if (isCommandCodeCorrect()) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean isCommandCodeCorrect() {
        //目前只需要该类型命令
        return commandCode == COMMAND_CODE_REQUEST_DATA;
    }

    private boolean isCrc16Correct(byte[] data, int len, int crc16) {
        return (crc16 >> 8) == NumericConverter.toUInt32(data[len - END_CHARACTER.length - 1]) &&
                (crc16 & 0xff) == NumericConverter.toUInt32(data[len - END_CHARACTER.length - 2]);
    }

    private int calculatePredicalDataLength(int actualDataZoneLength) {
        return START_CHARACTER.length + baseStationAddress.length + DATA_ZONE_LENGTH_LEN + COMMAND_CODE_LEN + actualDataZoneLength + CRC16_LEN + END_CHARACTER.length;
    }

    private boolean isStartCharacterCorrect(byte[] data) {
        return data[0] == START_CHARACTER[0] && data[1] == START_CHARACTER[1];
    }

    private boolean isEndCharacterCorrect(byte[] data, int len) {
        return data[len - 2] == END_CHARACTER[0] && data[len - 1] == END_CHARACTER[1];
    }

    @Override
    public byte[] assemble(PackageInfo packageInfo) {
        byte[] result = null;
        if (packageInfo != null && packageInfo instanceof BaseStationInfo) {
            BaseStationInfo baseStationInfo = (BaseStationInfo)packageInfo;
            commandCode = baseStationInfo.CommandCode;
            try {
                //目前只需要这一个命令
                if (commandCode == COMMAND_CODE_REQUEST_DATA) {
                    result = makeGeneralFrame(null);
                }
            } catch (Exception e) {
                result = null;
            }
        }
        return result;
    }

    //dataZone只包含数据域数据，不包括命令码
    private byte[] makeGeneralFrame(byte[] dataZone) {
        byte[] checking = makeCheckingBlock(dataZone);
        int crc16 = Crc.calc16(checking);
        byte[] result = new byte[START_CHARACTER.length +
                checking.length +
                CRC16_LEN +
                END_CHARACTER.length];
        int pos = -1;
        result[++pos] = START_CHARACTER[0];
        result[++pos] = START_CHARACTER[1];
        System.arraycopy(checking, 0, result, ++pos, checking.length);
        pos += checking.length;
        result[pos] = (byte)(crc16 & 0xff);
        result[++pos] = (byte)(crc16 >> 8);
        result[++pos] = END_CHARACTER[0];
        result[++pos] = END_CHARACTER[1];
        return result;
    }

    private byte[] makeCheckingBlock(byte[] dataZone) {
        dataZoneLength = (byte)(dataZone != null ? dataZone.length : 0);
        byte[] checking = new byte[baseStationAddress.length +
                DATA_ZONE_LENGTH_LEN +
                COMMAND_CODE_LEN +
                dataZoneLength];
        int pos = -1;
        checking[++pos] = DEFAULT_BASE_STATION_ADDRESS_UP_BIT;
        checking[++pos] = DEFAULT_BASE_STATION_ADDRESS_DOWN_BIT;
        checking[++pos] = (byte) (dataZoneLength + COMMAND_CODE_LEN);
        checking[++pos] = commandCode;
        if (dataZoneLength > 0) {
            System.arraycopy(dataZone, 0, checking, ++pos, dataZone.length);
        }
        return checking;
    }

    @Override
    public BaseStationInfo analyze(byte[] data) {
        return preAnalyze(data) ? onAnalyze(getDataZone(data)) : null;
    }

    private BaseStationInfo onAnalyze(byte[] dataZone) {
        BaseStationInfo result = null;
        try {
            //目前只需要这一个命令
            if (commandCode == COMMAND_CODE_REQUEST_DATA) {
                if (dataZone.length >= SensorUdpInfo.SENSOR_DATA_LEN) {
                    result = new BaseStationInfo();
                    result.CommandCode = commandCode;
                    result.SensorInfos = new ArrayList<>();
                    for (int beg = 0, end = dataZone.length / SensorUdpInfo.SENSOR_DATA_LEN * SensorUdpInfo.SENSOR_DATA_LEN;
                         beg < end;beg += SensorUdpInfo.SENSOR_DATA_LEN) {
                        result.SensorInfos.add(SensorUdpInfo.from(dataZone, beg));
                    }
                }
            }
        } catch (Exception e) {
            result = null;
        } finally {
            if (result == null) {
                commandCode = COMMAND_CODE_NONE;
            }
        }
        return result;
    }

    private byte[] getDataZone(byte[] data) {
        int start = START_CHARACTER.length + baseStationAddress.length +
                DATA_ZONE_LENGTH_LEN + COMMAND_CODE_LEN;
        return Arrays.copyOfRange(data, start, start + dataZoneLength);
    }

    public static final byte COMMAND_CODE_REQUEST_DATA = 0x35;
    private static final byte COMMAND_CODE_NONE = 0x00;
    private static final byte DEFAULT_BASE_STATION_ADDRESS_UP_BIT = 0x00;
    private static final byte DEFAULT_BASE_STATION_ADDRESS_DOWN_BIT = 0x00;
    private static final byte[] START_CHARACTER = new byte[] { (byte)0xAA, (byte)0xAA };
    private static final byte[] END_CHARACTER = new byte[] { 0x55, 0x55 };
    //baseStationAddress[0] - 高字节
    //baseStationAddress[1] - 低字节
    private static byte[] baseStationAddress = new byte[] { 0x00, 0x00 };
    private static final int COMMAND_CODE_LEN = 1;
    private static final byte FIXED_DIFFERENCE_FROM_COMMAND_TO_RESPONSE = (byte)0x80;
    private byte commandCode;
}
