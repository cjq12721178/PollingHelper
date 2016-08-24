package com.example.kat.pollinghelper.structure.scout;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/5/18.
 */
public class ScoutCellProjectEntity extends ScoutCellEntity {

    public ScoutCellProjectEntity(Context context, ScoutProjectConfig projectConfig) {
        super(context, ScoutCellType.PCLIT_PROJECT_ENTITY, projectConfig.getName());
        this.projectConfig = projectConfig;
        missionVirtual = new ScoutCellMissionVirtual(context);
        autoGenerateMissions();
    }

    private void autoGenerateMissions() {
        missionEntities = new ArrayList<>();
        for (ScoutMissionConfig missionConfig :
                projectConfig.getMissions()) {
            ScoutCellMissionEntity missionEntity = new ScoutCellMissionEntity(context, missionConfig);
            missionEntity.setState(ScoutCellState.PCS_INVARIANT);
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

    public ScoutProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public int getMissionSize() {
        return getMissionEntitySize() + 1;
    }

    public int getMissionEntitySize() {
        return missionEntities.size();
    }

    public ScoutCell getMission(int index) {
        ScoutCell result = null;
        if (index == missionEntities.size()) {
            result =  missionVirtual;
        } else if (index >= 0 && index < missionEntities.size()) {
            result = getMissionEntity(index);
        }
        return result;
    }

    public ScoutCellMissionEntity getMissionEntity(int index) {
        return missionEntities.get(index);
    }

    public void addMission(ScoutCellMissionEntity missionEntity) {
        missionEntities.add(missionEntity);
    }

    public boolean removeMission(ScoutCellMissionEntity missionEntity) {
        return missionEntities.remove(missionEntity);
    }

    public ScoutCellMissionEntity removeMission(int index) {
        return missionEntities.remove(index);
    }

    private List<ScoutCellMissionEntity> missionEntities;
    private ScoutCellMissionVirtual missionVirtual;
    private ScoutProjectConfig projectConfig;
}