package com.example.kat.pollinghelper.structure.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.utility.SimpleFormatter;
import com.example.kat.pollinghelper.utility.TreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KAT on 2016/5/9.
 */
public class ScoutMissionRecord implements TreeNode {

    private class ViewHolder {
        private TextView name;
        private TextView finishTime;
        private TextView result;
        private TextView evaluation;
        private TextView remark;
    }

    public ScoutMissionRecord(long id, ScoutMissionConfig missionConfig) {
        this.id = id;
        this.missionConfig = missionConfig;
        state = ScoutRecordState.PS_UNDONE;
        evaluationType = EvaluationType.ET_NORMAL;
        itemRecords = new ArrayList<>();
        changed = false;
        recordResult = "";
    }

    public long getId() {
        return id;
    }

    public String getRecordResult() {
        return recordResult;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public ArrayList<ScoutItemRecord> getItemRecords() {
        return itemRecords;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public ScoutMissionConfig getMissionConfig() {
        return missionConfig;
    }

    public ScoutRecordState getPollingState() {
        return state;
    }

    public void setPollingState(ScoutRecordState state) {
        if (this.state != state) {
            this.state = state;
            changed = true;
        }
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

    public void setFinishedTime() {
        if (changed) {
            finishedTime = new Date();
            changed = false;
        }
    }

    public void setFinishedTime(Date time) {
        finishedTime =time;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_record_mission, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_record_mission_name);
            viewHolder.finishTime = (TextView)convertView.findViewById(R.id.tv_record_mission_finish_time);
            viewHolder.result = (TextView)convertView.findViewById(R.id.tv_record_mission_result);
            viewHolder.evaluation = (TextView)convertView.findViewById(R.id.tv_record_mission_evaluation);
            viewHolder.remark = (TextView)convertView.findViewById(R.id.tv_record_mission_remark);
            int color = context.getResources().getColor(R.color.background_record_mission);
            viewHolder.name.setBackgroundColor(color);
            viewHolder.finishTime.setBackgroundColor(color);
            viewHolder.result.setBackgroundColor(color);
            viewHolder.evaluation.setBackgroundColor(color);
            viewHolder.remark.setBackgroundColor(color);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(missionConfig.getName());
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
    public List<ScoutItemRecord> getChildren() {
        return itemRecords;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    private boolean expanded;
    private boolean changed;
    private ScoutRecordState state;
    private final ScoutMissionConfig missionConfig;
    private Date finishedTime;
    private final long id;
    private String recordResult;
    private EvaluationType evaluationType;
    private ArrayList<ScoutItemRecord> itemRecords;
}
