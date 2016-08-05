package com.example.kat.pollinghelper.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kat.pollinghelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalogPanelSlipPage extends DataViewFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slip_page_analog_panel, container, false);
    }
}
