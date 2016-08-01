package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.utility.SimpleFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by KAT on 2016/7/28.
 */
public class DigitalTableAdapter extends BaseAdapter {

    private class ViewHolder {
        private TextView address;
        private TextView type;
        private TextView value;
        private TextView time;
        private long timeStamp;
    }

    public DigitalTableAdapter(Context context) {
        this.context = context;
    }

    public DigitalTableAdapter(Context context, List<SensorValue> sensorList) {
        this(context);
        setDataSource(sensorList);
    }

    public void setDataSource(List<SensorValue> sensorList) {
        sensors = sensorList;
    }

    public boolean isDataSourceEmpty() {
        return sensors == null;
    }

    @Override
    public int getCount() {
        return sensors != null ? sensors.size() : 0;
    }

    @Override
    public SensorValue getItem(int position) {
        return sensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_data_view_digital_table, null);
            viewHolder = new ViewHolder();
            viewHolder.address = (TextView)convertView.findViewById(R.id.tv_data_view_digital_address);
            viewHolder.type = (TextView)convertView.findViewById(R.id.tv_data_view_digital_type);
            viewHolder.value = (TextView)convertView.findViewById(R.id.tv_data_view_digital_value);
            viewHolder.time = (TextView)convertView.findViewById(R.id.tv_data_view_digital_time);
            viewHolder.timeStamp = 0;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        SensorValue sensorValue = getItem(position);
        long timeStamp = sensorValue.getLatestTimestamp();
        if (viewHolder.timeStamp != timeStamp) {
            viewHolder.timeStamp = timeStamp;
            viewHolder.address.setText(sensorValue.getAddress());
            viewHolder.type.setText(sensorValue.getDataType().getName());
            viewHolder.value.setText(sensorValue.getLatestSignificantValueWithUnit());
            viewHolder.time.setText(SimpleFormatter.formatHourMinuteSecond(timeStamp));
        }
        return convertView;
    }

    private Context context;
    private List<SensorValue> sensors;
}
