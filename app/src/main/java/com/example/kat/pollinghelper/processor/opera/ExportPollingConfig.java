package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.io.sqlite.InspItermCfg;
import com.example.kat.pollinghelper.io.sqlite.InspMissionCfg;
import com.example.kat.pollinghelper.io.sqlite.InspProjectCfg;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemItemEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemMissionEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemProjectEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemType;

import java.util.List;

/**
 * Created by KAT on 2016/6/13.
 */
public class ExportPollingConfig extends Operation {

    private List<PollingConfigListItemProjectEntity> projectEntities;
    private List<PollingConfigListItemEntity> desertedEntities;

    public ExportPollingConfig(OperationInfo operationInfo) {
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
        projectEntities = (List<PollingConfigListItemProjectEntity>)getValue(ArgumentTag.AT_LIST_PROJECT_ENTITY);
        desertedEntities = (List<PollingConfigListItemEntity>)getValue(ArgumentTag.AT_LIST_ENTITY_DESERTED);
        return projectEntities != null && desertedEntities != null;
    }

    @Override
    protected boolean onExecute() {
        //TODO 有时间引入执行是否成功判断
        //删除数据处理
        for (PollingConfigListItemEntity deleteEntity : desertedEntities) {

            if(deleteEntity.getType().equals( PollingConfigListItemType.PCLIT_ITEM_ENTITY)){
                InspItermCfg InspIterm = new InspItermCfg();
                PollingConfigListItemItemEntity iterm = (PollingConfigListItemItemEntity)deleteEntity;
                InspIterm.deleteDB(iterm.getItemConfig().getId());
            }

            if(deleteEntity.getType().equals( PollingConfigListItemType.PCLIT_PROJECT_ENTITY)){
                InspProjectCfg project = new  InspProjectCfg();

                //任务信息
                InspMissionCfg missionDB = new InspMissionCfg();
                List<InspMissionCfg> missionList = missionDB.queryByProjectName(deleteEntity.getName());
                for (InspMissionCfg missionIterm : missionList) {

                    //条目信息
                    InspItermCfg InspIterm = new InspItermCfg();
                    List<InspItermCfg> itermList = InspIterm.queryByMission(missionIterm.getName());
                    for (InspItermCfg iterm : itermList) {
                        iterm.deleteDB(iterm.getId());
                    }
                    missionIterm.deleteDB(missionIterm.getName());
                }
                project.deleteDB(deleteEntity.getName());
            }
            if(deleteEntity.getType().equals( PollingConfigListItemType.PCLIT_MISSION_ENTITY)) {
                InspMissionCfg missionDB = new InspMissionCfg();
                InspItermCfg InspIterm = new InspItermCfg();
                List<InspItermCfg> itermList = InspIterm.queryByMission(deleteEntity.getName());
                for (InspItermCfg iterm : itermList) {
                    iterm.deleteDB(iterm.getId());
                }
                missionDB.deleteDB(deleteEntity.getName());
            }
        }
        /////////////////////////////////////////////////////////////////////////
        //修改，增加处理
        for (PollingConfigListItemProjectEntity projectEntity : projectEntities) {
            InspProjectCfg project = new  InspProjectCfg();
            project.setName( projectEntity.getProjectConfig().getName());
            project.setDesc(projectEntity.getProjectConfig().getDescription());

            int i=0;
            for(SimpleTime time:projectEntity.getProjectConfig().getScheduledTimes()){
                project.getTime_insp().add(i,time.getTime());
                i++;
            }
            project.setInsp_times(projectEntity.getProjectConfig().getScheduledTimes().size());

            //删除原条目
            project.deleteDB(projectEntity.getName());
            project.addDB(project);

            //project_mission
            for(i=0; i< projectEntity.getMissionEntitySize(); i++) {
                PollingConfigListItemMissionEntity mission = projectEntity.getMissionEntity(i);
                InspMissionCfg missionDB = new InspMissionCfg();
                missionDB.setName(mission.getMissionConfig().getName());
                missionDB.setDesc(mission.getMissionConfig().getDescription());
                missionDB.setDeviceImageData(mission.getMissionConfig().getDeviceImageData());
                missionDB.setNamePorjece(project.getName());

                missionDB.deleteDB(mission.getName());
                missionDB.addDB(missionDB);

                //PollingConfigListItemItemEntity
                for (int j = 0; j < mission.getItemEntitySize(); j++) {
                    PollingConfigListItemItemEntity iterm = mission.getItemEntity(j);
                    InspItermCfg itermDB = new InspItermCfg();
                    itermDB.setMeasureName(iterm.getItemConfig().getMeasureName());
                    itermDB.setDesc(iterm.getItemConfig().getDescription());
                    itermDB.setMeasureUnit(iterm.getItemConfig().getMeasureUnit());
                    itermDB.setId(iterm.getItemConfig().getId());
                    itermDB.setDown_alarm(iterm.getItemConfig().getDownAlarm());
                    itermDB.setUp_alarm(iterm.getItemConfig().getUpAlarm());
                    itermDB.setName_sensor_measure(iterm.getItemConfig().getSensor().getName());
                    itermDB.setNameMission(missionDB.getName());

                    itermDB.deleteDB(iterm.getItemConfig().getId());
                    itermDB.addDB(itermDB);
                }
            }
        }

        return true;
    }
}
