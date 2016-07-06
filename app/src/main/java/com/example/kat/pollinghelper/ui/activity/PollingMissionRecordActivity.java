package com.example.kat.pollinghelper.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.record.EvaluationType;
import com.example.kat.pollinghelper.fuction.record.PollingItemRecord;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PollingMissionRecordActivity extends ManagedActivity {
    private PollingMissionRecord missionRecord;
    private RadioGroup rdogrpEvaluation;
    private EditText edttxtEvaluation;
    private PollingItemAdapter itemAdapter;
    private Date originTime;
    private Timer timer;
    private Runnable updateItemTextList = new Runnable() {
        @Override
        public void run() {
            itemAdapter.notifyDataSetChanged();
        }
    };

    private TimerTask updateSensorData = new TimerTask() {
        @Override
        public void run() {
            notifyManager(OperaType.OT_UPDATE_SENSOR_DATA);
        }
    };

    private class PollingItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return missionRecord.getItemRecords().size();
        }

        @Override
        public PollingItemRecord getItem(int position) {
            return missionRecord.getItemRecords().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(PollingMissionRecordActivity.this).inflate(R.layout.listitem_polling_item_record, null);
                convertView.setTag(convertView.findViewById(R.id.tv_polling_item_record));
            }
            TextView content = (TextView)convertView.getTag();
            PollingItemRecord currentItemRecord = getItem(position);
            content.setText(generateItemText(currentItemRecord));
            content.setTextColor(currentItemRecord.isOutOfAlarm() ? Color.RED : Color.BLACK);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_mission_record);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        //Log.d("PollingHelper", "timer cancel");
        super.onDestroy();
    }

    @Override
    protected void provideMaterial() {
        putArgument(ArgumentTag.AT_RUNNABLE_UPDATE_SENSOR_DATA, updateItemTextList);
    }

    @Override
    protected void onInitializeBusiness() {
        importPollingMissionRecord();
        scanBleSensor();
        startUpdateSensorData();
        changeTitle();
        initializeLayout();
    }

    private void scanBleSensor() {
        notifyManager(OperaType.OT_SCAN_BLE_SENSOR);
    }

    private void startUpdateSensorData() {
        timer = new Timer();
        timer.schedule(updateSensorData, 0, getResources().getInteger(R.integer.time_interval_update_sensor_data));
    }

    private void initializeLayout() {
        initializeDeviceImageView();
        initializePollingItemListView();
        initializePollingProjectEvaluationAndResult();
    }

    private void initializePollingProjectEvaluationAndResult() {
        rdogrpEvaluation = (RadioGroup)findViewById(R.id.rdogrp_mission_evaluation);
        edttxtEvaluation = (EditText)findViewById(R.id.edttxt_mission_record_result);
        rdogrpEvaluation.check(getEvaluationRadioButtonId(false, missionRecord.getEvaluationType()));
        edttxtEvaluation.setText(missionRecord.getRecordResult());
    }

    private void initializePollingItemListView() {
        ListView listView = (ListView)findViewById(R.id.lv_polling_items);
        itemAdapter = new PollingItemAdapter();
        listView.setAdapter(itemAdapter);
    }

    private String generateItemText(PollingItemRecord itemRecord) {
        String itemText = itemRecord.getItemConfig().getMeasureName() + "（" + itemRecord.getItemConfig().getMeasureUnit() + "）"+ "：" + String.format("%.3f", itemRecord.getValue());
        if (itemRecord.isOutOfAlarm()) {
            if (itemRecord.isOutOfDownAlarm()) {
                itemText += generateItemOutOfAlarmInstruction(true, itemRecord.getItemConfig().getDownAlarm());
            } else {
                itemText += generateItemOutOfAlarmInstruction(false, itemRecord.getItemConfig().getUpAlarm());
            }
        }
        return itemText;
    }

    private String generateItemOutOfAlarmInstruction(boolean downOrUpAlarm, double threshold) {
        return "（" + (downOrUpAlarm ? getString(R.string.ui_li_down_alarm) :
                getString(R.string.ui_li_up_alarm)) +
                threshold + "）";
    }

    private void initializeDeviceImageView() {
        ImageView imageView = (ImageView)findViewById(R.id.iv_polling_mission);
        imageView.setImageBitmap(getBitmapFromByteArray(missionRecord.getMissionConfig().getDeviceImageData()));
    }

    private void changeTitle() {
        setTitle(missionRecord.getMissionConfig().getName() + "（" + missionRecord.getPollingState().toString() + "）");
    }

    private void importPollingMissionRecord() {
        //missionRecord = operationInfo.getCurrentMissionRecord();
        missionRecord = (PollingMissionRecord)getArgument(ArgumentTag.AT_MISSION_RECORD_CURRENT);
        if (missionRecord.getPollingState() != PollingState.PS_COMPLETED) {
            missionRecord.setPollingState(PollingState.PS_RUNNING);
        }
        originTime = missionRecord.getFinishedTime();
    }

    @Override
    public void onBackPressed() {
        updatePollingMissionRecord();
        exportPollingMissionRecord();
        super.onBackPressed();
    }

    private void exportPollingMissionRecord() {
        if (originTime != missionRecord.getFinishedTime()) {
            //putArgument(ArgumentTag.AT_MISSION_RECORD_CURRENT, missionRecord);
            notifyManager(OperaType.OT_EXPORT_POLLING_MISSION_RECORD);
        }
    }

    private void updatePollingMissionRecord() {
        RadioButton rdoEvaluation = (RadioButton)findViewById(rdogrpEvaluation.getCheckedRadioButtonId());
        missionRecord.setEvaluationType(EvaluationType.createFromString(rdoEvaluation.getText()));
        missionRecord.setRecordResult(edttxtEvaluation.getText().toString());
        missionRecord.setPollingState(PollingState.PS_COMPLETED);
        missionRecord.setFinishedTime();
    }
}