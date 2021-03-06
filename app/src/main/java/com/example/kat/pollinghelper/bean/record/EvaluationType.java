package com.example.kat.pollinghelper.bean.record;

/**
 * Created by KAT on 2016/5/11.
 */
public enum EvaluationType {
    ET_GOOD("好"),
    ET_NORMAL("正常"),
    ET_BAD("差");

    EvaluationType(String lable) {
        this.lable = lable;
    }

    public static EvaluationType createFromString(String lable) {
        switch (lable) {
            case "好": return ET_GOOD;
            case "正常": return ET_NORMAL;
            case "差": return ET_BAD;
            default: throw new IllegalArgumentException();
        }
    }

    public static EvaluationType createFromIndex(int lable) {
        switch (lable) {
            case 0: return ET_GOOD;
            case 1: return ET_NORMAL;
            case 2: return ET_BAD;
            default: throw new IllegalArgumentException();
        }
    }

    public static EvaluationType createFromString(CharSequence lable) {
        return createFromString(lable.toString());
    }

    @Override
    public String toString() {
        return lable;
    }

    private String lable;
}
