package com.example.kat.pollinghelper.structure.record;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.utility.SimpleFormatter;
import com.example.kat.pollinghelper.utility.TreeNode;

import java.util.List;

/**
 * Created by KAT on 2016/5/9.
 */
public class ScoutItemRecord implements TreeNode {

    private class ViewHolder {
        private TextView name;
        private TextView device;
        private TextView value;
        private TextView unit;
        private TextView warning;
        private int normalColor;
        private int warningColor;
        private String downAlarm;
        private String upAlarm;
        private String noAlarm;
    }

    public ScoutItemRecord(long id, ScoutItemConfig itemConfig) {
        this.id = id;
        this.itemConfig = itemConfig;
    }

    public long getId() {
        return id;
    }

    public ScoutItemConfig getItemConfig() {
        return itemConfig;
    }

    public double getValue() {
        return value;
    }

    public String getSignificantValue() {
        return itemConfig.getSensor().getType().getSignificantValue(value);
    }

    public boolean isOutOfAlarm() {
        return isOutOfDownAlarm() || isOutOfUpAlarm();
    }

    public boolean isOutOfUpAlarm() {
        return value > itemConfig.getUpAlarm();
    }

    public boolean isOutOfDownAlarm() {
        return value < itemConfig.getDownAlarm();
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_record_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_record_item_name);
            viewHolder.device = (TextView)convertView.findViewById(R.id.tv_record_item_device);
            viewHolder.value = (TextView)convertView.findViewById(R.id.tv_record_item_value);
            viewHolder.unit = (TextView)convertView.findViewById(R.id.tv_record_item_unit);
            viewHolder.warning = (TextView)convertView.findViewById(R.id.tv_record_item_warning);
            Resources resources = context.getResources();
            viewHolder.normalColor = resources.getColor(R.color.background_record_item_normal);
            viewHolder.warningColor = resources.getColor(R.color.background_record_item_warning);
            viewHolder.downAlarm = resources.getString(R.string.ui_tv_record_item_warning_down_alarm);
            viewHolder.upAlarm = resources.getString(R.string.ui_tv_record_item_warning_up_alarm);
            viewHolder.noAlarm = resources.getString(R.string.ui_tv_record_item_warning_no_alarm);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        int color;
        String warningInfo;
        if (isOutOfDownAlarm()) {
            color = viewHolder.warningColor;
            warningInfo = viewHolder.downAlarm;
        } else if (isOutOfUpAlarm()) {
            color = viewHolder.warningColor;
            warningInfo = viewHolder.upAlarm;
        } else {
            color = viewHolder.normalColor;
            warningInfo = viewHolder.noAlarm;
        }
        convertView.setBackgroundColor(color);
        viewHolder.name.setText(itemConfig.getMeasureName());
        viewHolder.device.setText(itemConfig.getSensor().getName());
        viewHolder.value.setText(itemConfig.getSensor().getType().getSignificantValue(value));
        viewHolder.unit.setText(itemConfig.getSensor().getType().getUnit());
        viewHolder.warning.setText(warningInfo);
        return convertView;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void setExpanded(boolean expanded) {

    }

    @Override
    public List getChildren() {
        return null;
    }

    @Override
    public int getViewType() {
        return 2;
    }

    private final long id;
    private final ScoutItemConfig itemConfig;
    private double value;
}
