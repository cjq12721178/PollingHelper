package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;

import com.example.kat.pollinghelper.io.sqlite.DBData;

/**
 * Created by KAT on 2016/6/27.
 */
public class EstablishScoutDatabase extends Operation {

    private final Context context;

    public EstablishScoutDatabase(OperationInfo operationInfo, Context context) {
        super(operationInfo);
        this.context = context;
    }

    @Override
    protected boolean onExecute() {
        //TODO 有时间引入执行是否成功判断
        DBData.createDatabaseEvn(context);
        return true;
    }
}
