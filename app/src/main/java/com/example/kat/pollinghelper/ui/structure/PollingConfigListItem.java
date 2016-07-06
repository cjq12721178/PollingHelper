package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public abstract class PollingConfigListItem {
    public PollingConfigListItem(Context context, boolean entity, PollingConfigListItemType type) {
        this.context = context;
        this.entity = entity;
        this.type = type;
    }

    public abstract String getLable();

    public abstract String getContent();

    public String getHideMenuLable() {
        return context.getString(R.string.ui_li_delete);
    }

    public PollingConfigListItemType getType() {
        return type;
    }

    public boolean isEntity() {
        return entity;
    }

    private boolean entity;
    protected final Context context;
    private PollingConfigListItemType type;
}
