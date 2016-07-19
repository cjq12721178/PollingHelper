package com.example.kat.pollinghelper.structure;

import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.structure.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.structure.config.SimpleTime;
import com.example.kat.pollinghelper.structure.record.ScoutItemRecord;
import com.example.kat.pollinghelper.structure.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.structure.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.utility.IdentifierGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by KAT on 2016/5/10.
 */
public class ScoutInfo {
    public ScoutInfo() {
        projectConfigs = new ArrayList<>();
        projectRecords = new ArrayList<>();
        sensorConfigs = new ArrayList<>();
    }

    public List<ScoutProjectConfig> getProjectConfigs() {
        return projectConfigs;
    }

    public boolean generateProjectRecords(List<ScoutProjectConfig> projectConfigs,
                                          List<ScoutSensorConfig> sensorConfigs,
                                          List<ScoutProjectRecord> latestProjectRecords) {
        boolean result = false;
        if (projectConfigs != null && sensorConfigs != null) {
            this.projectConfigs = projectConfigs;
            this.sensorConfigs = sensorConfigs;
            projectRecords.clear();
            for (ScoutProjectConfig projectConfig :
                    projectConfigs) {
                ScoutProjectRecord projectRecord = getHistoryProjectRecord(latestProjectRecords, projectConfig);
                if (projectRecord == null) {
                    projectRecord = createNewProjectRecord(projectConfig);
                }
                if (projectRecord != null) {
                    projectRecords.add(projectRecord);
                }
            }
            result = true;
        }
        return result;
    }

    //在创建一个新的巡检项目记录的同时，替换原先的巡检项目记录
    public ScoutProjectRecord generateNewProjectRecord(ScoutProjectConfig projectConfig) {
        ScoutProjectRecord newProjectRecord = createNewProjectRecord(projectConfig);
        if (newProjectRecord != null) {
            int recordIndex = projectConfigs.indexOf(projectConfig);
            projectRecords.remove(recordIndex);
            projectRecords.add(recordIndex, newProjectRecord);
        }
        return newProjectRecord;
    }

    //只是单纯的创建一个新的巡检项目记录
    private ScoutProjectRecord createNewProjectRecord(ScoutProjectConfig projectConfig) {
        if (projectConfig == null)
            return null;

        ScoutProjectRecord projectRecord = new ScoutProjectRecord(IdentifierGenerator.get64(), projectConfig);
        projectRecord.setScheduledTime(projectConfig.getCurrentScheduledTime(System.currentTimeMillis(), true));
        for (ScoutMissionConfig missionConfig:
                projectConfig.getMissions()) {
            ScoutMissionRecord missionRecord = new ScoutMissionRecord(IdentifierGenerator.get64(), missionConfig);
            for (ScoutItemConfig itemConfig :
                    missionConfig.getItems()) {
                missionRecord.getItemRecords().add(new ScoutItemRecord(IdentifierGenerator.get64(), itemConfig));
            }
            projectRecord.getMissionRecords().add(missionRecord);
        }
        return projectRecord;
    }

    private ScoutProjectRecord getHistoryProjectRecord(List<ScoutProjectRecord> historyProjectRecords,
                                                       ScoutProjectConfig projectConfig) {
        if (historyProjectRecords == null)
            return null;

        for (ScoutProjectRecord projectRecord :
                historyProjectRecords) {
            if (projectConfig.equals(projectRecord.getProjectConfig()))
                return projectRecord;
        }

        return null;
    }

    public List<ScoutProjectRecord> getProjectRecords() {
        return projectRecords;
    }

    public List<ScoutSensorConfig> getSensorConfigs() {
        return sensorConfigs;
    }

    public HashSet<ProjectTimeInfo> getProjectScheduleTimeInfo() {
        HashSet<ProjectTimeInfo> projectTimeInfoSet = new HashSet<>();
        for (ScoutProjectConfig projectConfig :
                projectConfigs) {
            for (SimpleTime time :
                    projectConfig.getScheduledTimes()) {
                projectTimeInfoSet.add(new ProjectTimeInfo(projectConfig.getName(), time));
            }
        }
        return projectTimeInfoSet;
    }

    private List<ScoutSensorConfig> sensorConfigs;
    private List<ScoutProjectRecord> projectRecords;
    private List<ScoutProjectConfig> projectConfigs;
}
