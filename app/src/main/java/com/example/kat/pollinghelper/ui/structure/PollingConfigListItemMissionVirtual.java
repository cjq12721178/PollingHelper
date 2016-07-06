package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public class PollingConfigListItemMissionVirtual extends PollingConfigListItemVirtual {
    public PollingConfigListItemMissionVirtual(Context context) {
        super(context, PollingConfigListItemType.PCLIT_MISSION_VIRTUAL);
    }

    @Override
    public String getContent() {
        return context.getString(R.string.ui_li_mission_virtual);
    }
}
