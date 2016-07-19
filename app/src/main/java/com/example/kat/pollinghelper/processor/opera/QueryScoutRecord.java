package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.structure.record.ScoutRecordState;
import com.example.kat.pollinghelper.structure.config.SimpleTime;
import com.example.kat.pollinghelper.structure.record.EvaluationType;
import com.example.kat.pollinghelper.structure.record.ScoutItemRecord;
import com.example.kat.pollinghelper.structure.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.structure.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.io.sqlite.InspRecordIterm;
import com.example.kat.pollinghelper.io.sqlite.InspRecordMission;
import com.example.kat.pollinghelper.io.sqlite.InspRecordProject;
import com.example.kat.pollinghelper.structure.QueryInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KAT on 2016/7/8.
 */
public class QueryScoutRecord extends Operation {

    private QueryInfo queryInfo;

    public QueryScoutRecord(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        queryInfo = (QueryInfo)getValue(ArgumentTag.AT_QUERY_INFO);
        return queryInfo != null;
    }

    @Override
    protected boolean onExecute() {

        List<ScoutProjectRecord> result = null;
        switch (queryInfo.getIntent()) {
            case QueryInfo.LATEST_RECORD_FOR_PER_PROJECT: {
                result = queryLatestProjectRecord(queryInfo.getProjectConfigs(),
                        queryInfo.getBegScheduledTime());
            } break;
            case QueryInfo.WHOLE_RECORD_IN_SCHEDULED_TIME_RANGE: {
                result = queryProjectRecordInScheduledTimeRange(queryInfo.getProjectConfigs(),
                        queryInfo.getBegScheduledTime(), queryInfo.getEndScheduledTime());
            } break;
            case QueryInfo.WHOLE_RECORD_IN_FINISHED_TIME_RANGE: {
                result = queryProjectRecordsInFinishedTimeRange(queryInfo.getProjectConfigs(),
                        queryInfo.getBegFinishedTime(), queryInfo.getEndFinishedTime());
            } break;
            default:break;
        }
        setValue(ArgumentTag.AT_LIST_LATEST_PROJECT_RECORD, result);

        return true;
    }

    private List<ScoutProjectRecord> queryProjectRecordsInFinishedTimeRange(List<ScoutProjectConfig> projectConfigs,
                                                                            Date begFinishedTime,
                                                                            Date endFinishedTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        List<ScoutProjectRecord> result = null;

        //TODO 查询projectConfigs所列项目在给出的完成时间范围内(begFinishedTime, endFinishedTime)的巡检记录
        //注意，begFinishedTime为null意味着没有时间上限，即从最初到endFinishedTime的范围
        //即从最初到endFinishedTime的范围为null则是从begFinishedTime到现在

        return result;
    }

    private List<ScoutProjectRecord> queryProjectRecordInScheduledTimeRange(List<ScoutProjectConfig> projectConfigs,
                                                                            Date begScheduledTime,
                                                                            Date endScheduledTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        List<ScoutProjectRecord> result = null;

        //TODO 查询projectConfigs所列项目在给出的巡检时间范围内(begScheduledTime, endScheduledTime)的巡检记录
        //注意，begScheduledTime为null意味着没有时间上限，即从最初到endScheduledTime的范围
        //即从最初到endScheduledTime的范围为null则是从begScheduledTime到现在

        //debug
        result = new ArrayList<>();
        int id = 0;
        int hours = 0;
        double value = -13.2;
        for (ScoutProjectConfig projectConfig :
                projectConfigs) {
            for (int i = 0;i < 3;++i) {
                ScoutProjectRecord projectRecord = createProjectRecord(++id,
                        projectConfig, getDate(++hours), getDate(++hours),
                        EvaluationType.createFromIndex(hours % 3),
                        getState(hours % 4), getResult(hours % 5));
                for (ScoutMissionConfig missionConfig :
                        projectConfig.getMissions()) {
                    ScoutMissionRecord missionRecord = createMissionRecord(++id,
                            missionConfig, getDate(++hours),
                            EvaluationType.createFromIndex(hours % 3),
                            getState(hours % 4), getResult(hours % 5));
                    for (ScoutItemConfig itemConfig :
                            missionConfig.getItems()) {
                        ScoutItemRecord itemRecord = createItemRecord(++id,
                                itemConfig, value += 1.3);
                        missionRecord.getItemRecords().add(itemRecord);
                    }
                    projectRecord.getMissionRecords().add(missionRecord);
                }
                result.add(projectRecord);
            }
        }
        return result;
    }

