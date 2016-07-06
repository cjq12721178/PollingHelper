package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;

import java.util.List;

/**
 * Created by KAT on 2016/5/27.
 */
public class SensorShowAdapter extends BaseAdapter {
    private class ViewHolder {
        TextView nameLabel;
        TextView addressLabel;
        TextView descriptionLabel;
        TextView nameContent;
        TextView addressContent;
        TextView descriptionContent;
    }

    public SensorShowAdapter(Context context, List<PollingSensorConfig> sensorConfigs) {
        this.sensorConfigs = sensorConfigs;
        layoutInflater = LayoutInflater.from(context);
        currentSelectedIndex = -1;
        lastSelectedIndex = -1;
    }

    @Override
    public int getCount() {
        return sensorConfigs.size();
    }

    @Override
    public PollingSensorConfig getItem(int position) {
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
            viewHolder.nameLabel = (TextView)convertView.findViewById(R.id.tv_sensor_show_name_lable);
            viewHolder.addressLabel = (TextView)convertView.findViewById(R.id.tv_sensor_show_address_lable);
            viewHolder.descriptionLabel = (TextView)convertView.findViewById(R.id.tv_sensor_show_description_lable);
            viewHolder.nameContent = (TextView)convertView.findViewById(R.id.tv_sensor_show_name_content);
            viewHolder.addressContent = (TextView)convertView.findViewById(R.id.tv_sensor_show_address_content);
            viewHolder.descriptionContent = (TextView)convertView.findViewById(R.id.tv_sensor_show_description_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        PollingSensorConfig sensorConfig = getItem(position);
        viewHolder.nameLabel.setText(R.string.ui_tv_sensor_name_label);
        viewHolder.addressLabel.setText(R.string.ui_tv_sensor_address_label);
        viewHolder.descriptionLabel.setText(R.string.ui_tv_sensor_description_label);
        viewHolder.nameContent.setText(sensorConfig.getName());
        viewHolder.addressContent.setText(sensorConfig.getAddress());
        viewHolder.descriptionContent.setText(sensorConfig.getDescription());
        setItemTextBold(position, viewHolder);
        return convertView;
    }

    public void setItemTextBold(int position, ViewHolder viewHolder) {
        if (position == currentSelectedIndex) {
            viewHolder.nameLabel.getPaint().setFakeBoldText(true);
            viewHolder.addressLabel.getPaint().setFakeBoldText(true);
            viewHolder.descriptionLabel.getPaint().setFakeBoldText(true);
            viewHolder.nameContent.getPaint().setFakeBoldText(true);
            viewHolder.addressContent.getPaint().setFakeBoldText(true);
            viewHolder.descriptionContent.getPaint().setFakeBoldText(true);
        }
        if (position == lastSelectedIndex){
            viewHolder.nameLabel.getPaint().setFakeBoldText(false);
            viewHolder.addressLabel.getPaint().setFakeBoldText(false);
            viewHolder.descriptionLabel.getPaint().setFakeBoldText(false);
            viewHolder.nameContent.getPaint().setFakeBoldText(false);
            viewHolder.addressContent.getPaint().setFakeBoldText(false);
            viewHolder.descriptionContent.getPaint().setFakeBoldText(false);
        }
    }

    public void bindAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public void closeDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void selectItem(String sensorName) {
        for (PollingSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorName.equals(sensorConfig.getName())) {
                lastSelectedIndex = currentSelectedIndex;
                currentSelectedIndex = sensorConfigs.indexOf(sensorConfig);
            }
        }
    }

    private int lastSelectedIndex;
    private int currentSelectedIndex;
    private List<PollingSensorConfig> sensorConfigs;
    private AlertDialog alertDialog;
    private LayoutInflater layoutInflater;
}
