package com.example.kat.pollinghelper.structure.cell.scout;

import android.content.Context;

/**
 * Created by KAT on 2016/5/18.
 */
public abstract class ScoutCellVirtual extends ScoutCell {
    public ScoutCellVirtual(Context context, ScoutCellType type) {
        super(context, false, type);
    }

    @Override
    public String getLable() {
        return null;
    }
}
