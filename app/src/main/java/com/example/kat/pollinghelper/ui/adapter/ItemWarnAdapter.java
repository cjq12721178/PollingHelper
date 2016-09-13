package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.warn.ItemWarnInfo;
import com.example.kat.pollinghelper.bean.warn.MissionWarnInfo;
import com.example.kat.pollinghelper.utility.Converter;
import com.example.kat.pollinghelper.utility.SimpleFormatter;

/**
 * Created by KAT on 2016/9/12.
 */
public class ItemWarnAdapter extends BaseAdapter {

    private static class ViewHolder {
        public TextView name;
        public TextView time;
        public TextView value;
        public TextView alarm;
    }

    public ItemWarnAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return missionWarnInfo != null ? missionWarnInfo.size() : 0;
    }

    @Override
    public ItemWarnInfo getItem(int position) {
        return missionWarnInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_item_warn, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_warn_item_name);
            viewHolder.time = (TextView)convertView.findViewById(R.id.tv_warn_item_time);
            viewHolder.value = (TextView)convertView.findViewById(R.id.tv_warn_item_value);
            viewHolder.alarm = (TextView)convertView.findViewById(R.id.tv_warn_item_alarm);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ItemWarnInfo itemWarnInfo = missionWarnInfo.get(position);
        viewHolder.name.setText(itemWarnInfo.getMeasureName());
        viewHolder.time.setText(SimpleFormatter.formatHourMinuteSecond(itemWarnInfo.getWarnTime()));
        viewHolder.value.setText(itemWarnInfo.getCurrentValueWithUnit());
        viewHolder.alarm.setText(itemWarnInfo.getAlarm());
        return convertView;
    }

    public void setMissionWarnInfo(MissionWarnInfo missionWarnInfo) {
        this.missionWarnInfo = missionWarnInfo;
    }

    private final Context context;
    private MissionWarnInfo missionWarnInfo;
}
