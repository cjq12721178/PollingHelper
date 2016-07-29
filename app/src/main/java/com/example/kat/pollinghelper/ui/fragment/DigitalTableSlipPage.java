package com.example.kat.pollinghelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.ui.adapter.DigitalTableAdapter;

import java.util.Collection;

public class DigitalTableSlipPage extends DataViewFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_digital_table_slip_page, container, false);
        listView = (ListView) view.findViewById(R.id.lv_data_view_digital);
        digitalTableAdapter = new DigitalTableAdapter(getContext(), getSensorList());
        listView.setAdapter(digitalTableAdapter);
        return view;
    }

    @Override
    public void updateDataView() {
        if (digitalTableAdapter.getCount() == 0) {
            digitalTableAdapter.setDataSource(getSensorList());
        }
        digitalTableAdapter.notifyDataSetChanged();
    }

    private ListView listView;
    private DigitalTableAdapter digitalTableAdapter;
}
