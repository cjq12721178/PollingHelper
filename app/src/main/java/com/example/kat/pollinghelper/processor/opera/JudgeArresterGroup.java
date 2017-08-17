package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.bean.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.bean.record.ScoutItemRecord;
import com.example.kat.pollinghelper.bean.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.bean.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.data.DataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/6/13.
 */
public class JudgeArresterGroup extends Operation {

    private final DataStorage dataStorage;
    private ScoutProjectRecord projectRecord;

    public JudgeArresterGroup(OperationInfo operationInfo, DataStorage dataStorage) {
        super(operationInfo);
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        projectRecord = (ScoutProjectRecord) getValue(ArgumentTag.AT_PROJECT_RECORD_CURRENT);
        return projectRecord != null;
    }

    @Override
    protected boolean onExecute() {
        Map<String, List<ScoutMissionRecord>> missionRecordMap = groupMissionRecords();
        List<ScoutMissionRecord> missionOnLine = null;
        float minOffLineRate = 1.0f;
        for (List<ScoutMissionRecord> missionRecords :
                missionRecordMap.values()) {
            int offLineCount = 0;
            int count = 0;
            for (ScoutMissionRecord missionRecord :
                    missionRecords) {
                for (ScoutItemRecord itemRecord :
                        missionRecord.getItemRecords()) {
                    ++count;
                    if (dataStorage.isSensorOffLine(itemRecord.getItemConfig().getSensor().getAddress())) {
                        ++offLineCount;
                    }
                }
            }
            float offLineRate = (float)offLineCount/count;
            if (offLineRate < minOffLineRate) {
                minOffLineRate = offLineRate;
                missionOnLine = missionRecords;
            }
        }

        setValue(ArgumentTag.AT_LIST_MISSION_RECORD, missionOnLine);
        return true;
    }

    private Map<String, List<ScoutMissionRecord>> groupMissionRecords() {
        Map<String, List<ScoutMissionRecord>> missionRecordMap = new HashMap<>();
        for (ScoutMissionRecord missionRecord :
                projectRecord.getMissionRecords()) {
            ScoutMissionConfig missionConfig = missionRecord.getMissionConfig();
            String tag = missionConfig.getDescription();
            List<ScoutMissionRecord> missionRecords = missionRecordMap.get(tag);
            if (missionRecords == null) {
                missionRecords = new ArrayList<>();
                missionRecordMap.put(tag, missionRecords);
            }
            missionRecords.add(missionRecord);
        }
        return missionRecordMap;
    }
}
