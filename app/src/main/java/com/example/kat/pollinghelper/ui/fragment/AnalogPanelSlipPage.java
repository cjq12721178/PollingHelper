package com.example.kat.pollinghelper.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kat.pollinghelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalogPanelSlipPage extends Fragment {


    public AnalogPanelSlipPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().setTitle(R.string.analog_dialplate);
        return inflater.inflate(R.layout.fragment_analog_panel_slip_page, container, false);
    }

    @Override
    public String toString() {
        return getString(R.string.analog_dialplate);
    }
}
