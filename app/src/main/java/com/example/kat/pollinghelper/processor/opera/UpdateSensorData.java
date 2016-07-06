package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.fuction.record.PollingItemRecord;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;

/**
 * Created by KAT on 2016/6/13.
 */
public class UpdateSensorData extends Operation {

    public UpdateSensorData(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute(OperationInfo operationInfo) {
        missionRecord = (PollingMissionRecord)operationInfo.getArgument(ArgumentTag.AT_MISSION_RECORD_CURRENT);
        uiProcessor = (Runnable)operationInfo.getArgument(ArgumentTag.AT_RUNNABLE_UPDATE_SENSOR_DATA);
        return super.onPreExecute(operationInfo);
    }

    @Override
    protected void onExecute() {
        //TODO 使用communicator接受最新传感器数据
        //debug
//        missionRecord.getItemRecords().get(0).setValue(2.0);
//        missionRecord.getItemRecords().get(1).setValue(0.8);
//        missionRecord.getItemRecords().get(2).setValue(15.3);
        for (PollingItemRecord itemRecord :
                missionRecord.getItemRecords()) {
            itemRecord.setValue(dataStorage.getRealTimeData(itemRecord.getItemConfig().getSensor().getAddress()));
        }
    }

    private final DataStorage dataStorage;
    private PollingMissionRecord missionRecord;
}
