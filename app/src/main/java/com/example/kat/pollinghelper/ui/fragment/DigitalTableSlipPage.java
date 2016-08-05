package com.example.kat.pollinghelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.adapter.DigitalTableAdapter;

public class DigitalTableSlipPage extends DataViewFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slip_page_digital_table, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lv_data_view_digital);
        digitalTableAdapter = new DigitalTableAdapter(getContext(), getSensorList());
        listView.setAdapter(digitalTableAdapter);
        return view;
    }

    @Override
    public void updateDataView() {
        if (digitalTableAdapter == null)
            return;

        if (digitalTableAdapter.isDataSourceEmpty()) {
            digitalTableAdapter.setDataSource(getSensorList());
        }
        digitalTableAdapter.notifyDataSetChanged();
    }

    private DigitalTableAdapter digitalTableAdapter;
}
