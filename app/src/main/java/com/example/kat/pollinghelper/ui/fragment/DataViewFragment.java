package com.example.kat.pollinghelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.kat.pollinghelper.data.SensorValue;

import java.util.Collection;
import java.util.List;

/**
 * Created by KAT on 2016/7/27.
 */
public class DataViewFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    public void onBindDataSource(List<SensorValue> sensorList) {
        this.sensorList = sensorList;
    }

    public String getLabel() {
        return label;
    }

    public DataViewFragment setLabel(String label) {
        this.label = label;
        return this;
    }

    public void updateDataView() {

    }

    protected List<SensorValue> getSensorList() {
        return sensorList;
    }

    private List<SensorValue> sensorList;
    private String label;
}
