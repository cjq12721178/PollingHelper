package com.example.kat.pollinghelper.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.adapter.MissionConfigAdapter;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCell;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellClause;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellProjectEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellMissionEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellState;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutEntity;
import com.example.kat.pollinghelper.utility.Converter;

import java.util.ArrayList;
import java.util.List;

public class ScoutMissionConfigActivity extends ScoutConfigBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_mission_config);
    }

    @Override
    protected BaseAdapter initListView() {
        MissionConfigAdapter missionConfigAdapter = new MissionConfigAdapter(this, getClauses());
        ListView listView = (ListView)findViewById(R.id.lv_mission_config);
        listView.setAdapter(missionConfigAdapter);
        listView.setOnItemClickListener(missionConfigClickListener);
        return missionConfigAdapter;
    }

    @Override
    protected ScoutEntity importEntity() {
        ScoutCell listItem = (ScoutCell)getArgument(ArgumentTag.AT_SCOUT_CELL_CURRENT_CLICKED);
        ScoutCellMissionEntity missionEntity;
        if (listItem.isEntity()) {
            missionEntity = (ScoutCellMissionEntity)listItem;
            setFirstEdit(false);
        } else {
            missionEntity = new ScoutCellMissionEntity(this, new ScoutMissionConfig());
            missionEntity.setState(ScoutCellState.PCS_NEW);
            setFirstEdit(true);
        }
        return missionEntity;
    }

    @Override
    protected ScoutCellMissionEntity getEntity() {
        return (ScoutCellMissionEntity)super.getEntity();
    }

    @Override
    protected List<ScoutCellClause> createClauses() {
        currentProjectEntities = (List<ScoutCellProjectEntity>)getArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY);
        List<ScoutCellClause> itemClauses = new ArrayList<>();
        ScoutMissionConfig missionConfig = getEntity().getMissionConfig();
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_tv_mission_config_label_name),
                missionConfig.getName()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_tv_mission_config_label_description),
                missionConfig.getDescription()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_tv_mission_config_label_device_image),
                missionConfig.getDeviceImageData()));
        return itemClauses;
    }

    private AdapterView.OnItemClickListener missionConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScoutCellClause currentItemClause = getCurrentClause(position);
            if (currentItemClause.getLabel() != getString(R.string.ui_tv_mission_config_label_device_image)) {
                showEditDialog(currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                openAlbum();
            }
        }
    };

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedDeviceImage = data.getData();
            if (selectedDeviceImage != null) {
                getCurrentClause().setContent(Converter.uriToByteArray(this, selectedDeviceImage));
                getBaseAdapter().notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected boolean updateEntity() {
        ScoutCellClause missionNameClause = getContent(R.string.ui_tv_mission_config_label_name);
        ScoutCellClause descriptionClause = getContent(R.string.ui_tv_mission_config_label_description);
        ScoutCellClause deviceImageDataClause = getContent(R.string.ui_tv_mission_config_label_device_image);

        if (missionNameClause.getContentString().isEmpty()) {
            processConfigError(R.string.ui_prompt_mission_name_empty);
            return false;
        }

        if (isMissionNameRepetition(missionNameClause.getContentString())) {
            processConfigError(R.string.ui_prompt_mission_name_repetition);
            return false;
        }

        if (deviceImageDataClause.getContent() == null) {
            processConfigError(R.string.ui_prompt_device_image_empty);
            return false;
        }

        ScoutCellMissionEntity missionEntity = getEntity();
        ScoutMissionConfig missionConfig = missionEntity.getMissionConfig();
        missionConfig.setName((String)missionNameClause.getContent());
        if (missionEntity.getState() == ScoutCellState.PCS_NEW) {
            missionEntity.setName(missionConfig.getName());
        }
        missionConfig.setDescription((String)descriptionClause.getContent());
        missionConfig.setDeviceImageData((byte[])deviceImageDataClause.getContent());

        return true;
    }

    private boolean isMissionNameRepetition(String newMissionName) {
        ScoutCellMissionEntity currentMissionEntity = getEntity();
        for (ScoutCellProjectEntity projectEntity :
                currentProjectEntities) {
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                ScoutCellMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (currentMissionEntity != missionEntity &&
                        missionEntity.getMissionConfig().getName().equals(newMissionName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<ScoutCellProjectEntity> currentProjectEntities;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
}
