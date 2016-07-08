package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.communicator.Ble;

/**
 * Created by KAT on 2016/6/28.
 */
public class ScanBleSensor extends Operation {

    private final Ble ble;
    private final int scanDurationTime;

    public ScanBleSensor(OperationInfo operationInfo, Ble ble, int scanDurationTime) {
        super(operationInfo);
        this.ble = ble;
        this.scanDurationTime = scanDurationTime;
    }

    @Override
    protected boolean onExecute() {
        ble.startScan(0, scanDurationTime);
        return true;
    }
}
