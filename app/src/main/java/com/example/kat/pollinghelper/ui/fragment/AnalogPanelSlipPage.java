package com.example.kat.pollinghelper.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.activity.DataViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalogPanelSlipPage extends DataViewFragment {

    @Override
    public String getLabel() {
        return "模拟表盘";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analog_panel_slip_page, container, false);
    }
}
