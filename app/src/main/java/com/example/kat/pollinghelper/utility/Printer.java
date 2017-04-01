package com.example.kat.pollinghelper.utility;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by KAT on 2016/8/19.
 */
public class Printer {

    public static void checkNull(Object o, String printInfo) {
        if (o == null && printInfo != null) {
            Log.d(TAG, printInfo + " == null");
        }
    }

    public static void print(byte[] data) {
        if (data != null && data.length > 0) {
            StringBuilder builder = new StringBuilder(data.length * 3 - 1);
            builder.append(String.format("%02X", data[0]));
            for (int i = 1, end = data.length;i < end;++i) {
                builder.append(String.format(" %02X", data[i]));
            }
            Log.d(TAG, builder.toString());
        }
    }

    public static void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static final String TAG = "PollingHelper";
}
