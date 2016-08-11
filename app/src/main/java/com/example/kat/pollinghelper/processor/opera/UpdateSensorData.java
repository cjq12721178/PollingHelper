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
        return missionRecord != null;
    }

    @Override
    protected boolean onExecute() {
        for (ScoutItemRecord itemRecord :
                missionRecord.getItemRecords()) {
            itemRecord.setValue(dataStorage.getRealTimeData(itemRecord.getItemConfig().getSensor().getAddress()));
        }
        //debug
//        for (ScoutItemRecord itemRecord :
//                missionRecord.getItemRecords()) {
//            itemRecord.setValue(1);
//        }
//        missionRecord.getItemRecords().get(0).setValue(55.929);
//        missionRecord.getItemRecords().get(1).setValue(55.942);
//        missionRecord.getItemRecords().get(2).setValue(55.969);
        return true;
    }
}
