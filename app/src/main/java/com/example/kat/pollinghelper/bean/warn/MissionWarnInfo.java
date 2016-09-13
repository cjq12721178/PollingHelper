package com.example.kat.pollinghelper.bean.warn;

import android.util.Log;

import com.example.kat.pollinghelper.bean.config.ScoutItemConfig;
import com.example.kat.pollinghelper.bean.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.utility.SimpleFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/8/29.
 */
public class MissionWarnInfo {

    public interface OnWarnOccurredListener {
        void onWarnOccurred(MissionWarnInfo missionWarn);
    }

    public class OnSensorValueWarnListener implements SensorValue.OnValueChangedListener {

        @Override
        public void onValueChanged(long timeStamp, double value) {
            if (value < itemConfig.getDownAlarm() || value > itemConfig.getUpAlarm()) {
                add(itemConfig, timeStamp, value);
            }
        }

        public OnSensorValueWarnListener(ScoutItemConfig itemConfig) {
            this.itemConfig = itemConfig;
            isInstalled = false;
        }

        public boolean isInstalled() {
            return isInstalled;
        }

        public void setInstalled(boolean installed) {
            isInstalled = installed;
        }

        private boolean isInstalled;
        private final ScoutItemConfig itemConfig;
    }

    public MissionWarnInfo(String projectName, ScoutMissionConfig missionConfig, int warnInfoCapacity) {
        if (missionConfig == null)
            throw new NullPointerException("ScoutMissionConfig = null");
        this.projectName = projectName;
        this.missionConfig = missionConfig;
        //设置itemWarnInfoMaxCapacity
        if (warnInfoCapacity <= 0) {
            itemWarnInfoMaxCapacity = DEFAULT_ITEM_WARN_INFO_CAPACITY;
        } else if (warnInfoCapacity < MIN_ITEM_WARN_INFO_CAPACITY) {
            itemWarnInfoMaxCapacity = MIN_ITEM_WARN_INFO_CAPACITY;
        } else {
            itemWarnInfoMaxCapacity = warnInfoCapacity;
        }
        itemWarnInfoCurrentCapacity = MIN_ITEM_WARN_INFO_CAPACITY;
        itemWarnInfoList = new ArrayList<>(itemWarnInfoCurrentCapacity);
        onSensorValueWarnListeners = new OnSensorValueWarnListener[missionConfig.getItems().size()];
        for (int i = 0;i < onSensorValueWarnListeners.length;++i) {
            onSensorValueWarnListeners[i] = new OnSensorValueWarnListener(missionConfig.getItems().get(i));
        }
        currentLoopIndex = 0;
    }

    public MissionWarnInfo(String projectName, ScoutMissionConfig missionConfig) {
        this(projectName, missionConfig, 0);
    }

    private void add(ScoutItemConfig itemConfig, long warnTime, double currentValue) {
        if (itemConfig == null)
            return;
        int size = itemWarnInfoList.size();
        if (size < itemWarnInfoMaxCapacity) {
            if (size >= itemWarnInfoCurrentCapacity) {
                int increaseCapacity = itemWarnInfoCurrentCapacity;
                while (size > itemWarnInfoCurrentCapacity) {
                    itemWarnInfoCurrentCapacity += increaseCapacity;
                }
                if (itemWarnInfoCurrentCapacity > itemWarnInfoMaxCapacity) {
                    itemWarnInfoCurrentCapacity = itemWarnInfoMaxCapacity;
                }
                synchronized (itemWarnInfoList) {
                    itemWarnInfoList.ensureCapacity(itemWarnInfoCurrentCapacity);
                }
            }
            synchronized (itemWarnInfoList) {
                itemWarnInfoList.add(new ItemWarnInfo(itemConfig, warnTime, currentValue));
            }
        } else {
            if (itemWarnInfoList.get(currentLoopIndex).reset(itemConfig, warnTime, currentValue)) {
                if (++currentLoopIndex >= itemWarnInfoMaxCapacity) {
                    currentLoopIndex = 0;
                }
            }
        }
        if (onWarnOccurredListener != null) {
            onWarnOccurredListener.onWarnOccurred(this);
        }
    }

    public synchronized ItemWarnInfo get(int position) {
        return position + currentLoopIndex >= itemWarnInfoMaxCapacity ?
                itemWarnInfoList.get(itemWarnInfoMaxCapacity - (position + currentLoopIndex - itemWarnInfoMaxCapacity) - 1) :
                itemWarnInfoList.get(itemWarnInfoList.size() - 1 - position - currentLoopIndex);
    }

    public synchronized int size() {
        return itemWarnInfoList.size();
    }

    public String getProjectName() {
        return projectName;
    }

    public ScoutMissionConfig getMissionConfig() {
        return missionConfig;
    }

    public OnSensorValueWarnListener getSensorValueWarnListener(int index) {
        return onSensorValueWarnListeners[index];
    }

    public int sensorValueWarnListenerSize() {
        return onSensorValueWarnListeners.length;
    }

    public void setOnWarnOccurredListener(OnWarnOccurredListener onWarnOccurredListener) {
        this.onWarnOccurredListener = onWarnOccurredListener;
    }

    private static final int DEFAULT_ITEM_WARN_INFO_CAPACITY = 50;
    private static final int MIN_ITEM_WARN_INFO_CAPACITY = 10;
    private final String projectName;
    private final ScoutMissionConfig missionConfig;
    private ArrayList<ItemWarnInfo> itemWarnInfoList;
    private final int itemWarnInfoMaxCapacity;
    private int itemWarnInfoCurrentCapacity;
    private int currentLoopIndex;
    private OnWarnOccurredListener onWarnOccurredListener;
    private final OnSensorValueWarnListener[] onSensorValueWarnListeners;
}
