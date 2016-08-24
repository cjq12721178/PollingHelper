package com.example.kat.pollinghelper.structure.scout;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public abstract class ScoutCellEntity extends ScoutCell implements ScoutEntity {

    public ScoutCellEntity(Context context, ScoutCellType type, String name) {
        super(context, true, type);
        unfold = false;
        this.name = name;
    }

    public abstract String getLableHeader();

    @Override
    public String getLable() {
        return getLableHeader() + getAppendix();
    }

    public String getAppendix() {
        return isUnfold() ? context.getString(R.string.ui_li_appendix_unfold) : context.getString(R.string.ui_li_appendix_fold);
    }

    public boolean isUnfold() {
        return unfold;
    }

    public String getName() {
        return name;
    }

    public void setUnfold(boolean unfold) {
        this.unfold = unfold;
    }

    public ScoutCellState getState() {
        return state;
    }

    public void setState(ScoutCellState state) {
        this.state = state;
    }

    public void setName(String name) {
        //注意该方法只能用于state == PCS_NEW
        this.name = name;
    }

    private ScoutCellState state;
    private String name;
    private boolean unfold;
}