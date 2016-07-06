package com.example.kat.pollinghelper.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.record.EvaluationType;
import com.example.kat.pollinghelper.fuction.record.PollingMissionRecord;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollingProjectRecordActivity extends ManagedActivity implements AdapterView.OnItemClickListener {
    private PollingProjectRecord projectRecord;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> listDatas;
    private RadioGroup rdogrpEvaluation;
    private EditText edttxtEvaluation;
    private final String deviceImage = "img";
    private final String missionName = "lbl";
    private final String recordResult = "res";
    private Date originTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_project_record);
    }

    @Override
    protected void onInitializeBusiness() {
        importPollingProjectRecord();
        changeTitle();
        initializeLayout();
    }

    private void initializeLayout() {
        initializePollingEventListView();
        initializePollingProjectEvaluationAndResult();
    }

    private void initializePollingProjectEvaluationAndResult() {
        rdogrpEvaluation = (RadioGroup)findViewById(R.id.rdogrp_project_evaluation);
        edttxtEvaluation = (EditText)findViewById(R.id.edttxt_project_record_result);
        rdogrpEvaluation.check(getEvaluationRadioButtonId(true, projectRecord.getEvaluationType()));
        edttxtEvaluation.setText(projectRecord.getRecordResult());
    }

    private void changeTitle() {
        setTitle(getPollingRecordLabel(projectRecord.getProjectConfig().getName(), projectRecord.getPollingState()));
    }

    private void importPollingProjectRecord() {
        projectRecord = (PollingProjectRecord)getArgument(ArgumentTag.AT_PROJECT_RECORD_CURRENT);
        if (projectRecord.getPollingState() != PollingState.PS_COMPLETED) {
            projectRecord.setPollingState(PollingState.PS_RUNNING);
        }
        originTime = projectRecord.getFinishedTime();
    }

    private void initializePollingEventListView() {
        ListView lvPollingMissions = (ListView) findViewById(R.id.lv_polling_missions);
        lvPollingMissions.setAdapter(generateSimpleAdapter());
        lvPollingMissions.setOnItemClickListener(this);
    }

    private SimpleAdapter generateSimpleAdapter() {
        if (simpleAdapter == null) {
            simpleAdapter = new SimpleAdapter(this, generatePollingMissionListData(), R.layout.listitem_polling_mission_record,
                    new String[]{deviceImage, missionName, recordResult},
                    new int[]{R.id.iv_device, R.id.tv_polling_mission_name, R.id.tv_polling_mission_record_result});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view instanceof ImageView && data instanceof byte[]) {
                        ImageView imageView = (ImageView)view;
                        //imageView.setImageBitmap(BitmapFactory.decodeByteArray((byte[]) data, 0, ((byte[]) data).length));
                        imageView.setImageBitmap(getBitmapFromByteArray((byte[])data));
                        return true;
                    }
                    return false;
                }
            });
        }
        return simpleAdapter;
    }

    private List<? extends Map<String, ?>> generatePollingMissionListData() {
        if (listDatas == null) {
            listDatas = new ArrayList<>();
            for (PollingMissionRecord missionRecord :
                    projectRecord.getMissionRecords()) {
                Map<String, Object> map = new HashMap<>();
                map.put(deviceImage, missionRecord.getMissionConfig().getDeviceImageData());
                map.put(missionName, missionRecord.getMissionConfig().getName());
                map.put(recordResult, missionRecord.getPollingState().toString());
                listDatas.add(map);
            }
        }
        return listDatas;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startPollingMission(position);
    }

    private void startPollingMission(int missionIndex) {
        if (missionIndex >= 0 && missionIndex < projectRecord.getProjectConfig().getMissions().size()) {
            putArgument(ArgumentTag.AT_MISSION_RECORD_CURRENT, projectRecord.getMissionRecords().get(missionIndex));
            startActivityForResult(new Intent(this, PollingMissionRecordActivity.class), missionIndex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        changeMissionRecordUIState(requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeMissionRecordUIState(int missionIndex) {
        if (missionIndex >= 0 && missionIndex < projectRecord.getMissionRecords().size()) {
            PollingMissionRecord currentMissionRecord = projectRecord.getMissionRecords().get(missionIndex);
            listDatas.get(missionIndex).remove(recordResult);
            listDatas.get(missionIndex).put(recordResult, currentMissionRecord.getPollingState().toString());
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        updatePollingProjectRecord();
        if (projectRecord.getPollingState() != PollingState.PS_COMPLETED) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(getString(R.string.ui_prompt_exit_polling_project));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ui_prompt_yes), exitAlertListener);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ui_prompt_no), exitAlertListener);
            dialog.show();
        } else {
            exitPollingProjectRecord();
        }
    }

    private void updatePollingProjectRecord() {
        RadioButton rdoEvaluation = (RadioButton)findViewById(rdogrpEvaluation.getCheckedRadioButtonId());
        projectRecord.setEvaluationType(EvaluationType.createFromString(rdoEvaluation.getText()));
        projectRecord.setRecordResult(edttxtEvaluation.getText().toString());
        projectRecord.setPollingState(correctifyPollingState());
        projectRecord.setFinishedTime();
    }

    private PollingState correctifyPollingState() {
        PollingState result = PollingState.PS_COMPLETED;
        for (PollingMissionRecord missionRecord :
                projectRecord.getMissionRecords()) {
                switch (missionRecord.getPollingState()) {
                    case PS_UNKNOWN: {
                        result = PollingState.PS_UNKNOWN;
                    } break;
                    case PS_RUNNING: {
                        result = PollingState.PS_RUNNING;
                    } break;
                    case PS_UNDONE: {
                        if (result != PollingState.PS_RUNNING) {
                            result = PollingState.PS_UNDONE;
                        }
                    } break;
                    default: {
                    } break;
                }
                if (result == PollingState.PS_UNKNOWN) {
                    break;
                }
            }
        return result;
    }

    private void exitPollingProjectRecord() {
        if (originTime != projectRecord.getFinishedTime()) {
            //putArgument(ArgumentTag.AT_PROJECT_RECORD_CURRENT, projectRecord);
            notifyManager(OperaType.OT_EXPORT_POLLING_PROJECT_RECORD);
        }
        super.onBackPressed();
    }

    private DialogInterface.OnClickListener exitAlertListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                exitPollingProjectRecord();
            }
        }
    };
}
