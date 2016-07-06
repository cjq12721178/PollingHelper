package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/5/18.
 */
public class PollingConfigListItemMissionEntity extends PollingConfigListItemEntity {
    public PollingConfigListItemMissionEntity(Context context, PollingMissionConfig missionConfig) {
        super(context, PollingConfigListItemType.PCLIT_MISSION_ENTITY, missionConfig.getName());
        this.missionConfig = missionConfig;
        itemVirtual = new PollingConfigListItemItemVirtual(context);
        autoGenerateItems();
    }

    private void autoGenerateItems() {
        itemEntities = new ArrayList<>();
        for (PollingItemConfig itemConfig :
                missionConfig.getItems()) {
            PollingConfigListItemItemEntity itemEntity = new PollingConfigListItemItemEntity(context, itemConfig);
            itemEntity.setState(PollingConfigState.PCS_INVARIANT);
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

    public PollingMissionConfig getMissionConfig() {
        return missionConfig;
    }

    public int getItemSize() {
        return getItemEntitySize() + 1;
    }

    public int getItemEntitySize() {
        return itemEntities.size();
    }

    public PollingConfigListItem getItem(int index) {
        PollingConfigListItem result = null;
        if (index == itemEntities.size()) {
            result = itemVirtual;
        } else if (index >= 0 && index < itemEntities.size()) {
            result = getItemEntity(index);
        }
        return result;
    }

    public PollingConfigListItemItemEntity getItemEntity(int index) {
        return itemEntities.get(index);
    }

    public void addItem(PollingConfigListItemItemEntity missionEntity) {
        itemEntities.add(missionEntity);
    }

    public boolean removeItem(PollingConfigListItemItemEntity missionEntity) {
        return itemEntities.remove(missionEntity);
    }

    public PollingConfigListItemItemEntity removeItem(int index) {
        return itemEntities.remove(index);
    }

    private List<PollingConfigListItemItemEntity> itemEntities;
    private PollingConfigListItemItemVirtual itemVirtual;
    private PollingMissionConfig missionConfig;
}
