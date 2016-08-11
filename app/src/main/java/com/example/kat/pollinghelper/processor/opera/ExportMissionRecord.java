package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.structure.record.ScoutItemRecord;
import com.example.kat.pollinghelper.structure.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.io.sqlite.InspRecordIterm;
import com.example.kat.pollinghelper.io.sqlite.InspRecordMission;

/**
 * Created by KAT on 2016/6/13.
 */
public class ExportMissionRecord extends Operation {

    private ScoutMissionRecord missionRecord;

    public ExportMissionRecord(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        missionRecord = (ScoutMissionRecord)getValue(ArgumentTag.AT_MISSION_RECORD_CURRENT);
        return true;
    }

    @Override
    protected boolean onExecute() {
        exportConfig();
        return true;
    }

    private void exportConfig() {
        if(true)
            return;

        InspRecordMission missionRecordDB = new InspRecordMission();

        missionRecordDB.setId(missionRecord.getId());
        missionRecordDB.setDate(missionRecord.getFinishedTime());
        missionRecordDB.setName_mission(missionRecord.getMissionConfig().getName());
        //missionRecordDB.setId_project_record(projectRecord.getId());
        missionRecordDB.setState(missionRecord.getEvaluationType().ordinal());
        missionRecordDB.setState(missionRecord.getPollingState().ordinal());
        missionRecordDB.setDesc(missionRecord.getRecordResult());

        missionRecordDB.deleteDB(missionRecordDB.getId());
        missionRecordDB.addDB(missionRecordDB);

        for (ScoutItemRecord itermRecord:missionRecord.getItemRecords()){
            InspRecordIterm itermRecordDB = new InspRecordIterm();
            itermRecordDB.setId_mission_record(missionRecordDB.getId());
            itermRecordDB.setId(itermRecord.getId());
            itermRecordDB.setValue(itermRecord.getValue());
            itermRecordDB.setName_measure(itermRecord.getItemConfig().getMeasureName());

            itermRecordDB.deleteDB(itermRecordDB.getId());
            itermRecordDB.addDB(itermRecordDB);
        }
    }
}
