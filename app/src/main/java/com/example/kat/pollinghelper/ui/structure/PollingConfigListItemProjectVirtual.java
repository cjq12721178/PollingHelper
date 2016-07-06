package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public class PollingConfigListItemProjectVirtual extends PollingConfigListItemVirtual {
    public PollingConfigListItemProjectVirtual(Context context) {
        super(context, PollingConfigListItemType.PCLIT_PROJECT_VIRTUAL);
    }

    @Override
    public String getContent() {
        return context.getString(R.string.ui_li_project_virtual);
    }
}
