package com.example.kat.pollinghelper.fuction.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.utility.SimpleFormatter;
import com.example.kat.pollinghelper.utility.TreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KAT on 2016/5/9.
 */
public class PollingProjectRecord implements TreeNode {

    private class ViewHolder {
        private TextView name;
        private TextView scheduleTime;
        private TextView finishTime;
        private TextView result;
        private TextView evaluation;
        private TextView remark;
    }

    public PollingProjectRecord(long id, PollingProjectConfig projectConfig) {
        this.id = id;
        this.projectConfig = projectConfig;
        state = PollingState.PS_UNDONE;
        evaluationType = EvaluationType.ET_NORMAL;
        missionRecords = new ArrayList<>();
        changed = false;
        recordResult = "";
    }

    public long getId() {
        return id;
    }

    public PollingProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public PollingState getPollingState() {
        return state;
    }

    public String getRecordResult() {
        return recordResult;
    }

    public ArrayList<PollingMissionRecord> getMissionRecords() {
        return missionRecords;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        if (this.evaluationType != evaluationType) {
            this.evaluationType = evaluationType;
            changed = true;
        }
    }

    public void setRecordResult(String recordResult) {
        if (!this.recordResult.equals(recordResult)) {
            this.recordResult = recordResult;
            changed = true;
        }
    }

    public void setPollingState(PollingState state) {
        if (this.state != state) {
            this.state = state;
            changed = true;
        }
    }

    public void setFinishedTime() {
        if (changed) {
            finishedTime = new Date();
            changed = false;
        }
    }
    public void setFinishedTime(Date time) {
            finishedTime = time;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_record_project, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_record_project_name);
            viewHolder.scheduleTime = (TextView)convertView.findViewById(R.id.tv_record_project_schedule_time);
            viewHolder.finishTime = (TextView)convertView.findViewById(R.id.tv_record_project_finish_time);
            viewHolder.result = (TextView)convertView.findViewById(R.id.tv_record_project_result);
            viewHolder.evaluation = (TextView)convertView.findViewById(R.id.tv_record_project_evaluation);
            viewHolder.remark = (TextView)convertView.findViewById(R.id.tv_record_project_remark);
            int color = context.getResources().getColor(R.color.background_record_project);
            viewHolder.name.setBackgroundColor(color);
            viewHolder.scheduleTime.setBackgroundColor(color);
            viewHolder.finishTime.setBackgroundColor(color);
            viewHolder.result.setBackgroundColor(color);
            viewHolder.evaluation.setBackgroundColor(color);
            viewHolder.remark.setBackgroundColor(color);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(projectConfig.getName());
        viewHolder.scheduleTime.setText(SimpleFormatter.format(scheduledTime));
        viewHolder.finishTime.setText(SimpleFormatter.format(finishedTime));
        viewHolder.result.setText(state.toString());
        viewHolder.evaluation.setText(evaluationType.toString());
        viewHolder.remark.setText(recordResult);
        return convertView;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public List<PollingMissionRecord> getChildren() {
        return missionRecords;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    private boolean expanded;
    private boolean changed;
    private EvaluationType evaluationType;
    private final long id;
    private final PollingProjectConfig projectConfig;
    private Date scheduledTime;
    private Date finishedTime;
    private PollingState state;
    private String recordResult;
    private ArrayList<PollingMissionRecord> missionRecords;
}
