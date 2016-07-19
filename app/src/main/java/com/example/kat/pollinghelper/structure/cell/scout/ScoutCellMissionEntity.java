package com.example.kat.pollinghelper.structure.cell.scout;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/5/18.
 */
public class ScoutCellMissionEntity extends ScoutCellEntity {
    public ScoutCellMissionEntity(Context context, ScoutMissionConfig missionConfig) {
        super(context, ScoutCellType.PCLIT_MISSION_ENTITY, missionConfig.getName());
        this.missionConfig = missionConfig;
        itemVirtual = new ScoutCellItemVirtual(context);
        autoGenerateItems();
    }

    private void autoGenerateItems() {
        itemEntities = new ArrayList<>();
        for (ScoutItemConfig itemConfig :
                missionConfig.getItems()) {
            ScoutCellItemEntity itemEntity = new ScoutCellItemEntity(context, itemConfig);
            itemEntity.setState(ScoutCellState.PCS_INVARIANT);
            itemEntities.add(itemEntity);
        }
    }

    @Override
    public String getLableHeader() {
        return context.getString(R.string.ui_li_mission_entity);
    }

    @Override
    public String getContent() {
        return missionConfig.getName();
    }

    public ScoutMissionConfig getMissionConfig() {
        return missionConfig;
    }

    public int getItemSize() {
        return getItemEntitySize() + 1;
    }

    public int getItemEntitySize() {
        return itemEntities.size();
    }

    public ScoutCell getItem(int index) {
        ScoutCell result = null;
        if (index == itemEntities.size()) {
            result = itemVirtual;
        } else if (index >= 0 && index < itemEntities.size()) {
            result = getItemEntity(index);
        }
        return result;
    }

    public ScoutCellItemEntity getItemEntity(int index) {
        return itemEntities.get(index);
    }

    public void addItem(ScoutCellItemEntity missionEntity) {
        itemEntities.add(missionEntity);
    }

    public boolean removeItem(ScoutCellItemEntity missionEntity) {
        return itemEntities.remove(missionEntity);
    }

    public ScoutCellItemEntity removeItem(int index) {
        return itemEntities.remove(index);
    }

    private List<ScoutCellItemEntity> itemEntities;
    private ScoutCellItemVirtual itemVirtual;
    private ScoutMissionConfig missionConfig;
}
