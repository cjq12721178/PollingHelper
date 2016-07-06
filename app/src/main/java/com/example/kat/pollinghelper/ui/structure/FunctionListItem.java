package com.example.kat.pollinghelper.ui.structure;

import android.view.LayoutInflater;
import android.view.View;

import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;

/**
 * Created by KAT on 2016/7/5.
 */
public abstract class FunctionListItem {

    public FunctionListItem(FunctionType type) {
        this.type = type;
    }

    public abstract View getView(LayoutInflater inflater, View convertView);

    public FunctionType getType() {
        return type;
    }

    private FunctionType type;
}
