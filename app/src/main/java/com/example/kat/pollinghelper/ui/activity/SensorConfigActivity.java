package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.scout.ScoutEntity;
import com.example.kat.pollinghelper.structure.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.adapter.BaseConfigAdapter;
import com.example.kat.pollinghelper.structure.scout.ScoutCellClause;
import com.example.kat.pollinghelper.structure.scout.ScoutCellSensorEntity;
import com.example.kat.pollinghelper.structure.scout.ScoutCellState;

import java.util.ArrayList;
import java.util.List;

public class SensorConfigActivity extends ScoutConfigBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_config);
    }

    @Override
    protected void onInitializeBusiness() {
        super.onInitializeBusiness();
        importSensorConfigs();
    }

    private void importSensorConfigs() {
        sensorConfigs = (List<ScoutSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
    }

    @Override
    protected BaseAdapter initListView() {
        BaseConfigAdapter baseConfigAdapter = new BaseConfigAdapter(this, getClauses());
        ListView listView = (ListView)findViewById(R.id.lv_sensor_config);
        listView.setAdapter(baseConfigAdapter);
        listView.setOnItemClickListener(itemConfigClickListener);
        return baseConfigAdapter;
    }

    @Override
    protected ScoutCellSensorEntity getEntity() {
        return (ScoutCellSensorEntity)super.getEntity();
    }

    @Override
    protected List<ScoutCellClause> createClauses() {
        List<ScoutCellClause> clauses = new ArrayList<>();
        ScoutSensorConfig sensorConfig = getEntity().getSensorConfig();
        clauses.add(new ScoutCellClause(getString(R.string.ui_tv_sensor_name_label),
                sensorConfig.getName()));
        clauses.add(new ScoutCellClause(getString(R.string.ui_tv_sensor_address_label),
                sensorConfig.getAddress()));
        clauses.add(new ScoutCellClause(getString(R.string.ui_tv_sensor_description_label),
                sensorConfig.getDescription()));
        return clauses;
    }

    @Override
    protected ScoutEntity importEntity() {
        ScoutCellSensorEntity sensorEntity = (ScoutCellSensorEntity)getArgument(ArgumentTag.AT_SCOUT_ENTITY_FEEDBACK);
        if (sensorEntity == null) {
            sensorEntity = new ScoutCellSensorEntity(new ScoutSensorConfig());
            sensorEntity.setState(ScoutCellState.PCS_NEW);
            setFirstEdit(true);
        } else {
            setFirstEdit(false);
        }
        return sensorEntity;
    }

    @Override
    protected boolean updateEntity() {
        ScoutCellClause sensorNameClause = getContent(R.string.ui_tv_sensor_name_label);
        if (sensorNameClause.getContentString().isEmpty()) {
            processConfigError(R.string.ui_prompt_sensor_name_empty);
            return false;
        }

        if (isSensorConfigNameRepeated(sensorNameClause.getContentString())) {
            processConfigError(R.string.ui_prompt_sensor_name_repetition);
            return false;
        }

        ScoutCellClause sensorAddressClause = getContent(R.string.ui_tv_sensor_address_label);
        if (sensorAddressClause.getContentString().isEmpty()) {
            processConfigError(R.string.ui_prompt_sensor_address_empty);
            return false;
        }

        if (isSensorAddressRepeated(sensorAddressClause.getContentString())) {
            processConfigError(R.string.ui_prompt_sensor_address_repetition);
            return false;
        }

        ScoutCellClause descriptionClause = getContent(R.string.ui_tv_sensor_description_label);
        ScoutCellSensorEntity sensorEntity = getEntity();
        ScoutSensorConfig sensorConfig = sensorEntity.getSensorConfig();
        sensorConfig.setName((String)sensorNameClause.getContent());
        if (sensorEntity.getState() == ScoutCellState.PCS_NEW) {
            sensorEntity.setName(sensorConfig.getName());
        }
        sensorConfig.setAddress((String)sensorAddressClause.getContent());
        sensorConfig.setDescription((String)descriptionClause.getContent());

        return true;
    }

    private boolean isSensorAddressRepeated(String newAddress) {
        ScoutSensorConfig currentSensorConfig = getEntity().getSensorConfig();
        for (ScoutSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorConfig.getAddress().equals(newAddress) &&
                    sensorConfig != currentSensorConfig) {
                return true;
            }
        }
        return false;
    }

    private boolean isSensorConfigNameRepeated(String newSensorName) {
        ScoutSensorConfig currentSensorConfig = getEntity().getSensorConfig();
        for (ScoutSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorConfig.getName().equals(newSensorName) &&
                    sensorConfig != currentSensorConfig) {
                return true;
            }
        }
        return false;
    }

    private AdapterView.OnItemClickListener itemConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScoutCellClause currentItemClause = getCurrentClause(position);
            showEditDialog(currentItemClause.getLabel(), currentItemClause.getContentString());
        }
    };

    private List<ScoutSensorConfig> sensorConfigs;
}
