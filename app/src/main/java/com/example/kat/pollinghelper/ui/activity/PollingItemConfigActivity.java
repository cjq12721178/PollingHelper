package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.dialog.EditDialog;
import com.example.kat.pollinghelper.ui.dialog.ListDialog;
import com.example.kat.pollinghelper.ui.adapter.BaseConfigAdapter;
import com.example.kat.pollinghelper.ui.adapter.SensorShowAdapter;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItem;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemItemEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemClause;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.List;

public class PollingItemConfigActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_item_config);
    }

    @Override
    protected void onInitializeBusiness() {
        importItemEntity();
        createEditDialog();
        createListDialog();
        initializeItemConfigListView();
    }

    private void createListDialog() {
        listDialog = new ListDialog();
        dialogContentAdapter = new SensorShowAdapter(this, sensorConfigs);
        listDialog.setContentAdapter(dialogContentAdapter);
        listDialog.setOnContentItemClickListener(onSensorClickListener);
    }

    private void createEditDialog() {
        editDialog = new EditDialog();
        editDialog.setOnClickPositiveListener(onEditDialogPositiveClickListener);
    }

    private void initializeItemConfigListView() {
        baseConfigAdapter = new BaseConfigAdapter(this, itemClauses);
        ListView listView = (ListView)findViewById(R.id.lv_item_config);
        listView.setAdapter(baseConfigAdapter);
        listView.setOnItemClickListener(itemConfigClickListener);
    }

    private void importItemEntity() {
        PollingConfigListItem listItem = (PollingConfigListItem) getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_CURRENT_CLICKED);
        if (listItem.isEntity()) {
            itemEntity = (PollingConfigListItemItemEntity)listItem;
        } else {
            itemEntity = new PollingConfigListItemItemEntity(this, new PollingItemConfig());
            itemEntity.setState(PollingConfigState.PCS_NEW);
        }
        //sensorConfigs = operationInfo.getSensorConfigs();
        sensorConfigs = (List<PollingSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
        itemClauses = new ArrayList<>();
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_measure_name),
                itemEntity.getItemConfig().getMeasureName()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_measure_unit),
                itemEntity.getItemConfig().getMeasureUnit()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_description),
                itemEntity.getItemConfig().getDescription()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_alarm_down),
                itemEntity.getItemConfig().getDownAlarm()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_alarm_up),
                itemEntity.getItemConfig().getUpAlarm()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_li_item_config_label_sensor),
                itemEntity.getItemConfig().getSensor()));
    }

    private AdapterView.OnItemClickListener itemConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemClause = itemClauses.get(position);
            if (currentItemClause.getLabel() != getString(R.string.ui_li_item_config_label_sensor)) {
                editDialog.show(getSupportFragmentManager(), currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                dialogContentAdapter.selectItem(currentItemClause.getContentString());
                listDialog.show(getSupportFragmentManager(), currentItemClause.getLabel());
            }
        }
    };

    private AdapterView.OnItemClickListener dialogListViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemClause.setContent(sensorConfigs.get(position));
            baseConfigAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onBackPressed() {
        if (itemEntity.getState() == PollingConfigState.PCS_UNKNOWN) {
            promptMessage(R.string.ui_prompt_item_config_unknown_error);
            setResult(RESULT_CANCELED);
        } else {
            if (!updateItemEntity()) {
                return;
            }
            if (itemEntity.getState() == PollingConfigState.PCS_NEW) {
                //operationInfo.setNewListItemEntity(itemEntity);
                putArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW, itemEntity);
            }
            setResult(RESULT_OK);
        }

        super.onBackPressed();
    }

    private boolean updateItemEntity() {
        PollingConfigListItemClause measureNameClause = getContent(R.string.ui_li_item_config_label_measure_name);
        PollingConfigListItemClause measureUnitClause = getContent(R.string.ui_li_item_config_label_measure_unit);
        PollingConfigListItemClause descriptionClause = getContent(R.string.ui_li_item_config_label_description);
        PollingConfigListItemClause alarmDownClause = getContent(R.string.ui_li_item_config_label_alarm_down);
        PollingConfigListItemClause alarmUpClause = getContent(R.string.ui_li_item_config_label_alarm_up);
        PollingConfigListItemClause sensorClause = getContent(R.string.ui_li_item_config_label_sensor);
        if (measureNameClause.getContentString().isEmpty()) {
            promptMessage(R.string.ui_prompt_measure_name_empty);
            return false;
        }

        if (alarmDownClause.getContent() == null) {
            promptMessage(R.string.ui_prompt_alarm_down_empty);
            return false;
        }

        if (!alarmDownClause.contentStringToDouble()) {
            promptMessage(R.string.ui_prompt_alarm_down_convert_error);
            return false;
        }

        if (alarmUpClause.getContent() == null) {
            promptMessage(R.string.ui_prompt_alarm_up_empty);
            return false;
        }

        if (!alarmUpClause.contentStringToDouble()) {
            promptMessage(R.string.ui_prompt_alarm_up_convert_error);
            return false;
        }

        PollingSensorConfig sensorConfig = (PollingSensorConfig)sensorClause.getContent();
        if (sensorConfig == null) {
            promptMessage(R.string.ui_prompt_sensor_empty);
            return false;
        }

        PollingItemConfig itemConfig = itemEntity.getItemConfig();
        itemConfig.setMeasureName((String)measureNameClause.getContent());
        itemConfig.setMeasureUnit((String)measureUnitClause.getContent());
        itemConfig.setDescription((String)descriptionClause.getContent());
        itemConfig.setDownAlarm((double)alarmDownClause.getContent());
        itemConfig.setUpAlarm((double)alarmUpClause.getContent());
        itemConfig.setSensor(sensorConfig);

        if (itemEntity.getState() == PollingConfigState.PCS_INVARIANT) {
            if (isItemConfigModified()) {
                itemEntity.setState(PollingConfigState.PCS_MODIFIED);
            }
        }

        return true;
    }

    public boolean isItemConfigModified() {
        boolean result = false;
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.isModified()) {
                result = true;
                break;
            }
        }
        return result;
    }


    private PollingSensorConfig getSensorConfigFromName(String sensorName) {
        PollingSensorConfig result = null;
        for (PollingSensorConfig sensorConfig :
                sensorConfigs) {
            if (sensorName.equals(sensorConfig.getName())) {
                result = sensorConfig;
                break;
            }
        }
        return result;
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

    private EditDialog.OnClickPositiveListener onEditDialogPositiveClickListener = new EditDialog.OnClickPositiveListener() {
        @Override
        public void onClick(String content) {
            currentItemClause.setContent(content);
            baseConfigAdapter.notifyDataSetChanged();
        }
    };

    private ListDialog.OnContentItemClickListener onSensorClickListener = new ListDialog.OnContentItemClickListener() {
        @Override
        public void onClick(int position) {
            currentItemClause.setContent(sensorConfigs.get(position));
            baseConfigAdapter.notifyDataSetChanged();
        }
    };

    private ListDialog listDialog;
    private EditDialog editDialog;
    private PollingConfigListItemClause currentItemClause;
    private SensorShowAdapter dialogContentAdapter;
    private BaseConfigAdapter baseConfigAdapter;
    private PollingConfigListItemItemEntity itemEntity;
    private List<PollingConfigListItemClause> itemClauses;
    private List<PollingSensorConfig> sensorConfigs;
}
