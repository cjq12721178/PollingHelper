package com.example.kat.pollinghelper.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.SlideListViewAdapter;
import com.example.kat.pollinghelper.ui.listview.SlideMenuListView;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItem;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemItemEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemMissionEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemProjectEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemProjectVirtual;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemType;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.List;

public class PollingConfigActivity extends ManagedActivity {
    private SlideListViewAdapter slideListViewAdapter;
    private List<PollingProjectConfig> projectConfigs;
    private List<PollingConfigListItem> existListItems;
    private SlideMenuListView slideMenuListView;
    private List<PollingConfigListItemEntity> desertListItems;
    private boolean pollingConfigModified;
    private boolean isRestoreProjectAndSensorConfig;
    private boolean isExitAfterExport;
    private SlideListViewAdapter.OnTextViewClickListener labelClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, PollingConfigListItem listItem) {
            if (listItem.isEntity()) {
                if (listItem.getType() == PollingConfigListItemType.PCLIT_PROJECT_ENTITY) {
                    shrinkProjectEntity((PollingConfigListItemProjectEntity)listItem);
                    slideListViewAdapter.notifyDataSetChanged();
                } else if (listItem.getType() == PollingConfigListItemType.PCLIT_MISSION_ENTITY) {
                    shrinkMissionEntity((PollingConfigListItemMissionEntity)listItem);
                    slideListViewAdapter.notifyDataSetChanged();
                }
            }
        }

        private void shrinkProjectEntity(PollingConfigListItemProjectEntity projectEntity) {
            if (projectEntity.isUnfold()) {
                foldProjectEntity(projectEntity);
            } else {
                unfoldProjectEntity(projectEntity);
            }
            projectEntity.setUnfold(!projectEntity.isUnfold());
        }

        private void shrinkMissionEntity(PollingConfigListItemMissionEntity missionEntity) {
            if (missionEntity.isUnfold()) {
                foldMissionEntity(missionEntity);
            } else {
                unfoldMissionEntity(missionEntity);
            }
            missionEntity.setUnfold(!missionEntity.isUnfold());
        }
    };
    private SlideListViewAdapter.OnTextViewClickListener deleteClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, PollingConfigListItem listItem) {
            if (listItem.isEntity()) {
                if (listItem.getType() == PollingConfigListItemType.PCLIT_PROJECT_ENTITY) {
                    deletePollingProjectConfig((PollingConfigListItemProjectEntity)listItem);
                } else if (listItem.getType() == PollingConfigListItemType.PCLIT_MISSION_ENTITY) {
                    deletePollingMissionConfig((PollingConfigListItemMissionEntity) listItem);
                } else {
                    deletePollingItemConfig((PollingConfigListItemItemEntity) listItem);
                }
                slideListViewAdapter.notifyDataSetChanged();
            }
        }

        private void deletePollingProjectConfig(PollingConfigListItemProjectEntity projectEntity) {
            if (projectEntity.isUnfold()) {
                foldProjectEntity(projectEntity);
            }
            projectConfigs.remove(projectEntity.getProjectConfig());
            existListItems.remove(projectEntity);
            desertListItems.add(projectEntity);
        }

        private void deletePollingMissionConfig(PollingConfigListItemMissionEntity missionEntity) {
            if (missionEntity.isUnfold()) {
               foldMissionEntity(missionEntity);
            }
            PollingConfigListItemProjectEntity father = (PollingConfigListItemProjectEntity) getImmediateBoss(missionEntity);
            father.removeMission(missionEntity);
            father.getProjectConfig().getMissions().remove(missionEntity.getMissionConfig());
            existListItems.remove(missionEntity);
            desertListItems.add(missionEntity);
        }

        private void deletePollingItemConfig(PollingConfigListItemItemEntity itemEntity) {
            PollingConfigListItemMissionEntity father = (PollingConfigListItemMissionEntity) getImmediateBoss(itemEntity);
            father.removeItem(itemEntity);
            father.getMissionConfig().getItems().remove(itemEntity.getItemConfig());
            existListItems.remove(itemEntity);
            desertListItems.add(itemEntity);
        }
    };
    private SlideListViewAdapter.OnTextViewClickListener addClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, PollingConfigListItem listItem) {
            addOrModifyPollingConfig(listItem);
        }
    };
    private SlideListViewAdapter.OnTextViewClickListener contentClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, PollingConfigListItem listItem) {
            addOrModifyPollingConfig(listItem);
        }
    };
    private Runnable success = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_polling_configs_success);
            onPostExportPollingConfig();
        }
    };
    private Runnable failure = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            promptMessage(R.string.ui_prompt_export_polling_configs_failed);
            onPostExportPollingConfig();
        }
    };
    private DialogInterface.OnClickListener exitAlertListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                pollingConfigModified = true;
                isRestoreProjectAndSensorConfig = false;
                isExitAfterExport = true;
                exportPollingConfigs();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                isRestoreProjectAndSensorConfig = true;
                exitPollingConfig();
            }
            //exitPollingConfig();
        }
    };
    private DialogInterface.OnClickListener saveAlertListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                isExitAfterExport = false;
                exportPollingConfigs();
                //updatePollingConfigs();
                pollingConfigModified = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_polling_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        if (item.getItemId() == R.id.mi_save_config) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            if (isPollingConfigModified()) {
                dialog.setMessage(getString(R.string.ui_prompt_polling_config_modified));
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ui_prompt_yes), saveAlertListener);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ui_prompt_no), saveAlertListener);
            } else {
                dialog.setMessage(getString(R.string.ui_prompt_polling_config_invariant));
            }
            dialog.show();
        } else {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void updatePollingConfigs() {
        desertListItems.clear();
        for (PollingConfigListItemProjectEntity projectEntity :
                getCurrentProjectEntities()) {
            if (projectEntity.getState() != PollingConfigState.PCS_INVARIANT) {
                projectEntity.setName(projectEntity.getProjectConfig().getName());
                projectEntity.setState(PollingConfigState.PCS_INVARIANT);
            }
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                PollingConfigListItemMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (missionEntity.getState() != PollingConfigState.PCS_INVARIANT) {
                    missionEntity.setName(missionEntity.getMissionConfig().getName());
                    missionEntity.setState(PollingConfigState.PCS_INVARIANT);
                }
                for (int itemIndex = 0;itemIndex < missionEntity.getItemEntitySize();++itemIndex) {
                    missionEntity.getItemEntity(itemIndex).setState(PollingConfigState.PCS_INVARIANT);
                }
            }
        }
    }

    private void exportPollingConfigs() {
        showLoadingDialog(R.string.ui_export_polling_configs);
        putArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY, getCurrentProjectEntities());
        putArgument(ArgumentTag.AT_LIST_ENTITY_DESERTED, desertListItems);
        notifyManager(OperaType.OT_EXPORT_POLLING_CONFIGS, success, failure);
    }

    @Override
    protected void onInitializeBusiness() {
        initializeConfigParameter();
        importPollingConfig();
        generateExistListItemsFromPollingProjectConfigs();
        initializeDesertListItems();
        initializePollingConfigListView();
    }

    private void initializeConfigParameter() {
        isRestoreProjectAndSensorConfig = false;
        pollingConfigModified = false;
    }

    private void initializeDesertListItems() {
        desertListItems = new ArrayList<>();
    }

    private void initializePollingConfigListView() {
        slideListViewAdapter = new SlideListViewAdapter(this, existListItems);
        slideListViewAdapter.setLableClickListener(labelClickListener);
        slideListViewAdapter.setContentClickListener(contentClickListener);
        slideListViewAdapter.setDeleteClickListener(deleteClickListener);
        slideListViewAdapter.setAddClickListener(addClickListener);
        slideMenuListView = (SlideMenuListView)findViewById(R.id.lv_polling_config);
        slideMenuListView.setAdapter(slideListViewAdapter);
        slideMenuListView.setOnItemClickListener(slideListViewAdapter);
    }

    private void generateExistListItemsFromPollingProjectConfigs() {
        if (projectConfigs != null) {
            existListItems = new ArrayList<>();
            for (PollingProjectConfig projectConfig :
                    projectConfigs) {
                PollingConfigListItemProjectEntity projectEntity = new PollingConfigListItemProjectEntity(this, projectConfig);
                projectEntity.setState(PollingConfigState.PCS_INVARIANT);
                existListItems.add(projectEntity);
            }
            existListItems.add(new PollingConfigListItemProjectVirtual(this));
        }
    }

    private void importPollingConfig() {
        projectConfigs = (List<PollingProjectConfig>)getArgument(ArgumentTag.AT_LIST_PROJECT_CONFIG);
        if (projectConfigs == null) {
            projectConfigs = new ArrayList<>();
        }
    }

    private void foldProjectEntity(PollingConfigListItemProjectEntity projectEntity) {
        for (int unfoldIndex = 0;unfoldIndex < projectEntity.getMissionSize();++unfoldIndex) {
            PollingConfigListItem mission = projectEntity.getMission(unfoldIndex);
            if (mission.isEntity()) {
                PollingConfigListItemMissionEntity missionEntity = (PollingConfigListItemMissionEntity)mission;
                if (missionEntity.isUnfold()) {
                    foldMissionEntity(missionEntity);
                }
            }
            existListItems.remove(mission);
        }
    }

    private void unfoldProjectEntity(PollingConfigListItemProjectEntity projectEntity) {
        for (int itemIndex = existListItems.indexOf(projectEntity) + 1, unfoldIndex = 0; unfoldIndex < projectEntity.getMissionSize(); ++unfoldIndex) {
            PollingConfigListItem mission = projectEntity.getMission(unfoldIndex);
            existListItems.add(itemIndex + unfoldIndex, mission);
            if (mission.isEntity()) {
                PollingConfigListItemMissionEntity missionEntity = (PollingConfigListItemMissionEntity)mission;
                if (missionEntity.isUnfold()) {
                    unfoldMissionEntity(missionEntity);
                    itemIndex += missionEntity.getItemSize();
                }
            }
        }
    }

    private void foldMissionEntity(PollingConfigListItemMissionEntity missionEntity) {
        for (int itemIndex = existListItems.indexOf(missionEntity) + 1, unfoldIndex = 0; unfoldIndex < missionEntity.getItemSize(); ++unfoldIndex) {
            existListItems.remove(itemIndex);
        }
    }

    private void unfoldMissionEntity(PollingConfigListItemMissionEntity missionEntity) {
        for (int itemIndex = existListItems.indexOf(missionEntity) + 1, unfoldIndex = 0; unfoldIndex < missionEntity.getItemSize(); ++unfoldIndex) {
            existListItems.add(itemIndex + unfoldIndex, missionEntity.getItem(unfoldIndex));
        }
    }

    private PollingConfigListItemEntity getImmediateBoss(PollingConfigListItem listItem) {
        PollingConfigListItemEntity result = null;
        int levelThreshold = listItem.isEntity() ? 1 : 2;
        for (int startIndex = existListItems.indexOf(listItem) - 1; startIndex >= 0; --startIndex) {
            PollingConfigListItem tmp = existListItems.get(startIndex);
            if (tmp.getType().getLevel() > listItem.getType().getLevel() + levelThreshold) {
                result = (PollingConfigListItemEntity)tmp;
                break;
            }
        }
        return result;
    }

    public void addOrModifyPollingConfig(PollingConfigListItem listItem) {
        putArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_CURRENT_CLICKED, listItem);
        switch (listItem.getType()) {
            case PCLIT_PROJECT_ENTITY:
            case PCLIT_PROJECT_VIRTUAL: {
                putArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY, getCurrentProjectEntities());
                startPollingConfig(PollingProjectConfigActivity.class, listItem.getType().getLevel());
            } break;
            case PCLIT_MISSION_ENTITY:
            case PCLIT_MISSION_VIRTUAL: {
                putArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY, getCurrentProjectEntities());
                startPollingConfig(PollingMissionConfigActivity.class, listItem.getType().getLevel());
            } break;
            case PCLIT_ITEM_ENTITY:
            case PCLIT_ITEM_VIRTUAL: {
                startPollingConfig(PollingItemConfigActivity.class, listItem.getType().getLevel());
            } break;
        }
    }

    private void startPollingConfig(Class<?> activityClass, int level) {
        startActivityForResult(new Intent(this, activityClass), level);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PollingConfigListItem listItem = (PollingConfigListItem)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_CURRENT_CLICKED);
            if (listItem.isEntity()) {
                if (((PollingConfigListItemEntity)listItem).getState() == PollingConfigState.PCS_MODIFIED) {
                    slideListViewAdapter.notifyDataSetChanged();
                }
            } else {
                switch (PollingConfigListItemType.from(requestCode)) {
                    case PCLIT_ITEM_VIRTUAL: {
                        PollingConfigListItemMissionEntity itemFather = (PollingConfigListItemMissionEntity)getImmediateBoss(listItem);
                        PollingConfigListItemItemEntity newItemEntity = (PollingConfigListItemItemEntity)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW);
                        itemFather.addItem(newItemEntity);
                        itemFather.getMissionConfig().getItems().add(newItemEntity.getItemConfig());
                    } break;
                    case PCLIT_MISSION_VIRTUAL: {
                        PollingConfigListItemProjectEntity missionFather = (PollingConfigListItemProjectEntity)getImmediateBoss(listItem);
                        PollingConfigListItemMissionEntity newMissionEntity = (PollingConfigListItemMissionEntity)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW);
                        missionFather.addMission(newMissionEntity);
                        missionFather.getProjectConfig().getMissions().add(newMissionEntity.getMissionConfig());
                    } break;
                    case PCLIT_PROJECT_VIRTUAL: {
                        projectConfigs.add(((PollingConfigListItemProjectEntity)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW)).getProjectConfig());
                    } break;
                }
                existListItems.add(existListItems.indexOf(listItem), (PollingConfigListItemEntity) getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW));
                slideListViewAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private List<PollingConfigListItemProjectEntity> getCurrentProjectEntities(){
        List<PollingConfigListItemProjectEntity> result = new ArrayList<>();
        for (PollingConfigListItem listItem:
                existListItems) {
            if (listItem.getType() == PollingConfigListItemType.PCLIT_PROJECT_ENTITY) {
                result.add((PollingConfigListItemProjectEntity) listItem);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (isPollingConfigModified()) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(getString(R.string.ui_prompt_polling_config_modified));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ui_prompt_yes), exitAlertListener);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ui_prompt_no), exitAlertListener);
            dialog.show();
        } else {
            exitPollingConfig();
        }
    }

    private void exitPollingConfig() {
        Intent intent = getIntent();
        intent.putExtra(ArgumentTag.PROJECT_CONFIG_CHANGED, pollingConfigModified);
        intent.putExtra(ArgumentTag.RESTORE_PROJECT_AND_SENSOR_CONFIG, isRestoreProjectAndSensorConfig);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private boolean isPollingConfigModified() {
        if (!desertListItems.isEmpty()) {
            return true;
        }

        for (PollingConfigListItemProjectEntity projectEntity:
                getCurrentProjectEntities()) {
            if (projectEntity.getState() != PollingConfigState.PCS_INVARIANT) {
                return true;
            }
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                PollingConfigListItemMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (missionEntity.getState() != PollingConfigState.PCS_INVARIANT) {
                    return true;
                }
                for (int itemIndex = 0;itemIndex < missionEntity.getItemEntitySize();++itemIndex) {
                    if (missionEntity.getItemEntity(itemIndex).getState() != PollingConfigState.PCS_INVARIANT) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void onPostExportPollingConfig() {
        if (isExitAfterExport) {
            exitPollingConfig();
        } else {
            updatePollingConfigs();
        }
    }
}
