package com.example.kat.pollinghelper.ui.structure;

import android.content.Context;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/5/18.
 */
public abstract class PollingConfigListItemEntity extends PollingConfigListItem {

    public PollingConfigListItemEntity(Context context, PollingConfigListItemType type, String name) {
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

    public PollingConfigState getState() {
        return state;
    }

    public void setState(PollingConfigState state) {
        this.state = state;
    }

    public void setName(String name) {
        //注意该方法只能用于state == PCS_NEW
        this.name = name;
    }

    private PollingConfigState state;
    private String name;
    private boolean unfold;
}