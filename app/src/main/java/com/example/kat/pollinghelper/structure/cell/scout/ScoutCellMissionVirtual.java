package com.example.kat.pollinghelper.structure.cell.scout;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public class ScoutCellMissionVirtual extends ScoutCellVirtual {
    public ScoutCellMissionVirtual(Context context) {
        super(context, ScoutCellType.PCLIT_MISSION_VIRTUAL);
    }

    @Override
    public String getContent() {
        return context.getString(R.string.ui_li_mission_virtual);
    }
}
