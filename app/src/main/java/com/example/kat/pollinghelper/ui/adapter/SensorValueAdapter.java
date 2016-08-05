package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.example.kat.pollinghelper.data.SensorValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/8/2.
 */
public abstract class SensorValueAdapter extends BaseAdapter {

    public SensorValueAdapter(Context context) {
        this.context = context;
        onInit();
    }

    public SensorValueAdapter(Context context, List<SensorValue> sensorList) {
        this(context);
        setDataSource(sensorList);
    }

    protected void onInit() {
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

    protected Context getContext() {
        return context;
    }

    private Context context;
    private List<SensorValue> sensors;
    //protected List<Long> timeStamps;
}
