package com.example.kat.pollinghelper.structure;

import android.util.Log;

import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.utility.SimpleFormatter;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/8/4.
 */
public class ChartInfo {

    private static class ChartValueReceiver<E extends Entry> implements SensorValue.ValueReceiver<List<E>> {

        public ChartValueReceiver init(Class<E> c, SensorDataType.Pattern pattern) {
            this.c = c;
            extraY = pattern == SensorDataType.Pattern.DT_STATUS ? 1 : 0;
            x = -0.5f;
            return this;
        }

        @Override
        public List<E> start(int size) {
            return size > 0 ? new ArrayList<E>(size) : null;
        }

        @Override
        public void receive(long timeStamp, double value, List<E> receiver) {
            try {
                E e = c.newInstance();
                e.setX(++x);
                e.setY((float) value + extraY);
                e.setData(SimpleFormatter.formatHourMinuteSecond(timeStamp));
                receiver.add(e);
            } catch (Exception ignored) {
            }
        }

        private float extraY;
        private Class<E> c;
        private float x;
    };

    static {
        chartValueReceiver = new ChartValueReceiver();
        //barChartValueReceiver = new ChartValueReceiver();
    }

    public static ChartInfo from(SensorValue sensor) {
        if (sensor == null)
            return null;

        ChartInfo chartInfo = new ChartInfo();
        //Log.d("PollingHelper", "pre set data");
        chartInfo.setData(sensor);
        //Log.d("PollingHelper", "pre generateYFormatter");
        chartInfo.yFormatter = chartInfo.generateYFormatter(sensor);
        //Log.d("PollingHelper", "post generateYFormatter");
        return chartInfo;
    }

    private void setData(SensorValue sensor) {
        if (sensor.getDataType().getPattern() == SensorDataType.Pattern.DT_ANALOG) {
            LineDataSet sensorValueSet = new LineDataSet(getValues(Entry.class, sensor), getSensorLabel(sensor));
            sensorValueSet.setDrawCircleHole(false);
            sensorValueSet.setLineWidth(2);
            sensorValueSet.setCircleRadius(4);
            List<ILineDataSet> sensorValueSets = new ArrayList<>();
            sensorValueSets.add(sensorValueSet);
            data = new LineData(sensorValueSets);
        } else {
            //new ArrayList<BarEntry>() lib 本身的问题，有时间再弄
            BarDataSet sensorValueSet = new BarDataSet(new ArrayList<BarEntry>(), getSensorLabel(sensor));
            sensorValueSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            if (sensor.getDataType().getPattern() == SensorDataType.Pattern.DT_STATUS) {
                sensorValueSet.setDrawValues(false);
            }
            List<IBarDataSet> sensorValueSets = new ArrayList<>();
            sensorValueSets.add(sensorValueSet);
            BarData barData = new BarData(sensorValueSets);
            barData.setBarWidth(0.5f);
            data = barData;
        }
    }

    private <E extends Entry> List<E> getValues(Class<E> c, SensorValue sensor) {
        return sensor.getValues(getChartValueReceiver(c, sensor.getDataType().getPattern()));
    }

    private String getSensorLabel(SensorValue sensor) {
        StringBuilder builder = new StringBuilder();
        builder.append(sensor.getDataType().getName());
        if (sensor.getDataType().getUnit().length() > 0) {
            builder.append("(");
            builder.append(sensor.getDataType().getUnit());
            builder.append(")");
        }
        builder.append(" - ");
        builder.append(sensor.getAddress());
        return builder.toString();
    }

    private <E extends Entry> void generateXFormatter(final List<E> values) {
        xFormatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < 0 || value >= values.size())
                    return "";
                return (String)values.get((int)value).getData();
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };
    }

    private AxisValueFormatter generateYFormatter(final SensorValue sensor) {
        if (sensor.getDataType().getPattern() != SensorDataType.Pattern.DT_STATUS) {
            return null;
        } else {
            return new AxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value == 2)
                        return sensor.getDataType().getLabelOn();
                    if (value == 1)
                        return sensor.getDataType().getLabelOff();
                    return "";
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            };
        }
    }

    public  <E extends Entry> void updateData(Class<E> c, SensorValue sensor) {
        DataSet<E> dataSet = (DataSet<E>) data.getDataSetByIndex(0);
        List realValues = getValues(c, sensor);
        generateXFormatter(realValues);
        dataSet.setValues(realValues);
    }

    public ChartData getData() {
        return data;
    }

    public AxisValueFormatter getXFormatter() {
        return xFormatter;
    }

    public AxisValueFormatter getYFormatter() {
        return yFormatter;
    }

    private <E extends Entry> ChartValueReceiver<E> getChartValueReceiver(Class<E> c, SensorDataType.Pattern pattern) {
        return chartValueReceiver.init(c, pattern);
    }

    private static ChartValueReceiver chartValueReceiver;
    //private static ChartValueReceiver barChartValueReceiver;
    private ChartData data;
    private AxisValueFormatter xFormatter;
    private AxisValueFormatter yFormatter;
}
