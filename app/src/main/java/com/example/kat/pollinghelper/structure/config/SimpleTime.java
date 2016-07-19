package com.example.kat.pollinghelper.structure.config;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KAT on 2016/5/27.
 */
public class SimpleTime implements Serializable, Comparable<SimpleTime> {
    public SimpleTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    //此处的time只是简单的将小时化为分钟数然后与分钟数相加所得的结果
    public SimpleTime(int time) {
        this(time / RATIO_HOUR_MINUTE, time % RATIO_HOUR_MINUTE);
    }

    public int getTime() {
        return hour * RATIO_HOUR_MINUTE + minute;
    }

    public int getHour() {
        return hour;
    }

    public SimpleTime setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public SimpleTime setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public static SimpleTime from(Date date) {
        if (date == null)
            return null;

        return from(date.getTime(), false);
    }

    //如果是用System.currentTimeMillis()，则需要考虑时区，accountTimeZone应为true
    //如果是用Date.getTime()，则不用考虑时区，accountTimeZone应为false
    public static SimpleTime from(long milliseconds, boolean accountTimeZone) {
        long totalMinutes = (milliseconds + (accountTimeZone ? getTimeDifference() : 0)) / RATIO_SECOND_MILLIS / RATIO_MINUTE_SECOND;
        int currentMinute = (int)(totalMinutes % 60);
        int currentHour = (int)(totalMinutes / RATIO_HOUR_MINUTE % 24);
        return new SimpleTime(currentHour, currentMinute);
    }

    public Date toCurrentScheduleDate() {
        return toCurrentScheduleDate(System.currentTimeMillis(), true);
    }

    public Date toCurrentScheduleDate(Date currentDate) {
        return toCurrentScheduleDate(currentDate.getTime(), false);
    }

    //当设置时间早于当前时间时，日期为今天，否则为下上一天
    public Date toCurrentScheduleDate(long currentMilliseconds, boolean accountTimeZone) {
        return toScheduleDate(currentMilliseconds, true, accountTimeZone);
    }

    //当设置时间晚于当前时间时，日期为今天，否则为下一天
    public Date toNextScheduleDate() {
        return toNextScheduleDate(System.currentTimeMillis(), true);
    }

    public Date toNextScheduleDate(Date currentDate) {
        return toNextScheduleDate(currentDate.getTime(), false);
    }

    public Date toNextScheduleDate(long currentMilliseconds, boolean accountTimeZone) {
        return toScheduleDate(currentMilliseconds, false, accountTimeZone);
    }

    //如果是用System.currentTimeMillis()，则需要考虑时区，accountTimeZone应为true
    //如果是用Date.getTime()，则不用考虑时区，accountTimeZone应为false
    private Date toScheduleDate(long currentMilliseconds, boolean currentOrNext, boolean accountTimeZone) {
        long timeDifference = accountTimeZone ? getTimeDifference() : 0;
        long totalMinutes = (currentMilliseconds + timeDifference) / RATIO_SECOND_MILLIS / RATIO_MINUTE_SECOND;
        int currentMinute = (int)(totalMinutes % RATIO_HOUR_MINUTE);
        int currentHour = (int)(totalMinutes / RATIO_HOUR_MINUTE % RATIO_DAY_HOUR);
        int deltaMinutes = (hour - currentHour) * RATIO_HOUR_MINUTE + minute - currentMinute;
        totalMinutes += deltaMinutes + (currentOrNext ?
                (deltaMinutes >= 0 ? -DAY_MINUTES : 0) :
                (deltaMinutes >= 0 ? 0 : DAY_MINUTES));
        return new Date(totalMinutes * RATIO_MINUTE_SECOND * RATIO_SECOND_MILLIS - timeDifference);
    }

    private static long getTimeDifference() {
        return Calendar.getInstance().getTimeZone().getRawOffset();
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }

    @Override
    public int hashCode() {
        return getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null) {
            return false;
        }

        if (getClass() == o.getClass()) {
            SimpleTime other = (SimpleTime)o;
            return other.hour == hour && other.minute == minute;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(SimpleTime another) {
        return hour < another.hour ||
                (hour == another.hour && minute < another.minute) ? -1 : 1;
    }

    private static final int RATIO_SECOND_MILLIS = 1000;
    private static final int RATIO_MINUTE_SECOND = 60;
    private static final int RATIO_HOUR_MINUTE = 60;
    private static final int RATIO_DAY_HOUR = 24;
    public static final long DAY_MINUTES = RATIO_DAY_HOUR * RATIO_HOUR_MINUTE;
    public static final long DAY_MILLISECONDS = RATIO_DAY_HOUR * RATIO_HOUR_MINUTE * RATIO_MINUTE_SECOND * RATIO_SECOND_MILLIS;
    private int hour;
    private int minute;
}
