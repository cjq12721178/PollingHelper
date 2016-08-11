package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.communicator.Udp;
import com.example.kat.pollinghelper.utility.Converter;

/**
 * Created by KAT on 2016/8/11.
 */
public class ModifyBaseStationIpOrPort extends Operation {

    public ModifyBaseStationIpOrPort(OperationInfo operationInfo,
                                     Udp udp,
                                     Context context) {
        super(operationInfo);
        this.udp = udp;
        this.context = context;
    }

    @Override
    protected boolean onExecute() {
        SharedPreferences configs = context.getSharedPreferences(context.getString(R.string.file_function_setting), context.MODE_PRIVATE);
        String ip = configs.getString(context.getString(R.string.key_ip), context.getString(R.string.base_station_ip));
        int port = Converter.stringToInt(configs.getString(context.getString(R.string.key_port), null),
                context.getResources().getInteger(R.integer.base_station_port));
        udp.connect(udp.getParameter().setAddress(ip).setPort(port), false);
        return udp.isConnected();
    }

    private Context context;
    private Udp udp;
}
