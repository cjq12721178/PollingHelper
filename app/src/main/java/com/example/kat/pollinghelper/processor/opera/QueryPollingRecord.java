package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.fuction.record.EvaluationType;
import com.example.kat.pollinghelper.fuction.record.PollingItemRecord;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;
import com.example.kat.pollinghelper.io.sqlite.InspRecordIterm;
import com.example.kat.pollinghelper.io.sqlite.InspRecordMission;
import com.example.kat.pollinghelper.io.sqlite.InspRecordProject;
import com.example.kat.pollinghelper.ui.structure.QueryInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KAT on 2016/7/8.
 */
public class QueryPollingRecord extends Operation {

    private QueryInfo queryInfo;

    public QueryPollingRecord(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        queryInfo = (QueryInfo)getValue(ArgumentTag.AT_QUERY_INFO);
        return true;
    }

    @Override
    protected boolean onExecute() {
        //TODO 根据queryInfo进行查询，详见该类注释
        //注意，queryInfo可能为null，如果为null则部分查询参数通过getValue得到
        //这个主要针对LATEST_RECORD_FOR_PER_PROJECT，具体见QueryInfo类注释
        importProjectRecordLast7day();
        return true;
    }



    private void importProjectRecordLast7day() {
        List<PollingProjectRecord> projectRecords = new ArrayList<>();
        InspRecordProject project = new  InspRecordProject();
        //载入项目记录
        Date endDate = new Date();
        Date startDate = new Date();
        Long timeTick = startDate.getTime();
        startDate.setTime(timeTick - 7*24*3600*1000);
        List<InspRecordProject> dataList = project.query(startDate,endDate);

        if (dataList == null)
            return;

        for (InspRecordProject itermProject : dataList)
        {
            PollingProjectConfig projectConfig = getProjectConfig(itermProject.getName_project());
            if(projectConfig == null)
                continue;

            PollingProjectRecord projectRecord = new PollingProjectRecord(itermProject.getId(), projectConfig);
            projectRecord.setScheduledTime(itermProject.getDate());
            projectRecord.setFinishedTime(itermProject.getFinishDate());
            projectRecord.setEvaluationType(EvaluationType.createFromIndex(itermProject.getState()));
            projectRecord.setRecordResult(itermProject.getResult());

            switch (itermProject.getState_process()){
                case 0:
                    projectRecord.setPollingState(PollingState.PS_COMPLETED);
                    break;
                case 1:
                    projectRecord.setPollingState(PollingState.PS_RUNNING);
                    break;
                case 2:
                    projectRecord.setPollingState(PollingState.PS_UNDONE);
                    break;

                default:
                    projectRecord.setPollingState(PollingState.PS_UNDONE);
            }

            InspRecordMission mission = new  InspRecordMission();
            List<InspRecordMission> missionList = mission.queryByIdRecordProject(itermProject.getId());

            for (InspRecordMission itermMission : missionList)
            {
                PollingMissionConfig missionConfig = getMissionConfig(itermProject.getName_project(), itermMission.getName_mission());
                if (missionConfig==null)continue;
                PollingMissionRecord missionRecord = new PollingMissionRecord(itermMission.getId(), missionConfig);
                missionRecord.setRecordResult(itermMission.getDesc());
                missionRecord.setEvaluationType(EvaluationType.createFromIndex(itermMission.getState()));
                missionRecord.setFinishedTime(itermMission.getDate());

                switch (itermMission.getState_process()){
                    case 0:
                        missionRecord.setPollingState(PollingState.PS_COMPLETED);
                        break;
                    case 1:
                        missionRecord.setPollingState(PollingState.PS_RUNNING);
                        break;
                    case 2:
                        missionRecord.setPollingState(PollingState.PS_UNDONE);
                        break;
                    default:
                        projectRecord.setPollingState(PollingState.PS_UNDONE);
                }

                InspRecordIterm value = new  InspRecordIterm();
                List<InspRecordIterm> valueList = value.queryByIdRecordMission(itermMission.getId());

                for (InspRecordIterm itermValue : valueList)
                {
                    PollingItemConfig itemConfig = getItermConfig(itermProject.getName_project(), itermMission.getName_mission(), itermValue.getName_measure());
                    PollingItemRecord itemRecord= new PollingItemRecord(itermValue.getId(), itemConfig);
                    itemRecord.setValue(itermValue.getValue());
                    missionRecord.getItemRecords().add(itemRecord);
                }

                projectRecord.getMissionRecords().add(missionRecord);
            }

            projectRecords.add(projectRecord);
        }


        setValue(ArgumentTag.AT_LIST_LATEST_PROJECT_RECORD, projectRecords);

    }

    private PollingItemConfig getItermConfig(String name_project, String name_mission, String name_measure) {
        List<PollingProjectConfig> projectConfigs = (List<PollingProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);

        for (PollingProjectConfig iterm : projectConfigs){
            if (iterm.getName().equals(name_project)){
                for (PollingMissionConfig itermMission : iterm.getMissions()){
                    if (itermMission.getName().equals(name_mission)){

                        for (PollingItemConfig itermConfig : itermMission.getItems()){
                            if (itermConfig.getMeasureName().equals(name_measure))
                                return  itermConfig;
                        }
                    }

                }
            }

        }

        return null;
    }

    private PollingMissionConfig getMissionConfig(String name_project, String name_mission) {
        List<PollingProjectConfig> projectConfigs = (List<PollingProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);

        for (PollingProjectConfig iterm : projectConfigs){
            if (iterm.getName().equals(name_project)){
                for (PollingMissionConfig itermMission : iterm.getMissions()){
                    if (itermMission.getName().equals(name_mission))
                        return  itermMission;
                }
            }

        }

        return null;
    }

    private PollingProjectConfig getProjectConfig(String name_project) {
        List<PollingProjectConfig> projectConfigs = (List<PollingProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);

        for (PollingProjectConfig iterm : projectConfigs){
            if (iterm.getName().equals(name_project))
                return  iterm;
        }

        return null;
    }

    @Override
    protected boolean onPostExecute() {
        setValue(ArgumentTag.AT_QUERY_INFO, null);
        return true;
    }
}
