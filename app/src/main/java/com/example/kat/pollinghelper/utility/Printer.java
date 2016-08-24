package com.example.kat.pollinghelper.utility;

import android.util.Log;

/**
 * Created by KAT on 2016/8/19.
 */
public class Printer {
    public static void checkNull(Object o, String printInfo) {
        if (o == null && printInfo != null) {
            Log.d("PollingHelper", printInfo + " == null");
        }
    }
}
