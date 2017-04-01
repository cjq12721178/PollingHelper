package com.example.kat.pollinghelper.protocol;

import com.example.kat.pollinghelper.utility.SimpleFormatter;

/**
 * Created by KAT on 2016/11/23.
 */
public class FloatInterpreter implements ValueInterpreter {

    private static final int MAX_DECIMALS = 9;
    private static FloatInterpreter[] interpreters;
    static {
        interpreters = new FloatInterpreter[MAX_DECIMALS + 1];
        for (int i = 0;i <= MAX_DECIMALS;++i) {
            interpreters[i] = new FloatInterpreter(i);
        }
    }

    private int decimals;

    private FloatInterpreter(int decimals) {
        this.decimals = decimals;
    }

    public static FloatInterpreter build(int decimals) {
        return interpreters[Math.min(MAX_DECIMALS, Math.max(decimals, 0))];
    }

    @Override
    public String interpret(double value) {
        return SimpleFormatter.keepDecimal(value, decimals);
    }
}
