package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.kat.pollinghelper.structure.main_interface.FunctionListItem;
import com.example.kat.pollinghelper.structure.main_interface.FunctionType;

import java.util.List;

/**
 * Created by KAT on 2016/7/5.
 */
public class FunctionListAdapter extends BaseAdapter {

    public FunctionListAdapter(Context context, List<FunctionListItem> dataSource) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        functionList = dataSource;
    }

    @Override
    public int getCount() {
        return functionList.size();
    }

    @Override
    public FunctionListItem getItem(int position) {
        return functionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(layoutInflater, convertView);
    }

    @Override
    public int getViewTypeCount() {
        return FunctionType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return functionList.get(position).getType().ordinal();
    }

    private final List<FunctionListItem> functionList;
    private final Context context;
    private final LayoutInflater layoutInflater;
}
