package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;

import java.text.SimpleDateFormat;

/**
 * Created by KAT on 2016/7/5.
 */
public class RealTimePollingItem extends FunctionListItem {

    private class ViewHolder {
        private TextView projectName;
        private TextView scheduleTime;
        private TextView finishedTime;
        private TextView finishState;
        private LinearLayout wholeItem;
    }

    public RealTimePollingItem(PollingProjectRecord projectRecord) {
        super(FunctionType.FT_REAL_TIME_POLLING);
        this.projectRecord = projectRecord;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_real_time_polling, null);
            viewHolder = new ViewHolder();
            viewHolder.projectName = (TextView)convertView.findViewById(R.id.tv_real_time_polling_project_name);
            viewHolder.scheduleTime = (TextView)convertView.findViewById(R.id.tv_real_time_polling_schedule_time);
            viewHolder.finishedTime = (TextView)convertView.findViewById(R.id.tv_real_time_polling_finished_time);
            viewHolder.finishState = (TextView)convertView.findViewById(R.id.tv_real_time_polling_finished_state);
            viewHolder.wholeItem = (LinearLayout)convertView.findViewById(R.id.li_real_time_polling_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.projectName.setText(projectRecord.getProjectConfig().getName());
        viewHolder.scheduleTime.setText(SCHEDULE_TIME_PREFIX + dateFormat.format(projectRecord.getScheduledTime()));
        viewHolder.finishedTime.setText(FINISH_TIME_PREFIX + (projectRecord.getFinishedTime() != null ? dateFormat.format(projectRecord.getFinishedTime()) : ""));
        viewHolder.finishState.setText(FINISH_STATE_PREFIX + projectRecord.getPollingState().toString());
        viewHolder.wholeItem.setBackgroundColor(getProperColor());
        return convertView;
    }

    private int getProperColor() {
        if (projectRecord.getPollingState() == PollingState.PS_COMPLETED)
            return COLOR_RECORD_COMPLETED;

        if (projectRecord.getFinishedTime() == null)
            return COLOR_RECORD_NEW;

        return COLOR_RECORD_UNDONE;
    }

    public PollingProjectRecord getProjectRecord() {
        return projectRecord;
    }

    public static void initContentPrefix(Context context) {
        SCHEDULE_TIME_PREFIX = context.getString(R.string.ui_tv_schedule_time_prefix);
        FINISH_TIME_PREFIX = context.getString(R.string.ui_tv_finish_time_prefix);
        FINISH_STATE_PREFIX = context.getString(R.string.ui_tv_finish_state_prefix);
        COLOR_RECORD_NEW = context.getResources().getColor(R.color.transparent_green);
        COLOR_RECORD_COMPLETED = context.getResources().getColor(R.color.transparent_blue);
        COLOR_RECORD_UNDONE = context.getResources().getColor(R.color.transparent_red);
    }

    public void setProjectRecord(PollingProjectRecord projectRecord) {
        this.projectRecord = projectRecord;
    }

    private static String SCHEDULE_TIME_PREFIX;
    private static String FINISH_TIME_PREFIX;
    private static String FINISH_STATE_PREFIX;
    private static int COLOR_RECORD_NEW;
    private static int COLOR_RECORD_COMPLETED;
    private static int COLOR_RECORD_UNDONE;
    private SimpleDateFormat dateFormat;
    private PollingProjectRecord projectRecord;
}
