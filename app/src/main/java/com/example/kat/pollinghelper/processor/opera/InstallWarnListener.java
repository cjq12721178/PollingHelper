package com.example.kat.pollinghelper.processor.opera;

import android.util.Log;

import com.example.kat.pollinghelper.bean.config.ScoutItemConfig;
import com.example.kat.pollinghelper.bean.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.bean.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.bean.warn.MissionWarnInfo;
import com.example.kat.pollinghelper.data.DataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by KAT on 2016/9/5.
 */
public class InstallWarnListener extends Operation {

    public InstallWarnListener(OperationInfo operationInfo,
                               List<MissionWarnInfo> warnInfo,
                               DataStorage dataStorage) {
        super(operationInfo);
        this.warnInfo = warnInfo;
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        if (warnInstallTimer != null) {
            //停止当前告警信息监听器安装活动
            warnInstallTimer.cancel();
            //清除原有告警信息监听器
            dataStorage.clearAllSensorValueListener();
            //清空告警信息
            warnInfo.clear();
        }

        projectConfigs = (List<ScoutProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);
        List<ScoutSensorConfig> sensorConfigs = (List<ScoutSensorConfig>)getValue(ArgumentTag.AT_LIST_SENSOR_CONFIG);
        return projectConfigs != null && sensorConfigs != null;
    }

    @Override
    protected boolean onExecute() {
        //重建计时器
        warnInstallTimer = new Timer();
        //根据当前巡检项目配置生成新的告警信息体系
        generateWarnInfo();
        //上载告警信息
        setValue(ArgumentTag.AT_WARN_INFO, warnInfo);
        //启动新的告警信息监听器安装活动
        warnInstallTimer.schedule(getWarnListenerInstallTask(), 30000, 60000);
        return true;
    }

    private void generateWarnInfo() {
        for (ScoutProjectConfig projectConfig :
                projectConfigs) {
            for (ScoutMissionConfig missionConfig:
                    projectConfig.getMissions()) {
                warnInfo.add(new MissionWarnInfo(projectConfig.getName(), missionConfig));
            }
        }
    }

    private TimerTask getWarnListenerInstallTask() {
        return new TimerTask() {
            @Override
            public void run() {
                boolean isAllItemInstalledWarnListener = true;
                boolean isListenerInstalled = false;
                for (MissionWarnInfo missionWarn :
                        warnInfo) {
                    ArrayList<ScoutItemConfig> itemConfigs = missionWarn.getMissionConfig().getItems();
                    for (int i = 0, n = Math.min(missionWarn.sensorValueWarnListenerSize(), itemConfigs.size()); i < n; ++i) {
                        ScoutItemConfig itemConfig = itemConfigs.get(i);
                        MissionWarnInfo.OnSensorValueWarnListener listener = missionWarn.getSensorValueWarnListener(i);
                        if (!listener.isInstalled()) {
                            isListenerInstalled = dataStorage.setSensorValueListener(itemConfig.getSensor().getAddress(), listener);
                            listener.setInstalled(isListenerInstalled);
                            isAllItemInstalledWarnListener &= isListenerInstalled;
                        }
                    }
                }
                if (isAllItemInstalledWarnListener) {
                    this.cancel();
                }
            }
        };
    }

    private final List<MissionWarnInfo> warnInfo;
    private final DataStorage dataStorage;
    private Timer warnInstallTimer;
    private List<ScoutProjectConfig> projectConfigs;
}
