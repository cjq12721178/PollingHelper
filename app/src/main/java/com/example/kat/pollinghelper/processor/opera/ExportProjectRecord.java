package com.example.kat.pollinghelper.processor.opera;

import android.util.Log;

import com.example.kat.pollinghelper.fuction.record.PollingItemRecord;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;
import com.example.kat.pollinghelper.io.sqlite.InspRecordIterm;
import com.example.kat.pollinghelper.io.sqlite.InspRecordMission;
import com.example.kat.pollinghelper.io.sqlite.InspRecordProject;

/**
 * Created by KAT on 2016/6/13.
 */
public class ExportProjectRecord extends Operation {

    private PollingProjectRecord projectRecord;

    public ExportProjectRecord(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        projectRecord = (PollingProjectRecord)getValue(ArgumentTag.AT_PROJECT_RECORD_CURRENT);
        return projectRecord != null;
    }

    @Override
    protected boolean onExecute() {
        boolean result = false;
        try {
            //执行处理
            exportConfig();
            result = true;
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return result;
    }

    private void exportConfig() {
        InspRecordProject projectRecordDB = new  InspRecordProject();
        projectRecordDB.setId( projectRecord.getId());
        Integer state_process = projectRecord.getPollingState().ordinal();
        projectRecordDB.setState_process(state_process);

        projectRecordDB.setState(projectRecord.getEvaluationType().ordinal());
        projectRecordDB.setName_project(projectRecord.getProjectConfig().getName());
        projectRecordDB.setResult(projectRecord.getRecordResult());
        projectRecordDB.setFinishDate(projectRecord.getFinishedTime());
        projectRecordDB.setDate(projectRecord.getScheduledTime());
        projectRecordDB.setDesc(projectRecord.getRecordResult());

        int i=0;
        for(PollingMissionRecord missionRecord:projectRecord.getMissionRecords()){
            InspRecordMission missionRecordDB = new InspRecordMission();

            missionRecordDB.setId(missionRecord.getId());
            missionRecordDB.setDate(missionRecord.getFinishedTime());
            missionRecordDB.setName_mission(missionRecord.getMissionConfig().getName());
            missionRecordDB.setId_project_record(projectRecord.getId());
            missionRecordDB.setState(missionRecord.getEvaluationType().ordinal());
            missionRecordDB.setState_process(missionRecord.getPollingState().ordinal());
            missionRecordDB.setDesc(missionRecord.getRecordResult());

            missionRecordDB.deleteDB(missionRecordDB.getId());
            missionRecordDB.addDB(missionRecordDB);

            for (PollingItemRecord itermRecord:missionRecord.getItemRecords()){
                InspRecordIterm itermRecordDB = new InspRecordIterm();
                itermRecordDB.setId_mission_record(missionRecordDB.getId());
                itermRecordDB.setId(itermRecord.getId());
                itermRecordDB.setValue(itermRecord.getValue());
                itermRecordDB.setName_measure(itermRecord.getItemConfig().getMeasureName());

                itermRecordDB.deleteDB(itermRecordDB.getId());
                itermRecordDB.addDB(itermRecordDB);
            }

        }
        //删除原条目
        projectRecordDB.deleteDB(projectRecordDB.getId());
        projectRecordDB.addDB(projectRecordDB);
    }
}
