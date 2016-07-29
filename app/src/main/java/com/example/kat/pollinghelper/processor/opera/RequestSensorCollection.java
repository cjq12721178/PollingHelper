package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.ui.adapter.SlipPageAdapter;

/**
 * Created by KAT on 2016/7/28.
 */
public class RequestSensorCollection extends Operation {

    public RequestSensorCollection(OperationInfo operationInfo, DataStorage dataStorage) {
        super(operationInfo);
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        onDataListener = (DataStorage.OnDataListener)getValue(ArgumentTag.AT_DATA_LISTENER);
        return true;
    }

    @Override
    protected boolean onExecute() {
        dataStorage.setOnDataListener(onDataListener);
        return true;
    }

    private DataStorage.OnDataListener onDataListener;
    private DataStorage dataStorage;
}
