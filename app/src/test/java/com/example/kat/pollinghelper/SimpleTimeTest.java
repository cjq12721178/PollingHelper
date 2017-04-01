package com.example.kat.pollinghelper;

import com.example.kat.pollinghelper.bean.config.SimpleTime;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by KAT on 2016/9/18.
 */
public class SimpleTimeTest {

    @Test
    public void from_isCorrect() throws Exception {
        long t = System.currentTimeMillis();
        SimpleTime st1 = SimpleTime.from(t, true);
        SimpleTime st2 = SimpleTime.from(new Date(t));
        System.out.println("st1, h = " + st1.getHour() + ", m = " + st1.getMinute());
        System.out.println("st2, h = " + st2.getHour() + ", m = " + st2.getMinute());
        assertEquals(st1.getHour(), st2.getHour());
        assertEquals(st1.getMinute(), st2.getMinute());
    }

    @Test
    public void currentTimeMillis_equals_getTime() {
        long t = System.currentTimeMillis();
        Date date = new Date(t);
        assertEquals(t, date.getTime());
    }
}
