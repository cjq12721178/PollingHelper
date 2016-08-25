package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.communicator.Ble;
import com.example.kat.pollinghelper.utility.Converter;

/**
 * Created by KAT on 2016/6/28.
 */
public class ScanBleSensor extends Operation {

    public ScanBleSensor(OperationInfo operationInfo, Ble ble, Context context) {
        super(operationInfo);
        this.ble = ble;
        this.context = context;
    }

    @Override
    protected boolean onExecute() {
        SharedPreferences configs = context.getSharedPreferences(context.getString(R.string.file_function_setting), context.MODE_PRIVATE);
        int scanBleDuration = Converter.second2Millisecond(Converter.string2Int(configs.getString(context.getString(R.string.key_scan_duration), null),
                context.getResources().getInteger(R.integer.time_duration_scan_ble_communicator)));
        ble.startScan(0, scanBleDuration);
        return true;
    }

    private final Ble ble;
    private final Context context;
}
