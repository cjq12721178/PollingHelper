package com.example.kat.pollinghelper.structure.view;

import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.protocol.SensorBleInfo;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorUdpInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by KAT on 2016/8/17.
 */
public class SensorValueProcessor {

    public interface SensorComparator extends Comparator<SensorValue> {

    }

    private class SensorAddressAscend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return lhs.getAddress().compareTo(rhs.getAddress());
        }
    }

    private class SensorAddressDescend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return rhs.getAddress().compareTo(lhs.getAddress());
        }
    }

    private class SensorTypeAscend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return lhs.getDataType().getValue() - rhs.getDataType().getValue();
        }
    }

    private class SensorTypeDescend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return rhs.getDataType().getValue() - lhs.getDataType().getValue();
        }
    }

    private class SensorTimeAscend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return (int)(lhs.getCreateTime() - rhs.getCreateTime());
        }
    }

    private class SensorTimeDescend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return (int)(rhs.getCreateTime() - lhs.getCreateTime());
        }
    }

    private class SensorUnitAscend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return lhs.getDataType().getUnit().compareTo(rhs.getDataType().getUnit());
        }
    }

    private class SensorUnitDescend implements SensorComparator {

        @Override
        public int compare(SensorValue lhs, SensorValue rhs) {
            return rhs.getDataType().getUnit().compareTo(lhs.getDataType().getUnit());
        }
    }

    public enum SortEntry {
        SE_ADDRESS,
        SE_TYPE,
        SE_TIME,
        SE_UNIT
    }

    private interface Condition {
        boolean match(SensorValue sensor);
    }

    private class FromWifi implements Condition {

        @Override
        public boolean match(SensorValue sensor) {
            return sensor.getAddress().length() == SensorUdpInfo.ADDRESS_LEN;
        }
    }

    private class FromBle implements Condition {

        @Override
        public boolean match(SensorValue sensor) {
            return sensor.getAddress().length() == SensorBleInfo.ADDRESS_LEN;
        }
    }

    private class IsAnalog implements Condition {

        @Override
        public boolean match(SensorValue sensor) {
            return sensor.getDataType().getPattern() == SensorDataType.Pattern.DT_ANALOG;
        }
    }

    private class IsStatus implements Condition {

        @Override
        public boolean match(SensorValue sensor) {
            return sensor.getDataType().getPattern() == SensorDataType.Pattern.DT_STATUS;
        }
    }

    private class IsCount implements Condition {

        @Override
        public boolean match(SensorValue sensor) {
            return sensor.getDataType().getPattern() == SensorDataType.Pattern.DT_COUNT;
        }
    }

    private abstract class Filter {

        public Filter(int capacity) {
            conditions = new LinkedList<>();
            this.capacity = capacity <= 0 || capacity > MAX_CONDITION_CAPACITY ? 0 : capacity;
        }

        //使用位与算法，最多支持31个条件
        public void setConditions(int conditionValue) {
            conditions.clear();
            if (conditionValue <= 0 || ((conditionValue + 1) >> capacity) > 0) {
                conditionValue = FILTER_ENTITY_ALL;
            }
            onSetCondition(conditionValue);
        }

        protected abstract void onSetCondition(int conditionValue);

        //注意，只要有一个条件达成，即返回true
        public boolean filtrate(SensorValue sensor) {
            if (conditions.size() == 0)
                return true;
            for (Condition condition :
                    conditions) {
                if (condition.match(sensor))
                    return true;
            }
            return false;
        }

        protected void addCondition(Condition condition) {
            if (condition == null || conditions.size() == capacity)
                return;
            conditions.add(condition);
        }

        protected boolean removeCondition(Condition condition) {
            if (condition == null)
                return false;
            return this.conditions.remove(condition);
        }

        public static final int FILTER_ENTITY_ALL = 0x80000000;
        private static final int MAX_CONDITION_CAPACITY = 31;
        private LinkedList<Condition> conditions;
        private int capacity;
    }

    public class FromFilter extends Filter {

        public FromFilter() {
            super(2);
        }

        @Override
        protected void onSetCondition(int conditionValue) {

            if ((conditionValue & FILTER_ENTITY_WIFI) != 0) {
                addCondition(new FromWifi());
            }

            if ((conditionValue & FILTER_ENTITY_BLE) != 0) {
                addCondition(new FromBle());
            }
        }

        public static final int FILTER_ENTITY_WIFI = 0x00000001;
        public static final int FILTER_ENTITY_BLE = 0x00000002;
    }

    public class PatternFilter extends Filter {

        public PatternFilter() {
            super(3);
        }

        @Override
        protected void onSetCondition(int conditionValue) {
            if ((conditionValue & FILTER_ENTITY_ANALOG) != 0) {
                addCondition(new IsAnalog());
            }

            if ((conditionValue & FILTER_ENTITY_STATUS) != 0) {
                addCondition(new IsStatus());
            }

            if ((conditionValue & FILTER_ENTITY_COUNT) != 0) {
                addCondition(new IsCount());
            }
        }

        public static final int FILTER_ENTITY_ANALOG = 0x00000001;
        public static final int FILTER_ENTITY_STATUS = 0x00000002;
        public static final int FILTER_ENTITY_COUNT = 0x00000004;
    }

    public SensorValueProcessor() {
        fromFilter = new FromFilter();
        patternFilter = new PatternFilter();
        tmpSensors = new ArrayList<>();
        postProceedSensors = new ArrayList<>();
        filteredSensors = new ArrayList<>();
    }

    public List<SensorValue> getFinalSensors() {
        return postProceedSensors;
    }

    public DataStorage.OnDataListener getOnDataListener() {
        return onDataListener;
    }

    //true表示有新的传感器加入
    public boolean updateSensor() {
        if (tmpSensors.isEmpty())
            return false;

        postProceedSensors.addAll(tmpSensors);
        tmpSensors.clear();
        sort();
        return true;
    }

    public void setSensorComparator(boolean isAscend, SortEntry entry) {
        switch (entry) {
            case SE_ADDRESS: {
                sensorComparator = isAscend ? new SensorAddressAscend() :
                        new SensorAddressDescend();
            } break;
            case SE_TIME: {
                sensorComparator = isAscend ? new SensorTimeAscend() :
                        new SensorTimeDescend();
            } break;
            case SE_UNIT: {
                sensorComparator = isAscend ? new SensorUnitAscend() :
                        new SensorUnitDescend();
            } break;
            case SE_TYPE:
            default: {
                sensorComparator = isAscend ? new SensorTypeAscend() :
                        new SensorTypeDescend();
            } break;
        }
    }

    public void sort() {
        if (sensorComparator != null) {
            Collections.sort(postProceedSensors, sensorComparator);
        }
    }

    private void filtrateSensor(Collection<SensorValue> sensorCollection,
                                List<SensorValue> targetContainer) {
        for (SensorValue sensor :
                sensorCollection) {
            filtrateSensor(sensor, targetContainer);
        }
    }

    private void filtrateSensor(SensorValue sensor, List<SensorValue> targetContainer) {
        if (isMatchFilter(sensor) && isMatchSearch(sensor)) {
            targetContainer.add(sensor);
        } else {
            filteredSensors.add(sensor);
        }
    }

    private boolean isMatchSearch(SensorValue sensor) {
        if (searchContents == null)
            return true;

        for (String searchText :
                searchContents) {
            if (searchText.length() == 0)
                continue;
            if (!sensor.getAddress().contains(searchText) &&
                    !sensor.getMeasureName().contains(searchText))
                return false;
        }

        return true;
    }

    private boolean isMatchFilter(SensorValue sensor) {
        return fromFilter.filtrate(sensor) && patternFilter.filtrate(sensor);
    }

    public void setFilterCondition(int fromConditionValue, int patternConditionValue) {
        fromFilter.setConditions(fromConditionValue);
        patternFilter.setConditions(patternConditionValue);
        resetSensors();
    }

    public void setSearchContents(String[] searchContents) {
        this.searchContents = searchContents;
        resetSensors();
    }

    private void resetSensors() {
        tmpSensors.addAll(postProceedSensors);
        tmpSensors.addAll(filteredSensors);
        postProceedSensors.clear();
        filteredSensors.clear();
        filtrateSensor(tmpSensors, postProceedSensors);
        tmpSensors.clear();
    }

    private DataStorage.OnDataListener onDataListener = new DataStorage.OnDataListener() {
        @Override
        public void onInit(Collection<SensorValue> sensorCollection) {
            tmpSensors.clear();
            postProceedSensors.clear();
            filteredSensors.clear();
            filtrateSensor(sensorCollection, postProceedSensors);
        }

        @Override
        public void onUpdate(SensorValue newSensor) {
            filtrateSensor(newSensor, tmpSensors);
        }
    };

    private String[] searchContents;
    private FromFilter fromFilter;
    private PatternFilter patternFilter;
    private SensorComparator sensorComparator;
    private List<SensorValue> tmpSensors;
    private List<SensorValue> postProceedSensors;
    private List<SensorValue> filteredSensors;
}
