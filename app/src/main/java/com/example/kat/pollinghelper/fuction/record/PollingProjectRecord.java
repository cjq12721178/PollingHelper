package com.example.kat.pollinghelper.fuction.record;

import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingState;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by KAT on 2016/5/9.
 */
public class PollingProjectRecord {

    public PollingProjectRecord(long id, PollingProjectConfig projectConfig) {
        this.id = id;
        this.projectConfig = projectConfig;
        state = PollingState.PS_UNDONE;
        evaluationType = EvaluationType.ET_NORMAL;
        missionRecords = new ArrayList<>();
        changed = false;
        recordResult = "";
    }

    public long getId() {
        return id;
    }

    public PollingProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public PollingState getPollingState() {
        return state;
    }

    public String getRecordResult() {
        return recordResult;
    }

    public ArrayList<PollingMissionRecord> getMissionRecords() {
        return missionRecords;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        if (this.evaluationType != evaluationType) {
            this.evaluationType = evaluationType;
            changed = true;
        }
    }

    public void setRecordResult(String recordResult) {
        if (!this.recordResult.equals(recordResult)) {
            this.recordResult = recordResult;
            changed = true;
        }
    }

    public void setPollingState(PollingState state) {
        if (this.state != state) {
            this.state = state;
            changed = true;
        }
    }

    public void setFinishedTime() {
        if (changed) {
            finishedTime = new Date();
            changed = false;
        }
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    private boolean changed;
    private EvaluationType evaluationType;
    private final long id;
    private final PollingProjectConfig projectConfig;
    private Date scheduledTime;
    private Date finishedTime;
    private PollingState state;
    private String recordResult;
    private ArrayList<PollingMissionRecord> missionRecords;
}