    //debug
    private String getResult(int index) {
        switch (index) {
            case 0:return "还行";
            case 1:return "可以";
            case 2:return "怎么可能";
            case 3:return "就是这样的";
            case 4:return "好吧，你赢了";
            default:return "算你狠";
        }
    }
    private ScoutRecordState getState(int index) {
        switch (index) {
            case 0:return ScoutRecordState.PS_UNDONE;
            case 1:return ScoutRecordState.PS_UNKNOWN;
            case 2:return ScoutRecordState.PS_RUNNING;
            case 3:return ScoutRecordState.PS_COMPLETED;
            default:return ScoutRecordState.PS_UNKNOWN;
        }
    }
    private Date getDate(int hours) {
        return new Date(System.currentTimeMillis() - hours * 60 * 60 * 1000);
    }
    private ScoutProjectRecord createProjectRecord(long id,
                                                   ScoutProjectConfig projectConfig,
                                                   Date finishTime,
                                                   Date scheduleTime,
                                                   EvaluationType type,
                                                   ScoutRecordState state,
                                                   String recordResult) {
        ScoutProjectRecord projectRecord = new ScoutProjectRecord(id, projectConfig);
        projectRecord.setScheduledTime(scheduleTime);
        projectRecord.setFinishedTime(finishTime);
        projectRecord.setEvaluationType(type);
        projectRecord.setPollingState(state);
        projectRecord.setRecordResult(recordResult);
        return projectRecord;
    }
    private ScoutMissionRecord createMissionRecord(long id,
                                                   ScoutMissionConfig missionConfig,
                                                   Date finishTime,
                                                   EvaluationType type,
                                                   ScoutRecordState state,
                                                   String recordResult) {
        ScoutMissionRecord missionRecord = new ScoutMissionRecord(id, missionConfig);
        missionRecord.setFinishedTime(finishTime);
        missionRecord.setEvaluationType(type);
        missionRecord.setPollingState(state);
        missionRecord.setRecordResult(recordResult);
        return missionRecord;
    }
    private ScoutItemRecord createItemRecord(long id,
                                             ScoutItemConfig itemConfig,
                                             double value) {
        ScoutItemRecord itemRecord = new ScoutItemRecord(id, itemConfig);
        itemRecord.setValue(value);
        return itemRecord;
    }

    private List<ScoutProjectRecord> queryLatestProjectRecord(List<ScoutProjectConfig> projectConfigs,
                                                              Date begScheduledTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        if (begScheduledTime == null) {
            begScheduledTime = new Date(System.currentTimeMillis() - SimpleTime.DAY_MILLISECONDS);
        }

        List<ScoutProjectRecord> result = null;
        //TODO 查询projectConfigs所列项目最近一次的巡检记录
        //建议，可以通过巡检项目名称、任务名称和条目ID来定向搜索，这样就
        //不用得到查询结果后还要使用getProjectConfig、getMissionConfig和getItemConfig这
        //三个函数进行查找然后再打包了，当然这样做查询的效率可能会降低，但整体效率应该会提高
        //个人建议，仅供参考，不喜勿喷，：）

        return result;
    }

    private List<ScoutProjectConfig> getCurrentProjectConfigs() {
        return (List<ScoutProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);
    }

