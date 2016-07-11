package com.example.kat.pollinghelper.fuction.record;

import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingState;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by KAT on 2016/5/9.
 */
public class PollingMissionRecord {
    public PollingMissionRecord(long id, PollingMissionConfig missionConfig) {
        this.id = id;
        this.missionConfig = missionConfig;
        state = PollingState.PS_UNDONE;
        evaluationType = EvaluationType.ET_NORMAL;
        sensorRecords = new ArrayList<>();
        changed = false;
        recordResult = "";
    }

    public long getId() {
        return id;
    }

    public String getRecordResult() {
        return recordResult;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public ArrayList<PollingItemRecord> getItemRecords() {
        return sensorRecords;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public PollingMissionConfig getMissionConfig() {
        return missionConfig;
    }

    public PollingState getPollingState() {
        return state;
    }

    public void setPollingState(PollingState state) {
        if (this.state != state) {
            this.state = state;
            changed = true;
        }
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

    public void setFinishedTime() {
        if (changed) {
            finishedTime = new Date();
            changed = false;
        }
    }

    public void setFinishedTime(Date time) {
        finishedTime =time;
    }

    private boolean changed;
    private PollingState state;
    private final PollingMissionConfig missionConfig;
    private Date finishedTime;
    private final long id;
    private String recordResult;
    private EvaluationType evaluationType;
    private ArrayList<PollingItemRecord> sensorRecords;
}
