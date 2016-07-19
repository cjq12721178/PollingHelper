package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.io.sqlite.SensorCfg;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellSensorEntity;

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

//    @Override
//    protected void onPostExecute() {
//        uiProcessor = (Runnable)getValue(isSuccess ? ArgumentTag.AT_RUNNABLE_EXPORT_POLLING_CONFIGS_SUCCESS :
//                ArgumentTag.AT_RUNNABLE_EXPORT_POLLING_CONFIGS_FAILED);
//        super.onPostExecute();
//    }

    @Override
    protected boolean onPreExecute() {
        existSensorEntities = (List<ScoutCellSensorEntity>)getValue(ArgumentTag.AT_LIST_SENSOR_ENTITY_EXIST);
        desertedSensorEntities = (List<ScoutCellSensorEntity>)getValue(ArgumentTag.AT_LIST_SENSOR_ENTITY_DESERTED);
        return existSensorEntities != null && desertedSensorEntities != null;
    }

    @Override
    protected boolean onExecute() {
        //TODO 有时间引入执行是否成功判断

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
