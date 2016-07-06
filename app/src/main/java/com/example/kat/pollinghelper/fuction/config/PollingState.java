package com.example.kat.pollinghelper.fuction.config;

/**
 * Created by KAT on 2016/5/11.
 */
public enum PollingState {
    PS_COMPLETED("已完成"),
    PS_RUNNING("进行中"),
    PS_UNDONE("未完成"),
    PS_UNKNOWN("未知");

    PollingState(String lable) {
        this.lable = lable;
    }

    @Override
    public String toString() {
        return lable;
    }

    private String lable;
}
