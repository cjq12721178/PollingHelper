package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.structure.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.dialog.ListDialog;
import com.example.kat.pollinghelper.ui.adapter.BaseConfigAdapter;
import com.example.kat.pollinghelper.ui.adapter.SensorShowAdapter;
import com.example.kat.pollinghelper.structure.scout.ScoutCell;
import com.example.kat.pollinghelper.structure.scout.ScoutCellClause;
import com.example.kat.pollinghelper.structure.scout.ScoutCellItemEntity;
import com.example.kat.pollinghelper.structure.scout.ScoutEntity;
import com.example.kat.pollinghelper.structure.scout.ScoutCellState;

import java.util.ArrayList;
import java.util.List;

public class ScoutItemConfigActivity extends ScoutConfigBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_item_config);
    }

    @Override
    protected void onInitializeBusiness() {
        super.onInitializeBusiness();
        createListDialog();
    }

    private void createListDialog() {
        listDialog = new ListDialog();
        dialogContentAdapter = new SensorShowAdapter(this, sensorConfigs);
        listDialog.setContentAdapter(dialogContentAdapter);
        listDialog.setOnContentItemClickListener(onSensorClickListener);
    }

    @Override
    protected BaseAdapter initListView() {
        BaseConfigAdapter baseConfigAdapter = new BaseConfigAdapter(this, getClauses());
        ListView listView = (ListView)findViewById(R.id.lv_item_config);
        listView.setAdapter(baseConfigAdapter);
        listView.setOnItemClickListener(itemConfigClickListener);
        return baseConfigAdapter;
    }

    @Override
    protected ScoutEntity importEntity() {
        ScoutCell listItem = (ScoutCell) getArgument(ArgumentTag.AT_SCOUT_CELL_CURRENT_CLICKED);
        ScoutCellItemEntity itemEntity;
        if (listItem.isEntity()) {
            itemEntity = (ScoutCellItemEntity)listItem;
            setFirstEdit(false);
        } else {
            itemEntity = new ScoutCellItemEntity(this, new ScoutItemConfig());
            itemEntity.setState(ScoutCellState.PCS_NEW);
            setFirstEdit(true);
        }
        return itemEntity;
    }

    @Override
    protected ScoutCellItemEntity getEntity() {
        return (ScoutCellItemEntity)super.getEntity();
    }

    @Override
    protected List<ScoutCellClause> createClauses() {
        sensorConfigs = (List<ScoutSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
        ScoutCellItemEntity itemEntity = getEntity();
        List<ScoutCellClause> itemClauses = new ArrayList<>();
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_measure_name),
                itemEntity.getItemConfig().getMeasureName()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_measure_unit),
                itemEntity.getItemConfig().getMeasureUnit()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_description),
                itemEntity.getItemConfig().getDescription()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_alarm_down),
                itemEntity.getItemConfig().getDownAlarm()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_alarm_up),
                itemEntity.getItemConfig().getUpAlarm()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_li_item_config_label_sensor),
                itemEntity.getItemConfig().getSensor()));
        return itemClauses;
    }

    private AdapterView.OnItemClickListener itemConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScoutCellClause currentItemClause = getCurrentClause(position);
            if (currentItemClause.getLabel() != getString(R.string.ui_li_item_config_label_sensor)) {
                showEditDialog(currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                dialogContentAdapter.selectItem(currentItemClause.getContentString());
                listDialog.show(getSupportFragmentManager(), currentItemClause.getLabel());
            }
        }
    };

    @Override
    protected boolean updateEntity() {
        ScoutCellClause measureNameClause = getContent(R.string.ui_li_item_config_label_measure_name);
        ScoutCellClause measureUnitClause = getContent(R.string.ui_li_item_config_label_measure_unit);
        ScoutCellClause descriptionClause = getContent(R.string.ui_li_item_config_label_description);
        ScoutCellClause alarmDownClause = getContent(R.string.ui_li_item_config_label_alarm_down);
        ScoutCellClause alarmUpClause = getContent(R.string.ui_li_item_config_label_alarm_up);
        ScoutCellClause sensorClause = getContent(R.string.ui_li_item_config_label_sensor);
        if (measureNameClause.getContentString().isEmpty()) {
            processConfigError(R.string.ui_prompt_measure_name_empty);
            return false;
        }

        if (alarmDownClause.getContent() == null) {
            processConfigError(R.string.ui_prompt_alarm_down_empty);
            return false;
        }

        if (!alarmDownClause.contentStringToDouble()) {
            processConfigError(R.string.ui_prompt_alarm_down_convert_error);
            return false;
        }

        if (alarmUpClause.getContent() == null) {
            processConfigError(R.string.ui_prompt_alarm_up_empty);
            return false;
        }

        if (!alarmUpClause.contentStringToDouble()) {
            processConfigError(R.string.ui_prompt_alarm_up_convert_error);
            return false;
        }

        ScoutSensorConfig sensorConfig = (ScoutSensorConfig)sensorClause.getContent();
        if (sensorConfig == null) {
            processConfigError(R.string.ui_prompt_sensor_empty);
            return false;
        }

        ScoutCellItemEntity itemEntity = getEntity();
        ScoutItemConfig itemConfig = itemEntity.getItemConfig();
        itemConfig.setMeasureName((String)measureNameClause.getContent());
        itemConfig.setMeasureUnit((String)measureUnitClause.getContent());
        itemConfig.setDescription((String)descriptionClause.getContent());
        itemConfig.setDownAlarm((double)alarmDownClause.getContent());
        itemConfig.setUpAlarm((double)alarmUpClause.getContent());
        itemConfig.setSensor(sensorConfig);

        return true;
    }

    private ListDialog.OnContentItemClickListener onSensorClickListener = new ListDialog.OnContentItemClickListener() {
        @Override
        public void onClick(int position) {
            getCurrentClause().setContent(sensorConfigs.get(position));
            getBaseAdapter().notifyDataSetChanged();
        }
    };

    private ListDialog listDialog;
    private SensorShowAdapter dialogContentAdapter;
    private List<ScoutSensorConfig> sensorConfigs;
}
