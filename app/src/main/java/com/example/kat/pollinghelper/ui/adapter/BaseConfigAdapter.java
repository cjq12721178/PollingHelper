package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellClause;

import java.util.List;

/**
 * Created by KAT on 2016/5/27.
 */
public class BaseConfigAdapter extends BaseAdapter {
    private class ViewHolder {
        private TextView label;
        private TextView content;
    }

    public BaseConfigAdapter(Context context, List<ScoutCellClause> itemClauses) {
        this.itemClauses = itemClauses;
        itemConfigInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemClauses.size();
    }

    @Override
    public ScoutCellClause getItem(int position) {
        return itemClauses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = itemConfigInflater.inflate(R.layout.listitem_polling_config_double_textview, null);
            viewHolder = new ViewHolder();
            viewHolder.label = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
            viewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ScoutCellClause currentItemClause = getItem(position);
        viewHolder.label.setText(currentItemClause.getLabel());
        viewHolder.content.setText(currentItemClause.getContentString());
        if (currentItemClause.isModified()) {
            viewHolder.label.getPaint().setFakeBoldText(true);
            viewHolder.content.getPaint().setFakeBoldText(true);
        }
        return convertView;
    }

    private List<ScoutCellClause> itemClauses;
    private LayoutInflater itemConfigInflater;
}
