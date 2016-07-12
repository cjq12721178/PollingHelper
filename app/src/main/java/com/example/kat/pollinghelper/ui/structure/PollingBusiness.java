package com.example.kat.pollinghelper.ui.structure;

import android.support.annotation.NonNull;

import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
import com.example.kat.pollinghelper.fuction.record.PollingItemRecord;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;
import com.example.kat.pollinghelper.utility.IdentifierGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by KAT on 2016/5/10.
 */
public class PollingBusiness {
    public PollingBusiness() {
        projectConfigs = new ArrayList<>();
        projectRecords = new ArrayList<>();
        sensorConfigs = new ArrayList<>();
    }

    public List<PollingProjectConfig> getProjectConfigs() {
        return projectConfigs;
    }

    public boolean generateProjectRecords(List<PollingProjectConfig> projectConfigs,
                                          List<PollingSensorConfig> sensorConfigs,
                                          List<PollingProjectRecord> latestProjectRecords) {
        boolean result = false;
        if (projectConfigs != null && sensorConfigs != null) {
            this.projectConfigs = projectConfigs;
            this.sensorConfigs = sensorConfigs;
            projectRecords.clear();
            for (PollingProjectConfig projectConfig :
                    projectConfigs) {
                PollingProjectRecord projectRecord = getHistoryProjectRecord(latestProjectRecords, projectConfig);
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
    public PollingProjectRecord generateNewProjectRecord(PollingProjectConfig projectConfig) {
        PollingProjectRecord newProjectRecord = createNewProjectRecord(projectConfig);
        if (newProjectRecord != null) {
            int recordIndex = projectConfigs.indexOf(projectConfig);
            projectRecords.remove(recordIndex);
            projectRecords.add(recordIndex, newProjectRecord);
        }
        return newProjectRecord;
    }

    //只是单纯的创建一个新的巡检项目记录
    private PollingProjectRecord createNewProjectRecord(PollingProjectConfig projectConfig) {
        if (projectConfig == null)
            return null;

        PollingProjectRecord projectRecord = new PollingProjectRecord(IdentifierGenerator.get64(), projectConfig);
        projectRecord.setScheduledTime(projectConfig.getCurrentScheduledTime(System.currentTimeMillis(), true));
        for (PollingMissionConfig missionConfig:
                projectConfig.getMissions()) {
            PollingMissionRecord missionRecord = new PollingMissionRecord(IdentifierGenerator.get64(), missionConfig);
            for (PollingItemConfig itemConfig :
                    missionConfig.getItems()) {
                missionRecord.getItemRecords().add(new PollingItemRecord(IdentifierGenerator.get64(), itemConfig));
            }
            projectRecord.getMissionRecords().add(missionRecord);
        }
        return projectRecord;
    }

    private PollingProjectRecord getHistoryProjectRecord(List<PollingProjectRecord> historyProjectRecords,
                                                         PollingProjectConfig projectConfig) {
        if (historyProjectRecords == null)
            return null;

        for (PollingProjectRecord projectRecord :
                historyProjectRecords) {
            if (projectConfig.equals(projectRecord.getProjectConfig()))
                return projectRecord;
        }

        return null;
    }

    public List<PollingProjectRecord> getProjectRecords() {
        return projectRecords;
    }

    public List<PollingSensorConfig> getSensorConfigs() {
        return sensorConfigs;
    }

    public HashSet<ProjectTimeInfo> getProjectScheduleTimeInfo() {
        HashSet<ProjectTimeInfo> projectTimeInfoSet = new HashSet<>();
        for (PollingProjectConfig projectConfig :
                projectConfigs) {
            for (SimpleTime time :
                    projectConfig.getScheduledTimes()) {
                projectTimeInfoSet.add(new ProjectTimeInfo(projectConfig.getName(), time));
            }
        }
        return projectTimeInfoSet;
    }

    private List<PollingSensorConfig> sensorConfigs;
    private List<PollingProjectRecord> projectRecords;
    private List<PollingProjectConfig> projectConfigs;
}
