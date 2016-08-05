package com.example.kat.pollinghelper.data;

import android.util.Log;

import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorInfo;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by KAT on 2016/6/22.
 */
public class SensorValue {

    //用于外部模块获取传感器数据
    public interface ValueReceiver<T> {
        //初始化数据接受者，并将其返回给SensorValue，size为数据总数
        T start(int size);
        //前二者为传感器关键数据拷贝，后者为startReceive返回的数据接受者
        void receive(long timeStamp, double value, T receiver);
    }

    public SensorValue() {
        values = new TreeMap<>();
    }

    public static SensorValue from(SensorInfo sensorInfo) {
        SensorValue result = new SensorValue();
        result.dataType = sensorInfo.getDataType();
        result.address = sensorInfo.getMacAddress();
        return result.addValue(sensorInfo);
    }

    public SensorValue addValue(SensorInfo sensorInfo) {
        return addValue(sensorInfo.getTimestamp(), sensorInfo.getValue());
    }

    private SensorValue addValue(long timestamp, double value) {
        values.put(timestamp, value);
        if (values.size() > MAX_ELEMENT_COUNT) {
            values.remove(values.firstKey());
        }
        return this;
    }

    public double getLatestValue() {
        return values.get(values.lastKey());
    }

    public double getValue(long timestamp) {
        Double value = values.get(timestamp);
        return value != null ? value : 0;
    }

    public String getSignificantValue(long timestamp) {
        return dataType.getSignificantValue(getValue(timestamp));
    }

    public String getSignificantValueWithUnit(long timestamp) {
        return dataType.getSignificantValueWithUnit(getValue(timestamp));
    }

    public String getLatestSignificantValue() {
        return dataType.getSignificantValue(getLatestValue());
    }

    public String getLatestSignificantValueWithUnit() {
        return dataType.getSignificantValueWithUnit(getLatestValue());
    }

    public long getLatestTimestamp() {
        return values.lastKey();
    }

    public SensorDataType getDataType() {
        return dataType;
    }

    public String getAddress() {
        return address;
    }

    public <T> T getValues(ValueReceiver<T> valueReceiver) {

        if (valueReceiver == null)
            return null;

        T receiver = valueReceiver.start(values.size());
        for (Map.Entry<Long, Double> element :
                values.entrySet()) {
            valueReceiver.receive(element.getKey(), element.getValue(), receiver);
        }

        //Log.d("PollingHelper", "post get value");
        return receiver;
    }

//    public synchronized <E extends Entry> List<E> getChartValues(Class<E> c)
//            throws IllegalAccessException, InstantiationException {
//        List<E> result = new ArrayList<>(values.size());
//        float x = -0.5f;
//        for (Map.Entry<Long, Double> element :
//                values.entrySet()) {
//            E e = c.newInstance();
//            e.setX();
//            result.add();
//        }
//        return result;
//    }
//
//    public synchronized List<Entry> getLineValues() {
//        List<Entry> result = new ArrayList<>(values.size());
//        for (Map.Entry<Long, Double> element :
//                values.entrySet()) {
//            result.add(new Entry(element.getKey(), element.getValue().floatValue()));
//        }
//        return result;
//    }
//
//    public synchronized List<BarEntry> getBarValues() {
//        List<BarEntry> result = new ArrayList<>(values.size());
//        float x = -0.5f;
//        for (Map.Entry<Long, Double> element :
//                values.entrySet()) {
//            result.add(new BarEntry(++x, element.getValue().floatValue() + 1, element.getKey()));
//        }
//        return result;
//    }

    private SensorDataType dataType;
    private String address;
    //用于控制数据规模
    private static final int MAX_ELEMENT_COUNT = 10;
    private TreeMap<Long, Double> values;
}
