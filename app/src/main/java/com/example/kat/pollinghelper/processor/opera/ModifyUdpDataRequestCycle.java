package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.communicator.Udp;
import com.example.kat.pollinghelper.utility.Converter;

/**
 * Created by KAT on 2016/8/11.
 */
public class ModifyUdpDataRequestCycle extends Operation {

    public ModifyUdpDataRequestCycle(OperationInfo operationInfo,
                                     Udp udp,
                                     Context context) {
        super(operationInfo);
        this.udp = udp;
        this.context = context;
    }

    @Override
    protected boolean onExecute() {
        SharedPreferences configs = context.getSharedPreferences(context.getString(R.string.file_function_setting), context.MODE_PRIVATE);
        int requestDataCycle = Converter.stringToInt(configs.getString(context.getString(R.string.key_data_request_cycle), null),
                context.getResources().getInteger(R.integer.time_interval_request_data));
        return udp.setCirculateTime(requestDataCycle);
    }

    private Context context;
    private Udp udp;
}
