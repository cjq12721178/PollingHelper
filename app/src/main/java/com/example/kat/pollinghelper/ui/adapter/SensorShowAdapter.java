package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutSensorConfig;

import java.util.List;

/**
 * Created by KAT on 2016/5/27.
 */
public class SensorShowAdapter extends BaseAdapter {
    private class ViewHolder {
        TextView name;
        TextView address;
    }

    public SensorShowAdapter(Context context, List<ScoutSensorConfig> sensorConfigs) {
        this.sensorConfigs = sensorConfigs;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sensorConfigs.size();
    }

    @Override
    public ScoutSensorConfig getItem(int position) {
        return sensorConfigs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_sensor_show, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_sensor_show_name);
            viewHolder.address = (TextView)convertView.findViewById(R.id.tv_sensor_show_address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ScoutSensorConfig sensorConfig = getItem(position);
        viewHolder.name.setText(sensorConfig.getName());
        viewHolder.address.setText(sensorConfig.getAddress());
        setItemTextBold(position, viewHolder);
        if (position == 0)
            Log.e("PollingHelper", viewHolder.name.getPaint().isFakeBoldText() ? "true" : "false");
        return convertView;
    }

    public void setItemTextBold(int position, ViewHolder viewHolder) {
        if (position == lastSelectedIndex){
            viewHolder.name.getPaint().setFakeBoldText(false);
            viewHolder.address.getPaint().setFakeBoldText(false);
        }
        if (position == currentSelectedIndex) {
            viewHolder.name.getPaint().setFakeBoldText(true);
            viewHolder.address.getPaint().setFakeBoldText(true);
        }
    }

    public void selectItem(String sensorName) {
        currentSelectedIndex = 0;
        for (ScoutSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorName.equals(sensorConfig.getName())) {
                lastSelectedIndex = currentSelectedIndex;
                currentSelectedIndex = sensorConfigs.indexOf(sensorConfig);
            }
        }
    }

    private int lastSelectedIndex;
    private int currentSelectedIndex;
    private List<ScoutSensorConfig> sensorConfigs;
    private LayoutInflater layoutInflater;
}
