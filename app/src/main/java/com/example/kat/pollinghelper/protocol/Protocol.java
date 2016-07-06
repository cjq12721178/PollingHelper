package com.example.kat.pollinghelper.protocol;

import java.util.Collection;

/**
 * Created by KAT on 2016/6/21.
 */
public abstract class Protocol {
    public interface PackageInfo {
    }

    public Protocol(int minPackageLength) {
        this.minPackageLength = minPackageLength;
    }

    public abstract byte[] assemble(PackageInfo packageInfo);

    public abstract PackageInfo analyze(byte[] data);

    //将src整体拷入dest的offset位之后，为简便起见，不做任何判断，用的使用自己看着办
    //算了，还是加上吧。。
    protected void copy(byte[] dest, byte[] src, int offset) {
        if (dest != null && src != null && offset + src.length - 1 <= dest.length) {
            int pos = offset - 1;
            for (byte data :
                    src) {
                dest[++pos] = data;
            }
        }
    }

    public abstract PackageInfo getPackageInfo();

    protected static final int DATA_ZONE_LENGTH_LEN = 1;
    protected static final int CRC16_LEN = 2;
    protected int dataZoneLength;
    protected final int minPackageLength;
}
