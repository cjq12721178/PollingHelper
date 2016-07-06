package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;

/**
 * Created by KAT on 2016/5/18.
 */
public class PollingConfigListItemItemEntity extends PollingConfigListItemEntity {
    public PollingConfigListItemItemEntity(Context context, PollingItemConfig itemConfig) {
        super(context, PollingConfigListItemType.PCLIT_ITEM_ENTITY, itemConfig.getMeasureName());
        this.itemConfig = itemConfig;
    }

    @Override
    public String getLableHeader() {
        return null;
    }

    @Override
    public String getLable() {
        return context.getString(R.string.ui_li_item_entity);
    }

    @Override
    public String getContent() {
        return itemConfig.getMeasureName();
    }

    public PollingItemConfig getItemConfig() {
        return itemConfig;
    }

    private PollingItemConfig itemConfig;
}
