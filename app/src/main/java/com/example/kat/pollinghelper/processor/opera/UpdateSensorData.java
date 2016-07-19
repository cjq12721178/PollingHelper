package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.structure.record.ScoutItemRecord;
import com.example.kat.pollinghelper.structure.record.ScoutMissionRecord;

/**
 * Created by KAT on 2016/6/13.
 */
public class UpdateSensorData extends Operation {

    private final DataStorage dataStorage;
    private ScoutMissionRecord missionRecord;

    public UpdateSensorData(OperationInfo operationInfo, DataStorage dataStorage) {
        super(operationInfo);
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        missionRecord = (ScoutMissionRecord)getValue(ArgumentTag.AT_MISSION_RECORD_CURRENT);
        //uiProcessor = (Runnable)operationInfo.getArgument(ArgumentTag.AT_RUNNABLE_UPDATE_SENSOR_DATA);
        return missionRecord != null;
    }

    @Override
    protected boolean onExecute() {
        for (ScoutItemRecord itemRecord :
                missionRecord.getItemRecords()) {
            itemRecord.setValue(dataStorage.getRealTimeData(itemRecord.getItemConfig().getSensor().getAddress()));
        }
        return true;
    }
}
