package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/5/18.
 */
public class PollingConfigListItemProjectEntity extends PollingConfigListItemEntity {

    public PollingConfigListItemProjectEntity(Context context, PollingProjectConfig projectConfig) {
        super(context, PollingConfigListItemType.PCLIT_PROJECT_ENTITY, projectConfig.getName());
        this.projectConfig = projectConfig;
        missionVirtual = new PollingConfigListItemMissionVirtual(context);
        autoGenerateMissions();
    }

    private void autoGenerateMissions() {
        missionEntities = new ArrayList<>();
        for (PollingMissionConfig missionConfig :
                projectConfig.getMissions()) {
            PollingConfigListItemMissionEntity missionEntity = new PollingConfigListItemMissionEntity(context, missionConfig);
            missionEntity.setState(PollingConfigState.PCS_INVARIANT);
            missionEntities.add(missionEntity);
        }
    }

    @Override
    public String getLableHeader() {
        return context.getString(R.string.ui_li_project_entity);
    }

    @Override
    public String getContent() {
        return projectConfig.getName();
    }

    public PollingProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public int getMissionSize() {
        return getMissionEntitySize() + 1;
    }

    public int getMissionEntitySize() {
        return missionEntities.size();
    }

    public PollingConfigListItem getMission(int index) {
        PollingConfigListItem result = null;
        if (index == missionEntities.size()) {
            result =  missionVirtual;
        } else if (index >= 0 && index < missionEntities.size()) {
            result = getMissionEntity(index);
        }
        return result;
    }

    public PollingConfigListItemMissionEntity getMissionEntity(int index) {
        return missionEntities.get(index);
    }

    public void addMission(PollingConfigListItemMissionEntity missionEntity) {
        missionEntities.add(missionEntity);
    }

    public boolean removeMission(PollingConfigListItemMissionEntity missionEntity) {
        return missionEntities.remove(missionEntity);
    }

    public PollingConfigListItemMissionEntity removeMission(int index) {
        return missionEntities.remove(index);
    }

    private List<PollingConfigListItemMissionEntity> missionEntities;
    private PollingConfigListItemMissionVirtual missionVirtual;
    private PollingProjectConfig projectConfig;
}