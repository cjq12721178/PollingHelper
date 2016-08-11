package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.structure.ChartInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/8/2.
 */
public class LineChartAdapter extends SensorValueAdapter {

    private class ViewHolder {
        private LineChart lineChart;
        private BarChart barChart;
        private long timeStamp;
    }

    public LineChartAdapter(Context context) {
        super(context);
    }

    public LineChartAdapter(Context context, List<SensorValue> sensorList) {
        super(context, sensorList);
    }

    @Override
    protected void onInit() {
        chartInfoList = new ArrayList<>();
    }

    @Override
    public int getViewTypeCount() {
        return SensorDataType.Pattern.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getDataType().getPattern().ordinal();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //初始化图表
        final ViewHolder viewHolder;
        final SensorValue sensorValue = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (sensorValue.getDataType().getPattern()) {
                case DT_ANALOG: {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_linechart, null);
                    viewHolder.lineChart = (LineChart)convertView.findViewById(R.id.lc_data_view);
                    XAxis xAxis = viewHolder.lineChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelCount(MAX_SHOW_SIZE);
                    xAxis.setDrawGridLines(false);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinValue(0);
                    xAxis.setAxisMaxValue(MAX_SHOW_SIZE);
                    YAxis leftAxis = viewHolder.lineChart.getAxisLeft();
                    leftAxis.setSpaceBottom(0);
                    YAxis rightAxis = viewHolder.lineChart.getAxisRight();
                    rightAxis.setSpaceBottom(0);
                } break;
                case DT_STATUS: {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_barchart, null);
                    viewHolder.barChart = (BarChart)convertView.findViewById(R.id.bc_data_view);
                    XAxis xAxis = viewHolder.barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setLabelCount(MAX_SHOW_SIZE);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinValue(0);
                    xAxis.setAxisMaxValue(MAX_SHOW_SIZE);
                    YAxis leftAxis = viewHolder.barChart.getAxisLeft();
                    leftAxis.setAxisMinValue(0);
                    leftAxis.setAxisMaxValue(2);
                    leftAxis.setLabelCount(1);
                    leftAxis.setDrawGridLines(false);
                    YAxis rightAxis = viewHolder.barChart.getAxisRight();
                    rightAxis.setAxisMinValue(0);
                    rightAxis.setAxisMaxValue(2);
                    rightAxis.setLabelCount(1);
                    rightAxis.setDrawGridLines(false);
                } break;
                case DT_COUNT: {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_barchart, null);
                    viewHolder.barChart = (BarChart)convertView.findViewById(R.id.bc_data_view);
                    XAxis xAxis = viewHolder.barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setLabelCount(MAX_SHOW_SIZE);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinValue(0);
                    xAxis.setAxisMaxValue(MAX_SHOW_SIZE);
                    YAxis leftAxis = viewHolder.barChart.getAxisLeft();
                    leftAxis.setAxisMinValue(0);
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setDrawAxisLine(false);
                    YAxis rightAxis = viewHolder.barChart.getAxisRight();
                    rightAxis.setAxisMinValue(0);
                    rightAxis.setDrawGridLines(false);
                    rightAxis.setDrawAxisLine(false);
                } break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //设置数据
        //初始化数据
        //Log.d("PollingHelper", "pre init data");
        if (chartInfoList.size() == position) {
            chartInfoList.add(ChartInfo.from(sensorValue));
        }
        //更新数据
        //Log.d("PollingHelper", "pre update data");
        ChartInfo chartInfo = chartInfoList.get(position);
        long timeStamp = sensorValue.getLatestTimestamp();
        if (viewHolder.timeStamp != timeStamp) {
            viewHolder.timeStamp = timeStamp;
            switch (sensorValue.getDataType().getPattern()) {
                case DT_ANALOG: {
                    chartInfo.updateData(Entry.class, sensorValue);
                    viewHolder.lineChart.getXAxis().setValueFormatter(chartInfo.getXFormatter());
                    viewHolder.lineChart.setData((LineData) chartInfo.getData());
                    viewHolder.lineChart.animateX(750);
                } break;
                case DT_STATUS: {
                    chartInfo.updateData(BarEntry.class, sensorValue);
                    viewHolder.barChart.getXAxis().setValueFormatter(chartInfo.getXFormatter());
                    viewHolder.barChart.getAxisLeft().setValueFormatter(chartInfo.getYFormatter());
                    viewHolder.barChart.getAxisRight().setValueFormatter(chartInfo.getYFormatter());
                    viewHolder.barChart.setData((BarData) chartInfo.getData());
                    viewHolder.barChart.animateY(750);
                } break;
                case DT_COUNT: {
                    chartInfo.updateData(BarEntry.class, sensorValue);
                    viewHolder.barChart.getXAxis().setValueFormatter(chartInfo.getXFormatter());
                    viewHolder.barChart.setData((BarData) chartInfo.getData());
                    viewHolder.barChart.animateY(750);
                } break;
            }
        }

        //Log.d("PollingHelper", "post update data");
        return convertView;
    }

    private static final int MAX_SHOW_SIZE = 10;
    private List<ChartInfo> chartInfoList;
}
