package com.example.kat.pollinghelper.structure.cell.scout;

import android.content.Context;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;

/**
 * Created by KAT on 2016/5/18.
 */
public class ScoutCellItemEntity extends ScoutCellEntity {
    public ScoutCellItemEntity(Context context, ScoutItemConfig itemConfig) {
        super(context, ScoutCellType.PCLIT_ITEM_ENTITY, itemConfig.getMeasureName());
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

    public ScoutItemConfig getItemConfig() {
        return itemConfig;
    }

    private ScoutItemConfig itemConfig;
}
