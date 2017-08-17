package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.bean.record.ScoutItemRecord;
import com.example.kat.pollinghelper.bean.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.bean.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.data.DataStorage;

import java.util.List;

/**
 * Created by KAT on 2016/6/13.
 */
public class UpdateMultipleMissionSensorValue extends Operation {

    private final DataStorage dataStorage;
    private List<ScoutMissionRecord> missionRecords;

    public UpdateMultipleMissionSensorValue(OperationInfo operationInfo, DataStorage dataStorage) {
        super(operationInfo);
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        missionRecords = (List<ScoutMissionRecord>)getValue(ArgumentTag.AT_LIST_MISSION_RECORD);
        return missionRecords != null;
    }

    @Override
    protected boolean onExecute() {
        for (ScoutMissionRecord missionRecord :
                missionRecords) {
            for (ScoutItemRecord itemRecord :
                    missionRecord.getItemRecords()) {
                itemRecord.setValue(dataStorage.getRealTimeData(itemRecord.getItemConfig().getSensor().getAddress()));
            }
        }
        return true;
    }
}
