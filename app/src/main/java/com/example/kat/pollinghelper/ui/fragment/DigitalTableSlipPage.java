package com.example.kat.pollinghelper.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.kat.pollinghelper.R;

public class DigitalTableSlipPage extends Fragment {

    public DigitalTableSlipPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().setTitle(R.string.digital_table);
        return inflater.inflate(R.layout.fragment_digital_table_slip_page, container, false);
    }

    @Override
    public String toString() {
        return getString(R.string.digital_table);
    }
}
