package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.communicator.Ble;

/**
 * Created by KAT on 2016/6/28.
 */
public class ScanBleSensor extends Operation {

    public ScanBleSensor(Ble ble, int scanDurationTime) {
        this.ble = ble;
        this.scanDurationTime = scanDurationTime;
    }

    @Override
    protected void onExecute() {
        ble.startScan(0, scanDurationTime);
    }

    private final Ble ble;
    private final int scanDurationTime;
}
