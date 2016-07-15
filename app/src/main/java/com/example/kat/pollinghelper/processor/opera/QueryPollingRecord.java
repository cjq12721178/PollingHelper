package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
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
        return queryInfo != null;
    }

    @Override
    protected boolean onExecute() {

        List<PollingProjectRecord> result = null;
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

    private List<PollingProjectRecord> queryProjectRecordsInFinishedTimeRange(List<PollingProjectConfig> projectConfigs,
                                                                              Date begFinishedTime,
                                                                              Date endFinishedTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        List<PollingProjectRecord> result = null;

        //TODO 查询projectConfigs所列项目在给出的完成时间范围内(begFinishedTime, endFinishedTime)的巡检记录
        //注意，begFinishedTime为null意味着没有时间上限，即从最初到endFinishedTime的范围
        //即从最初到endFinishedTime的范围为null则是从begFinishedTime到现在

        return result;
    }

    private List<PollingProjectRecord> queryProjectRecordInScheduledTimeRange(List<PollingProjectConfig> projectConfigs,
                                                                              Date begScheduledTime,
                                                                              Date endScheduledTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        List<PollingProjectRecord> result = null;

        //TODO 查询projectConfigs所列项目在给出的巡检时间范围内(begScheduledTime, endScheduledTime)的巡检记录
        //注意，begScheduledTime为null意味着没有时间上限，即从最初到endScheduledTime的范围
        //即从最初到endScheduledTime的范围为null则是从begScheduledTime到现在

        //debug
        result = new ArrayList<>();
        int id = 0;
        int hours = 0;
        double value = -13.2;
        for (PollingProjectConfig projectConfig :
                projectConfigs) {
            for (int i = 0;i < 3;++i) {
                PollingProjectRecord projectRecord = createProjectRecord(++id,
                        projectConfig, getDate(++hours), getDate(++hours),
                        EvaluationType.createFromIndex(hours % 3),
                        getState(hours % 4), getResult(hours % 5));
                for (PollingMissionConfig missionConfig :
                        projectConfig.getMissions()) {
                    PollingMissionRecord missionRecord = createMissionRecord(++id,
                            missionConfig, getDate(++hours),
                            EvaluationType.createFromIndex(hours % 3),
                            getState(hours % 4), getResult(hours % 5));
                    for (PollingItemConfig itemConfig :
                            missionConfig.getItems()) {
                        PollingItemRecord itemRecord = createItemRecord(++id,
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
    private PollingState getState(int index) {
        switch (index) {
            case 0:return PollingState.PS_UNDONE;
            case 1:return PollingState.PS_UNKNOWN;
            case 2:return PollingState.PS_RUNNING;
            case 3:return PollingState.PS_COMPLETED;
            default:return PollingState.PS_UNKNOWN;
        }
    }
    private Date getDate(int hours) {
        return new Date(System.currentTimeMillis() - hours * 60 * 60 * 1000);
    }
    private PollingProjectRecord createProjectRecord(long id,
                                                     PollingProjectConfig projectConfig,
                                                     Date finishTime,
                                                     Date scheduleTime,
                                                     EvaluationType type,
                                                     PollingState state,
                                                     String recordResult) {
        PollingProjectRecord projectRecord = new PollingProjectRecord(id, projectConfig);
        projectRecord.setScheduledTime(scheduleTime);
        projectRecord.setFinishedTime(finishTime);
        projectRecord.setEvaluationType(type);
        projectRecord.setPollingState(state);
        projectRecord.setRecordResult(recordResult);
        return projectRecord;
    }
    private PollingMissionRecord createMissionRecord(long id,
                                                     PollingMissionConfig missionConfig,
                                                     Date finishTime,
                                                     EvaluationType type,
                                                     PollingState state,
                                                     String recordResult) {
        PollingMissionRecord missionRecord = new PollingMissionRecord(id, missionConfig);
        missionRecord.setFinishedTime(finishTime);
        missionRecord.setEvaluationType(type);
        missionRecord.setPollingState(state);
        missionRecord.setRecordResult(recordResult);
        return missionRecord;
    }
    private PollingItemRecord createItemRecord(long id,
                                               PollingItemConfig itemConfig,
                                               double value) {
        PollingItemRecord itemRecord = new PollingItemRecord(id, itemConfig);
        itemRecord.setValue(value);
        return itemRecord;
    }

    private List<PollingProjectRecord> queryLatestProjectRecord(List<PollingProjectConfig> projectConfigs,
                                                                Date begScheduledTime) {
        if (projectConfigs == null) {
            projectConfigs = getCurrentProjectConfigs();
        }

        if (begScheduledTime == null) {
            begScheduledTime = new Date(System.currentTimeMillis() - SimpleTime.DAY_MILLISECONDS);
        }

        List<PollingProjectRecord> result = null;
        //TODO 查询projectConfigs所列项目最近一次的巡检记录
        //建议，可以通过巡检项目名称、任务名称和条目ID来定向搜索，这样就
        //不用得到查询结果后还要使用getProjectConfig、getMissionConfig和getItemConfig这
        //三个函数进行查找然后再打包了，当然这样做查询的效率可能会降低，但整体效率应该会提高
        //个人建议，仅供参考，不喜勿喷，：）

        return result;
    }

    private List<PollingProjectConfig> getCurrentProjectConfigs() {
        return (List<PollingProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG);
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
        List<PollingProjectConfig> projectConfigs = getCurrentProjectConfigs();

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
        List<PollingProjectConfig> projectConfigs = getCurrentProjectConfigs();

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
        List<PollingProjectConfig> projectConfigs = getCurrentProjectConfigs();

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
