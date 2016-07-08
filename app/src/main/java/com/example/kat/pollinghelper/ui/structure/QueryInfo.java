package com.example.kat.pollinghelper.ui.structure;

import java.util.Date;
import java.util.List;

/**
 * Created by KAT on 2016/7/8.
 */
public class QueryInfo {
    //搜索所有现有巡检项目的最新一次的巡检记录
    //注意，不是最近一次巡检时间的不要
    //如巡检项目有10:00,14:00,18:00三个巡检时间，当前时间为15:00，则只要14:00那一次巡检记录，
    //若搜索出来没有14:00的巡检记录，则不用添加到结果中
    //可以通过projectConfig.getCurrentScheduledTime(System.currentTimeMillis())
    //方式来获取预设巡检时间，将其作为搜索条件即可
    //projectConfig可以通过List<PollingProjectConfig> projectConfigs = (List<PollingProjectConfig>)getValue(ArgumentTag.AT_LIST_PROJECT_CONFIG)得到
    public static final int LATEST_RECORD_FOR_PER_PROJECT = 0;
    //使用到的参数：projectRecordNames、begScheduledTime和endScheduledTime
    public static final int WHOLE_RECORD_IN_SCHEDULED_TIME_RANGE = 1;
    //使用到的参数：projectRecordNames、begFinishedTime和endFinishedTime
    public static final int WHOLE_RECORD_IN_FINISHED_TIME_RANGE = 2;
    //根据intent来判断做什么查询，参数为之后的常数
    private int intent;
    private List<String> projectRecordNames;
    private Date begScheduledTime;
    private Date endScheduledTime;
    private Date begFinishedTime;
    private Date endFinishedTime;

    public List<String> getProjectRecordNames() {
        return projectRecordNames;
    }

    public QueryInfo setProjectRecordNames(List<String> projectRecordNames) {
        this.projectRecordNames = projectRecordNames;
        return this;
    }

    public Date getBegScheduledTime() {
        return begScheduledTime;
    }

    public QueryInfo setBegScheduledTime(Date begScheduledTime) {
        this.begScheduledTime = begScheduledTime;
        return this;
    }

    public Date getEndScheduledTime() {
        return endScheduledTime;
    }

    public QueryInfo setEndScheduledTime(Date endScheduledTime) {
        this.endScheduledTime = endScheduledTime;
        return this;
    }

    public Date getBegFinishedTime() {
        return begFinishedTime;
    }

    public QueryInfo setBegFinishedTime(Date begFinishedTime) {
        this.begFinishedTime = begFinishedTime;
        return this;
    }

    public Date getEndFinishedTime() {
        return endFinishedTime;
    }

    public QueryInfo setEndFinishedTime(Date endFinishedTime) {
        this.endFinishedTime = endFinishedTime;
        return this;
    }

    public int getIntent() {
        return intent;
    }

    public QueryInfo setIntent(int intent) {
        this.intent = intent;
        return this;
    }
}
