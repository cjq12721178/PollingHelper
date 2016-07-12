package com.example.kat.pollinghelper.utility;

import java.util.UUID;

/**
 * Created by KAT on 2016/7/12.
 */
public class IdentifierGenerator {
    public static long get64() {
        return UUID.randomUUID().getMostSignificantBits();
    }
}
