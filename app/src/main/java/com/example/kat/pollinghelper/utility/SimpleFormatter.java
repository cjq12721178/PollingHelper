package com.example.kat.pollinghelper.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KAT on 2016/7/15.
 */
public class SimpleFormatter {

    public static String format(Date date) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return date != null ? dateFormat.format(date) : "";
    }

    public static String to3Decimal(double value) {
        return String.format("%.3f", value);
    }

    private static SimpleDateFormat dateFormat;
    private static SimpleFormatter simpleFormatter;
}