    private void importProjectRecordLast7day() {
        List<ScoutProjectRecord> projectRecords = new ArrayList<>();
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
            ScoutProjectConfig projectConfig = getProjectConfig(itermProject.getName_project());
            if(projectConfig == null)
                continue;

            ScoutProjectRecord projectRecord = new ScoutProjectRecord(itermProject.getId(), projectConfig);
            projectRecord.setScheduledTime(itermProject.getDate());
            projectRecord.setFinishedTime(itermProject.getFinishDate());
            projectRecord.setEvaluationType(EvaluationType.createFromIndex(itermProject.getState()));
            projectRecord.setRecordResult(itermProject.getResult());

            switch (itermProject.getState_process()){
                case 0:
                    projectRecord.setPollingState(ScoutRecordState.PS_COMPLETED);
                    break;
                case 1:
                    projectRecord.setPollingState(ScoutRecordState.PS_RUNNING);
                    break;
                case 2:
                    projectRecord.setPollingState(ScoutRecordState.PS_UNDONE);
                    break;

                default:
                    projectRecord.setPollingState(ScoutRecordState.PS_UNDONE);
            }

            InspRecordMission mission = new  InspRecordMission();
            List<InspRecordMission> missionList = mission.queryByIdRecordProject(itermProject.getId());

            for (InspRecordMission itermMission : missionList)
            {
                ScoutMissionConfig missionConfig = getMissionConfig(itermProject.getName_project(), itermMission.getName_mission());
                if (missionConfig==null)continue;
                ScoutMissionRecord missionRecord = new ScoutMissionRecord(itermMission.getId(), missionConfig);
                missionRecord.setRecordResult(itermMission.getDesc());
                missionRecord.setEvaluationType(EvaluationType.createFromIndex(itermMission.getState()));
                missionRecord.setFinishedTime(itermMission.getDate());

                switch (itermMission.getState_process()){
                    case 0:
                        missionRecord.setPollingState(ScoutRecordState.PS_COMPLETED);
                        break;
                    case 1:
                        missionRecord.setPollingState(ScoutRecordState.PS_RUNNING);
                        break;
                    case 2:
                        missionRecord.setPollingState(ScoutRecordState.PS_UNDONE);
                        break;
                    default:
                        projectRecord.setPollingState(ScoutRecordState.PS_UNDONE);
                }

                InspRecordIterm value = new  InspRecordIterm();
                List<InspRecordIterm> valueList = value.queryByIdRecordMission(itermMission.getId());

                for (InspRecordIterm itermValue : valueList)
                {
                    ScoutItemConfig itemConfig = getItermConfig(itermProject.getName_project(), itermMission.getName_mission(), itermValue.getName_measure());
                    ScoutItemRecord itemRecord= new ScoutItemRecord(itermValue.getId(), itemConfig);
                    itemRecord.setValue(itermValue.getValue());
                    missionRecord.getItemRecords().add(itemRecord);
                }

                projectRecord.getMissionRecords().add(missionRecord);
            }

            projectRecords.add(projectRecord);
        }


        setValue(ArgumentTag.AT_LIST_LATEST_PROJECT_RECORD, projectRecords);

    }

    private ScoutItemConfig getItermConfig(String name_project, String name_mission, String name_measure) {
        List<ScoutProjectConfig> projectConfigs = getCurrentProjectConfigs();

        for (ScoutProjectConfig iterm : projectConfigs){
            if (iterm.getName().equals(name_project)){
                for (ScoutMissionConfig itermMission : iterm.getMissions()){
                    if (itermMission.getName().equals(name_mission)){

                        for (ScoutItemConfig itermConfig : itermMission.getItems()){
                            if (itermConfig.getMeasureName().equals(name_measure))
                                return  itermConfig;
                        }
                    }

                }
            }

        }

        return null;
    }

    private ScoutMissionConfig getMissionConfig(String name_project, String name_mission) {
        List<ScoutProjectConfig> projectConfigs = getCurrentProjectConfigs();

        for (ScoutProjectConfig iterm : projectConfigs){
            if (iterm.getName().equals(name_project)){
                for (ScoutMissionConfig itermMission : iterm.getMissions()){
                    if (itermMission.getName().equals(name_mission))
                        return  itermMission;
                }
            }

        }

        return null;
    }

    private ScoutProjectConfig getProjectConfig(String name_project) {
        List<ScoutProjectConfig> projectConfigs = getCurrentProjectConfigs();

        for (ScoutProjectConfig iterm : projectConfigs){
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
