package com.example.kat.pollinghelper.protocol;

/**
 * Created by KAT on 2016/6/28.
 */
public class NumericConverter {
    public static short toUInt8(byte b) {
        return (short) (b & 0xff);
    }

    public static int toUInt16(byte b) {
        return b & 0xff;
    }

    public static int toUInt16(short s) {
        return s & 0xffff;
    }

    public static long toUInt32(byte b) {
        return b & 0xffl;
    }

    public static long toUInt32(short s) {
        return s & 0xffffl;
    }

    public static long toUInt32(int i) {
        return i & 0xffffffffl;
    }

    public static int toUInt16(byte high, byte low) {
        return (toUInt16(high) << 8) | toUInt16(low);
    }

    public static short toInt16(byte h, byte l) {
        return (short) ((((short)h) << 8) | toUInt8(l));
    }

    public static int toInt32(byte h, byte l) {
        return (((int)h) << 8) | toUInt16(l);
    }

    //高位在前，低位在后
    public static long toUInt32(byte b1, byte b2, byte b3, byte b4) {
        return (toUInt32(b1) << 24) |
                (toUInt32(b2) << 16) |
                (toUInt32(b3) << 8) |
                toUInt32(b4);
    }
}
