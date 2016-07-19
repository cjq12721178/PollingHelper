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
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.SlideListViewAdapter;
import com.example.kat.pollinghelper.ui.listview.SlideMenuListView;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCell;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellItemEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellMissionEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellProjectEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellProjectVirtual;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellType;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellState;

import java.util.ArrayList;
import java.util.List;

public class ScoutConfigActivity extends ManagedActivity {

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
            //AlertDialog dialog = new AlertDialog.Builder(this).create();
            if (isPollingConfigModified()) {
//                dialog.setMessage(getString(R.string.ui_prompt_polling_config_modified));
//                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ui_prompt_yes), saveAlertListener);
//                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ui_prompt_no), saveAlertListener);
                showAlternativeDialog(R.string.ui_prompt_polling_config_modified,
                        onSaveConfirmClickListener);
            } else {
                //dialog.setMessage(getString(R.string.ui_prompt_polling_config_invariant));
                promptMessage(R.string.ui_prompt_polling_config_invariant);
            }
            //dialog.show();
        } else {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void updatePollingConfigs() {
        desertListItems.clear();
        for (ScoutCellProjectEntity projectEntity :
                getCurrentProjectEntities()) {
            if (projectEntity.getState() != ScoutCellState.PCS_INVARIANT) {
                projectEntity.setName(projectEntity.getProjectConfig().getName());
                projectEntity.setState(ScoutCellState.PCS_INVARIANT);
            }
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                ScoutCellMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (missionEntity.getState() != ScoutCellState.PCS_INVARIANT) {
                    missionEntity.setName(missionEntity.getMissionConfig().getName());
                    missionEntity.setState(ScoutCellState.PCS_INVARIANT);
                }
                for (int itemIndex = 0;itemIndex < missionEntity.getItemEntitySize();++itemIndex) {
                    missionEntity.getItemEntity(itemIndex).setState(ScoutCellState.PCS_INVARIANT);
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
            for (ScoutProjectConfig projectConfig :
                    projectConfigs) {
                ScoutCellProjectEntity projectEntity = new ScoutCellProjectEntity(this, projectConfig);
                projectEntity.setState(ScoutCellState.PCS_INVARIANT);
                existListItems.add(projectEntity);
            }
            existListItems.add(new ScoutCellProjectVirtual(this));
        }
    }

    private void importPollingConfig() {
        projectConfigs = (List<ScoutProjectConfig>)getArgument(ArgumentTag.AT_LIST_PROJECT_CONFIG);
        if (projectConfigs == null) {
            projectConfigs = new ArrayList<>();
        }
    }

    private void foldProjectEntity(ScoutCellProjectEntity projectEntity) {
        for (int unfoldIndex = 0;unfoldIndex < projectEntity.getMissionSize();++unfoldIndex) {
            ScoutCell mission = projectEntity.getMission(unfoldIndex);
            if (mission.isEntity()) {
                ScoutCellMissionEntity missionEntity = (ScoutCellMissionEntity)mission;
                if (missionEntity.isUnfold()) {
                    foldMissionEntity(missionEntity);
                }
            }
            existListItems.remove(mission);
        }
    }

    private void unfoldProjectEntity(ScoutCellProjectEntity projectEntity) {
        for (int itemIndex = existListItems.indexOf(projectEntity) + 1, unfoldIndex = 0; unfoldIndex < projectEntity.getMissionSize(); ++unfoldIndex) {
            ScoutCell mission = projectEntity.getMission(unfoldIndex);
            existListItems.add(itemIndex + unfoldIndex, mission);
            if (mission.isEntity()) {
                ScoutCellMissionEntity missionEntity = (ScoutCellMissionEntity)mission;
                if (missionEntity.isUnfold()) {
                    unfoldMissionEntity(missionEntity);
                    itemIndex += missionEntity.getItemSize();
                }
            }
        }
    }

    private void foldMissionEntity(ScoutCellMissionEntity missionEntity) {
        for (int itemIndex = existListItems.indexOf(missionEntity) + 1, unfoldIndex = 0; unfoldIndex < missionEntity.getItemSize(); ++unfoldIndex) {
            existListItems.remove(itemIndex);
        }
    }

    private void unfoldMissionEntity(ScoutCellMissionEntity missionEntity) {
        for (int itemIndex = existListItems.indexOf(missionEntity) + 1, unfoldIndex = 0; unfoldIndex < missionEntity.getItemSize(); ++unfoldIndex) {
            existListItems.add(itemIndex + unfoldIndex, missionEntity.getItem(unfoldIndex));
        }
    }

    private ScoutCellEntity getImmediateBoss(ScoutCell listItem) {
        ScoutCellEntity result = null;
        int levelThreshold = listItem.isEntity() ? 1 : 2;
        for (int startIndex = existListItems.indexOf(listItem) - 1; startIndex >= 0; --startIndex) {
            ScoutCell tmp = existListItems.get(startIndex);
            if (tmp.getType().getLevel() > listItem.getType().getLevel() + levelThreshold) {
                result = (ScoutCellEntity)tmp;
                break;
            }
        }
        return result;
    }

    public void addOrModifyPollingConfig(ScoutCell listItem) {
        putArgument(ArgumentTag.AT_SCOUT_CELL_CURRENT_CLICKED, listItem);
        switch (listItem.getType()) {
            case PCLIT_PROJECT_ENTITY:
            case PCLIT_PROJECT_VIRTUAL: {
                putArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY, getCurrentProjectEntities());
                startPollingConfig(ScoutProjectConfigActivity.class, listItem.getType().getLevel());
            } break;
            case PCLIT_MISSION_ENTITY:
            case PCLIT_MISSION_VIRTUAL: {
                putArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY, getCurrentProjectEntities());
                startPollingConfig(ScoutMissionConfigActivity.class, listItem.getType().getLevel());
            } break;
            case PCLIT_ITEM_ENTITY:
            case PCLIT_ITEM_VIRTUAL: {
                startPollingConfig(ScoutItemConfigActivity.class, listItem.getType().getLevel());
            } break;
        }
    }

    private void startPollingConfig(Class<?> activityClass, int level) {
        startActivityForResult(new Intent(this, activityClass), level);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ScoutCell listItem = (ScoutCell)getArgument(ArgumentTag.AT_SCOUT_CELL_CURRENT_CLICKED);
            if (listItem.isEntity()) {
                if (((ScoutCellEntity)listItem).getState() != ScoutCellState.PCS_INVARIANT) {
                    slideListViewAdapter.notifyDataSetChanged();
                }
            } else {
                ScoutCellEntity newEntity = (ScoutCellEntity)getArgument(ArgumentTag.AT_SCOUT_ENTITY_FEEDBACK);
                if (newEntity != null) {
                    switch (ScoutCellType.from(requestCode)) {
                        case PCLIT_ITEM_VIRTUAL: {
                            ScoutCellMissionEntity itemFather = (ScoutCellMissionEntity)getImmediateBoss(listItem);
                            ScoutCellItemEntity newItemEntity = (ScoutCellItemEntity)newEntity;
                            itemFather.addItem(newItemEntity);
                            itemFather.getMissionConfig().getItems().add(newItemEntity.getItemConfig());
                        } break;
                        case PCLIT_MISSION_VIRTUAL: {
                            ScoutCellProjectEntity missionFather = (ScoutCellProjectEntity)getImmediateBoss(listItem);
                            ScoutCellMissionEntity newMissionEntity = (ScoutCellMissionEntity)newEntity;
                            missionFather.addMission(newMissionEntity);
                            missionFather.getProjectConfig().getMissions().add(newMissionEntity.getMissionConfig());
                        } break;
                        case PCLIT_PROJECT_VIRTUAL: {
                            projectConfigs.add(((ScoutCellProjectEntity)newEntity).getProjectConfig());
                        } break;
                    }
                    existListItems.add(existListItems.indexOf(listItem), newEntity);
                    slideListViewAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private List<ScoutCellProjectEntity> getCurrentProjectEntities(){
        List<ScoutCellProjectEntity> result = new ArrayList<>();
        for (ScoutCell listItem:
                existListItems) {
            if (listItem.getType() == ScoutCellType.PCLIT_PROJECT_ENTITY) {
                result.add((ScoutCellProjectEntity) listItem);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (isPollingConfigModified()) {
            showAlternativeDialog(R.string.ui_prompt_polling_config_modified,
                    onExitConfirmClickListener, onExitCancelClickListener);
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

        for (ScoutCellProjectEntity projectEntity:
                getCurrentProjectEntities()) {
            if (projectEntity.getState() != ScoutCellState.PCS_INVARIANT) {
                return true;
            }
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                ScoutCellMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (missionEntity.getState() != ScoutCellState.PCS_INVARIANT) {
                    return true;
                }
                for (int itemIndex = 0;itemIndex < missionEntity.getItemEntitySize();++itemIndex) {
                    if (missionEntity.getItemEntity(itemIndex).getState() != ScoutCellState.PCS_INVARIANT) {
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

    private SlideListViewAdapter.OnTextViewClickListener labelClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, ScoutCell listItem) {
            if (listItem.isEntity()) {
                if (listItem.getType() == ScoutCellType.PCLIT_PROJECT_ENTITY) {
                    shrinkProjectEntity((ScoutCellProjectEntity)listItem);
                    slideListViewAdapter.notifyDataSetChanged();
                } else if (listItem.getType() == ScoutCellType.PCLIT_MISSION_ENTITY) {
                    shrinkMissionEntity((ScoutCellMissionEntity)listItem);
                    slideListViewAdapter.notifyDataSetChanged();
                }
            }
        }

        private void shrinkProjectEntity(ScoutCellProjectEntity projectEntity) {
            if (projectEntity.isUnfold()) {
                foldProjectEntity(projectEntity);
            } else {
                unfoldProjectEntity(projectEntity);
            }
            projectEntity.setUnfold(!projectEntity.isUnfold());
        }

        private void shrinkMissionEntity(ScoutCellMissionEntity missionEntity) {
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
        public void onClick(View view, ScoutCell listItem) {
            if (listItem.isEntity()) {
                if (listItem.getType() == ScoutCellType.PCLIT_PROJECT_ENTITY) {
                    deletePollingProjectConfig((ScoutCellProjectEntity)listItem);
                } else if (listItem.getType() == ScoutCellType.PCLIT_MISSION_ENTITY) {
                    deletePollingMissionConfig((ScoutCellMissionEntity) listItem);
                } else {
                    deletePollingItemConfig((ScoutCellItemEntity) listItem);
                }
                slideListViewAdapter.notifyDataSetChanged();
            }
        }

        private void deletePollingProjectConfig(ScoutCellProjectEntity projectEntity) {
            if (projectEntity.isUnfold()) {
                foldProjectEntity(projectEntity);
            }
            projectConfigs.remove(projectEntity.getProjectConfig());
            existListItems.remove(projectEntity);
            desertListItems.add(projectEntity);
        }

        private void deletePollingMissionConfig(ScoutCellMissionEntity missionEntity) {
            if (missionEntity.isUnfold()) {
                foldMissionEntity(missionEntity);
            }
            ScoutCellProjectEntity father = (ScoutCellProjectEntity) getImmediateBoss(missionEntity);
            father.removeMission(missionEntity);
            father.getProjectConfig().getMissions().remove(missionEntity.getMissionConfig());
            existListItems.remove(missionEntity);
            desertListItems.add(missionEntity);
        }

        private void deletePollingItemConfig(ScoutCellItemEntity itemEntity) {
            ScoutCellMissionEntity father = (ScoutCellMissionEntity) getImmediateBoss(itemEntity);
            father.removeItem(itemEntity);
            father.getMissionConfig().getItems().remove(itemEntity.getItemConfig());
            existListItems.remove(itemEntity);
            desertListItems.add(itemEntity);
        }
    };

    private SlideListViewAdapter.OnTextViewClickListener addClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, ScoutCell listItem) {
            addOrModifyPollingConfig(listItem);
        }
    };

    private SlideListViewAdapter.OnTextViewClickListener contentClickListener = new SlideListViewAdapter.OnTextViewClickListener() {
        @Override
        public void onClick(View view, ScoutCell listItem) {
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

    private View.OnClickListener onExitConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pollingConfigModified = true;
            isRestoreProjectAndSensorConfig = false;
            isExitAfterExport = true;
            exportPollingConfigs();
        }
    };

    private View.OnClickListener onExitCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isRestoreProjectAndSensorConfig = true;
            exitPollingConfig();
        }
    };

    private View.OnClickListener onSaveConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isExitAfterExport = false;
            exportPollingConfigs();
            pollingConfigModified = true;
        }
    };

    private SlideListViewAdapter slideListViewAdapter;
    private List<ScoutProjectConfig> projectConfigs;
    private List<ScoutCell> existListItems;
    private SlideMenuListView slideMenuListView;
    private List<ScoutCellEntity> desertListItems;
    private boolean pollingConfigModified;
    private boolean isRestoreProjectAndSensorConfig;
    private boolean isExitAfterExport;
}
