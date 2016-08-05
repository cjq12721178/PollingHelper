package com.example.kat.pollinghelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.adapter.LineChartAdapter;

/**
 * Created by KAT on 2016/8/2.
 */
public class LineChartSlipPage extends DataViewFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slip_page_line_chart, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lv_data_view_chart);
        lineChartAdapter = new LineChartAdapter(getContext(), getSensorList());
        listView.setAdapter(lineChartAdapter);
        return view;
    }

    @Override
    public void updateDataView() {
        if (lineChartAdapter == null)
            return;

        if (lineChartAdapter.isDataSourceEmpty()) {
            lineChartAdapter.setDataSource(getSensorList());
        }
        lineChartAdapter.notifyDataSetChanged();
    }

    private LineChartAdapter lineChartAdapter;
}
