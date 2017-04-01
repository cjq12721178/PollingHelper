package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.data.DataStorage;

import java.util.List;

/**
 * Created by KAT on 2017/4/1.
 */

public class UpdateSensorMeasureName extends Operation {

    private final DataStorage dataStorage;
    private List<ScoutSensorConfig> sensorConfigs;

    public UpdateSensorMeasureName(OperationInfo operationInfo, DataStorage dataStorage) {
        super(operationInfo);
        this.dataStorage = dataStorage;
    }

    @Override
    protected boolean onPreExecute() {
        sensorConfigs = (List<ScoutSensorConfig>)getValue(ArgumentTag.AT_LIST_SENSOR_CONFIG);
        return sensorConfigs != null;
    }

    @Override
    protected boolean onExecute() {
        dataStorage.updateSensorMeasureName(sensorConfigs);
        return false;
    }
}
