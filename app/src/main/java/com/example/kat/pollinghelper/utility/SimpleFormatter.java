package com.example.kat.pollinghelper.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by KAT on 2016/7/15.
 */
public class SimpleFormatter {

    public static String formatYearMonthDayHourMinute(Date date) {
        return date != null ? dateFormatYearMonthDayHourMinute.format(date) : "";
    }

    public static String formatYearMonthDayHourMinute(long time) {
        timeReceiver.setTime(time);
        return formatYearMonthDayHourMinute(timeReceiver);
    }

    public static String formatHourMinuteSecond(Date date) {
        return date != null ? dateFormatHourMinuteSecond.format(date) : "";
    }

    public static String formatHourMinuteSecond(long time) {
        timeReceiver.setTime(time);
        return formatHourMinuteSecond(timeReceiver);
    }

    public static String keepDecimal(double src, int n) {
        if (n < 0)
            return String.valueOf(src);

        String format = formats.get(n);
        if (format == null) {
            format = "%." + n + "f";
            formats.put(n, format);
        }

        return String.format(format, src);
    }

    private static Date timeReceiver = new Date();
    private static Map<Integer, String> formats = new HashMap<>();
    private static SimpleDateFormat dateFormatHourMinuteSecond = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat dateFormatYearMonthDayHourMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
}
