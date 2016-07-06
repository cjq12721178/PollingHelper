package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

/**
 * Created by KAT on 2016/5/18.
 */
public abstract class PollingConfigListItemVirtual extends PollingConfigListItem {
    public PollingConfigListItemVirtual(Context context, PollingConfigListItemType type) {
        super(context, false, type);
    }

    @Override
    public String getLable() {
        return null;
    }
}
