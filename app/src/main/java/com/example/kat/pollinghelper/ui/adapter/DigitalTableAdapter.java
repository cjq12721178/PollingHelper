package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.utility.SimpleFormatter;

import java.util.List;

/**
 * Created by KAT on 2016/7/28.
 */
public class DigitalTableAdapter extends SensorValueAdapter {

    private class ViewHolder {
        private TextView address;
        private TextView type;
        private TextView value;
        private TextView time;
        //private long timeStamp;
    }

    public DigitalTableAdapter(Context context) {
        super(context);
    }

    public DigitalTableAdapter(Context context, List<SensorValue> sensorList) {
        super(context, sensorList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_data_view_digital_table, null);
            viewHolder = new ViewHolder();
            viewHolder.address = (TextView)convertView.findViewById(R.id.tv_data_view_digital_address);
            viewHolder.type = (TextView)convertView.findViewById(R.id.tv_data_view_digital_type);
            viewHolder.value = (TextView)convertView.findViewById(R.id.tv_data_view_digital_value);
            viewHolder.time = (TextView)convertView.findViewById(R.id.tv_data_view_digital_time);
            //viewHolder.timeStamp = 0;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        SensorValue sensorValue = getItem(position);
        viewHolder.address.setText(sensorValue.getMacAddress());
        viewHolder.type.setText(sensorValue.getMeasureName());
        viewHolder.value.setText(sensorValue.getLatestSignificantValueWithUnit());
        viewHolder.time.setText(SimpleFormatter.formatHourMinuteSecond(sensorValue.getLatestTimestamp()));
//        long timeStamp = sensorValue.getLatestTimestamp();
//        if (viewHolder.timeStamp != timeStamp) {
//            viewHolder.timeStamp = timeStamp;
//            viewHolder.address.setText(sensorValue.getMacAddress());
//            viewHolder.type.setText(sensorValue.getMeasureName());
//            viewHolder.value.setText(sensorValue.getLatestSignificantValueWithUnit());
//            viewHolder.time.setText(SimpleFormatter.formatHourMinuteSecond(timeStamp));
//        }
        return convertView;
    }
}
