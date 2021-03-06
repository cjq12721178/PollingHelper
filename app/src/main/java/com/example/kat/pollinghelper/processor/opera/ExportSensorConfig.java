package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.io.sqlite.SensorCfg;
import com.example.kat.pollinghelper.bean.scout.ScoutCellSensorEntity;

import java.util.List;

/**
 * Created by KAT on 2016/6/13.
 */
public class ExportSensorConfig extends Operation {

    private List<ScoutCellSensorEntity> existSensorEntities;
    private List<ScoutCellSensorEntity> desertedSensorEntities;

    public ExportSensorConfig(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        existSensorEntities = (List<ScoutCellSensorEntity>)getValue(ArgumentTag.AT_LIST_SENSOR_ENTITY_EXIST);
        desertedSensorEntities = (List<ScoutCellSensorEntity>)getValue(ArgumentTag.AT_LIST_SENSOR_ENTITY_DESERTED);
        return existSensorEntities != null && desertedSensorEntities != null;
    }

    @Override
    protected boolean onExecute() {

        Integer i=0;
        for(ScoutCellSensorEntity iterm:existSensorEntities){
            SensorCfg data = new SensorCfg();
            i++;

            data.setName(iterm.getName());
            data.setDesc(iterm.getSensorConfig().getDescription());
            data.setIndex(i);
            data.setAddr_text(iterm.getSensorConfig().getAddress());
            data.setSensor_name("");
            if(!data.addDB(data))
                data.updateDB(data);
        }

        for(ScoutCellSensorEntity iterm:desertedSensorEntities){
            SensorCfg data = new SensorCfg();
            data.deleteDB(iterm.getName());
        }

        return true;
    }
}
