package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.adapter.BaseConfigAdapter;
import com.example.kat.pollinghelper.ui.dialog.EditDialog;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemClause;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemSensorEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.List;

public class SensorConfigActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_config);
    }

    @Override
    protected void onInitializeBusiness() {
        initializeSensorEntity();
        importSensorConfigs();
        generateItemClauses();
        createEditDialog();
        initializeSensorConfigListView();
    }

    private void importSensorConfigs() {
        //sensorConfigs = operationInfo.getSensorConfigs();
        sensorConfigs = (List<PollingSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
    }

    private void initializeSensorConfigListView() {
        baseConfigAdapter = new BaseConfigAdapter(this, itemClauses);
        ListView listView = (ListView)findViewById(R.id.lv_sensor_config);
        listView.setAdapter(baseConfigAdapter);
        listView.setOnItemClickListener(itemConfigClickListener);
    }

    private void createEditDialog() {
        editDialog = new EditDialog();
        editDialog.setOnClickPositiveListener(onEditDialogPositiveClickListener);
    }

    private void generateItemClauses() {
        itemClauses = new ArrayList<>();
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_sensor_name_label),
                sensorEntity.getSensorConfig().getName()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_sensor_address_label),
                sensorEntity.getSensorConfig().getAddress()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_sensor_description_label),
                sensorEntity.getSensorConfig().getDescription()));
    }

    private void initializeSensorEntity() {
        //sensorEntity = operationInfo.getCurrentSensorEntity();
        sensorEntity = (PollingConfigListItemSensorEntity)getArgument(ArgumentTag.AT_SENSOR_ENTITY_CURRENT);
        if (sensorEntity == null) {
            sensorEntity = new PollingConfigListItemSensorEntity(new PollingSensorConfig());
            sensorEntity.setState(PollingConfigState.PCS_NEW);
        }
    }

    @Override
    public void onBackPressed() {
        if (sensorEntity.getState() == PollingConfigState.PCS_UNKNOWN) {
            promptMessage(R.string.ui_prompt_sensor_config_unknown_error);
            setResult(RESULT_CANCELED);
        } else {
            if (!updateSensorEntity()) {
                return;
            }
            //operationInfo.setCurrentSensorEntity(sensorEntity);
            putArgument(ArgumentTag.AT_SENSOR_ENTITY_CURRENT, sensorEntity);
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private boolean updateSensorEntity() {
        PollingConfigListItemClause sensorNameClause = getContent(R.string.ui_tv_sensor_name_label);
        if (sensorNameClause.getContentString().isEmpty()) {
            promptMessage(R.string.ui_prompt_sensor_name_empty);
            return false;
        }

        if (isSensorConfigNameRepeated(sensorNameClause.getContentString())) {
            promptMessage(R.string.ui_prompt_sensor_name_repetition);
            return false;
        }

        PollingConfigListItemClause sensorAddressClause = getContent(R.string.ui_tv_sensor_address_label);
        if (sensorAddressClause.getContentString().isEmpty()) {
            promptMessage(R.string.ui_prompt_sensor_address_empty);
            return false;
        }

        if (isSensorAddressRepeated(sensorAddressClause.getContentString())) {
            promptMessage(R.string.ui_prompt_sensor_address_repetition);
            return false;
        }

        PollingConfigListItemClause descriptionClause = getContent(R.string.ui_tv_sensor_description_label);
        PollingSensorConfig sensorConfig = sensorEntity.getSensorConfig();
        sensorConfig.setName((String)sensorNameClause.getContent());
        if (sensorEntity.getState() == PollingConfigState.PCS_NEW) {
            sensorEntity.setName(sensorConfig.getName());
        }
        sensorConfig.setAddress((String)sensorAddressClause.getContent());
        sensorConfig.setDescription((String)descriptionClause.getContent());

        if (sensorEntity.getState() == PollingConfigState.PCS_INVARIANT) {
            if (isSensorConfigModified()) {
                sensorEntity.setState(PollingConfigState.PCS_MODIFIED);
            }
        }
        return true;
    }

    public boolean isSensorConfigModified() {
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.isModified()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSensorAddressRepeated(String newAddress) {
        for (PollingSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorConfig.getAddress().equals(newAddress) && sensorConfig != sensorEntity.getSensorConfig()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSensorConfigNameRepeated(String newSensorName) {
        for (PollingSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorConfig.getName().equals(newSensorName) && sensorConfig != sensorEntity.getSensorConfig()) {
                return true;
            }
        }
        return false;
    }

    private PollingConfigListItemClause getContent(int stringID) {
        PollingConfigListItemClause result = null;
        final String label = getString(stringID);
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.getLabel().equals(label)) {
                result = itemClause;
                break;
            }
        }
        return result;
    }

    private AdapterView.OnItemClickListener itemConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemClause = itemClauses.get(position);
            editDialog.show(getSupportFragmentManager(), currentItemClause.getLabel(), currentItemClause.getContentString());
        }
    };

    private EditDialog.OnClickPositiveListener onEditDialogPositiveClickListener = new EditDialog.OnClickPositiveListener() {
        @Override
        public void onClick(String content) {
            currentItemClause.setContent(content);
            baseConfigAdapter.notifyDataSetChanged();
        }
    };

    private EditDialog editDialog;
    private PollingConfigListItemClause currentItemClause;
    private BaseConfigAdapter baseConfigAdapter;
    private PollingConfigListItemSensorEntity sensorEntity;
    private List<PollingConfigListItemClause> itemClauses;
    private List<PollingSensorConfig> sensorConfigs;
}
