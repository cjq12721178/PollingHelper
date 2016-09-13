package com.example.kat.pollinghelper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.SensorEntityAdapter;
import com.example.kat.pollinghelper.bean.scout.ScoutCellSensorEntity;
import com.example.kat.pollinghelper.bean.scout.ScoutCellState;

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
            for (ScoutSensorConfig sensorConfig :
                    sensorConfigs) {
                ScoutCellSensorEntity sensorEntity = new ScoutCellSensorEntity(sensorConfig);
                sensorEntity.setState(ScoutCellState.PCS_INVARIANT);
                existSensorEntities.add(sensorEntity);
            }
        }
    }

    private void importSensorConfigs() {
        isRestoreProjectAndSensorConfig = false;
        sensorConfigs = (List<ScoutSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SENSOR_CONFIG) {
            ScoutCellSensorEntity sensorEntity = (ScoutCellSensorEntity)getArgument(ArgumentTag.AT_SCOUT_ENTITY_FEEDBACK);
            if (sensorEntity != null) {
                if (sensorEntity.getState() == ScoutCellState.PCS_NEW) {
                    if (!existSensorEntities.contains(sensorEntity)) {
                        existSensorEntities.add(sensorEntity);
                        sensorConfigs.add(sensorEntity.getSensorConfig());
                    }
                    sensorEntityAdapter.notifyDataSetChanged();
                } else if (sensorEntity.getState() == ScoutCellState.PCS_MODIFIED) {
                    sensorEntityAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addOrModifySensorConfig(ScoutCellSensorEntity currentSensorEntity) {
        putArgument(ArgumentTag.AT_SCOUT_ENTITY_FEEDBACK, currentSensorEntity);
        putArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG, sensorConfigs);
        startActivityForResult(new Intent(SensorEntityActivity.this, SensorConfigActivity.class), REQUEST_CODE_SENSOR_CONFIG);
    }

    @Override
    public void onBackPressed() {
        if (isSensorEntityModified()) {
            showAlternativeDialog(R.string.ui_prompt_sensor_config_modified,
                    onConfirmClickListener, onCancelClickListener);
        } else {
            exitSensorEntity();
        }
    }

    private boolean isSensorEntityModified() {
        if (!desertedSensorEntities.isEmpty()) {
            return true;
        }

        for (ScoutCellSensorEntity sensorEntity :
                existSensorEntities) {
            if (sensorEntity.getState() != ScoutCellState.PCS_INVARIANT) {
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
        notifyManager(OperaType.OT_EXPORT_SENSOR_CONFIG, success, failure);
    }

    private Runnable success = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_sensor_configs_success);
        }
    };

    private Runnable failure = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_sensor_configs_failed);
        }
    };

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exportSensorConfigs();
            exitSensorEntity();
        }
    };

    private View.OnClickListener onCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isRestoreProjectAndSensorConfig = true;
            exitSensorEntity();
        }
    };

    private SensorEntityAdapter.OnItemCheckClickedListener onItemCheckClickedListener = new SensorEntityAdapter.OnItemCheckClickedListener() {
        @Override
        public void onClicked(CheckBox checkBox, int position) {
            if (sensorEntityAdapter.isInDeleteChoiceState()) {
                ScoutCellSensorEntity sensorEntity = sensorEntityAdapter.getItem(position);
                sensorEntity.setChecked(!sensorEntity.isChecked());
                checkBox.setChecked(sensorEntity.isChecked());
            } else {
                addOrModifySensorConfig(existSensorEntities.get(position));
            }
        }
    };

    private AdapterView.OnItemClickListener sensorEntityClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onItemCheckClickedListener.onClicked((CheckBox)parent.getChildAt(position).findViewById(R.id.chk_delete_choice), position);
        }
    };

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
                    ScoutCellSensorEntity sensorEntity = existSensorEntities.get(sensorIndex);
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

    private List<ScoutSensorConfig> sensorConfigs;
    private static final int REQUEST_CODE_SENSOR_CONFIG = 1;
    private boolean isRestoreProjectAndSensorConfig;
    private Button btnDeleteChoice;
    private SensorEntityAdapter sensorEntityAdapter;
    private List<ScoutCellSensorEntity> desertedSensorEntities;
    private List<ScoutCellSensorEntity> existSensorEntities;
}
