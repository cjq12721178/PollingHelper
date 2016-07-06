package com.example.kat.pollinghelper.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.SensorEntityAdapter;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemSensorEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.List;

public class SensorEntityActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_configuration);
        findViewById(R.id.btn_sensor_config_add).setOnClickListener(onAddSensorConfigListener);
        btnDeleteChoice = (Button) findViewById(R.id.btn_sensor_config_delete_choice);
        btnDeleteChoice.setOnClickListener(onDeleteSensorConfigListener);
    }

    @Override
    protected void provideMaterial() {
        putArgument(ArgumentTag.AT_RUNNABLE_EXPORT_SENSOR_CONFIGS_SUCCESS, success);
        putArgument(ArgumentTag.AT_RUNNABLE_EXPORT_SENSOR_CONFIGS_FAILED, failure);
    }

    @Override
    protected void onInitializeBusiness() {
        importSensorConfigs();
        generateSensorEntities();
        createDesertedSensorEntities();
        initializeSensorConfigListView();
    }

    private void initializeSensorConfigListView() {
        sensorEntityAdapter = new SensorEntityAdapter(this, existSensorEntities);
        sensorEntityAdapter.setInDeleteChoiceState(false);
        sensorEntityAdapter.setOnItemCheckClickedListener(onItemCheckClickedListener);
        ListView lvSensorConfig = (ListView)findViewById(R.id.lv_sensor_entity);
        lvSensorConfig.setAdapter(sensorEntityAdapter);
        lvSensorConfig.setOnItemClickListener(sensorEntityClickListener);
    }

    private void createDesertedSensorEntities() {
        desertedSensorEntities = new ArrayList<>();
    }

    private void generateSensorEntities() {
        if (sensorConfigs != null) {
            existSensorEntities = new ArrayList<>();
            for (PollingSensorConfig sensorConfig :
                    sensorConfigs) {
                PollingConfigListItemSensorEntity sensorEntity = new PollingConfigListItemSensorEntity(sensorConfig);
                sensorEntity.setState(PollingConfigState.PCS_INVARIANT);
                existSensorEntities.add(sensorEntity);
            }
        }
    }

    private void importSensorConfigs() {
        isRestoreProjectAndSensorConfig = false;
        sensorConfigs = (List<PollingSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SENSOR_CONFIG) {
            PollingConfigListItemSensorEntity sensorEntity = (PollingConfigListItemSensorEntity)getArgument(ArgumentTag.AT_SENSOR_ENTITY_CURRENT);
            if (sensorEntity.getState() == PollingConfigState.PCS_NEW) {
                if (!existSensorEntities.contains(sensorEntity)) {
                    existSensorEntities.add(sensorEntity);
                    sensorConfigs.add(sensorEntity.getSensorConfig());
                }
                sensorEntityAdapter.notifyDataSetChanged();
            } else if (sensorEntity.getState() == PollingConfigState.PCS_MODIFIED) {
                sensorEntityAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private AdapterView.OnItemClickListener sensorEntityClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onItemCheckClickedListener.onClicked((CheckBox)parent.getChildAt(position).findViewById(R.id.chk_delete_choice), position);
        }
    };

    private SensorEntityAdapter.OnItemCheckClickedListener onItemCheckClickedListener = new SensorEntityAdapter.OnItemCheckClickedListener() {
        @Override
        public void onClicked(CheckBox checkBox, int position) {
            if (sensorEntityAdapter.isInDeleteChoiceState()) {
                PollingConfigListItemSensorEntity sensorEntity = sensorEntityAdapter.getItem(position);
                sensorEntity.setChecked(!sensorEntity.isChecked());
                checkBox.setChecked(sensorEntity.isChecked());
            } else {
                addOrModifySensorConfig(existSensorEntities.get(position));
            }
        }
    };

    public void addOrModifySensorConfig(PollingConfigListItemSensorEntity currentSensorEntity) {
        putArgument(ArgumentTag.AT_SENSOR_ENTITY_CURRENT, currentSensorEntity);
        putArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG, sensorConfigs);
        startActivityForResult(new Intent(SensorEntityActivity.this, SensorConfigActivity.class), REQUEST_CODE_SENSOR_CONFIG);
    }

    @Override
    public void onBackPressed() {
        if (isSensorEntityModified()) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(getString(R.string.ui_prompt_sensor_config_modified));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ui_prompt_yes), exitAlertListener);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ui_prompt_no), exitAlertListener);
            dialog.show();
        } else {
            exitSensorEntity();
        }
    }

    private boolean isSensorEntityModified() {
        if (!desertedSensorEntities.isEmpty()) {
            return true;
        }

        for (PollingConfigListItemSensorEntity sensorEntity :
                existSensorEntities) {
            if (sensorEntity.getState() != PollingConfigState.PCS_INVARIANT) {
                return true;
            }
        }
        return false;
    }

    private void exitSensorEntity() {
        setResult(RESULT_OK, getIntent().putExtra(ArgumentTag.RESTORE_PROJECT_AND_SENSOR_CONFIG, isRestoreProjectAndSensorConfig));
        super.onBackPressed();
    }

    private void exportSensorConfigs() {
        showLoadingDialog(R.string.ui_export_sensor_configs);
        putArgument(ArgumentTag.AT_LIST_SENSOR_ENTITY_EXIST, existSensorEntities);
        putArgument(ArgumentTag.AT_LIST_SENSOR_ENTITY_DESERTED, desertedSensorEntities);
        notifyManager(OperaType.OT_EXPORT_SENSOR_CONFIG);
    }

    private View.OnClickListener onAddSensorConfigListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addOrModifySensorConfig(null);
        }
    };

    private View.OnClickListener onDeleteSensorConfigListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sensorEntityAdapter.isInDeleteChoiceState()) {
                for (int sensorIndex = 0;sensorIndex < existSensorEntities.size();++sensorIndex) {
                    PollingConfigListItemSensorEntity sensorEntity = existSensorEntities.get(sensorIndex);
                    if (sensorEntity.isChecked()) {
                        sensorConfigs.remove(sensorEntity.getSensorConfig());
                        existSensorEntities.remove(sensorIndex--);
                        desertedSensorEntities.add(sensorEntity);
                        sensorEntity.getSensorConfig().setName(sensorEntity.getSensorConfig().getName() + getString(R.string.deleted));
                    }
                }
                btnDeleteChoice.setText(getString(R.string.ui_btn_choice));
            } else {
                btnDeleteChoice.setText(getString(R.string.ui_btn_delete));
            }
            sensorEntityAdapter.setInDeleteChoiceState(!sensorEntityAdapter.isInDeleteChoiceState());
            sensorEntityAdapter.notifyDataSetChanged();
        }
    };

    private DialogInterface.OnClickListener exitAlertListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                exportSensorConfigs();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                //notifyManager(OperaType.OT_IMPORT_PROJECT_AND_SENSOR_CONFIGS);
                isRestoreProjectAndSensorConfig = true;
            }
            exitSensorEntity();
        }
    };

    private Runnable success = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_polling_configs_success);
        }
    };

    private Runnable failure = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_polling_configs_failed);
        }
    };

    private boolean isRestoreProjectAndSensorConfig;
    private static final int REQUEST_CODE_SENSOR_CONFIG = 1;
    private Button btnDeleteChoice;
    private SensorEntityAdapter sensorEntityAdapter;
    private List<PollingConfigListItemSensorEntity> desertedSensorEntities;
    private List<PollingConfigListItemSensorEntity> existSensorEntities;
    private List<PollingSensorConfig> sensorConfigs;
}
