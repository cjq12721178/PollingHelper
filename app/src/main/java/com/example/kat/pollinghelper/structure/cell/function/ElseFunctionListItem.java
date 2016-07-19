package com.example.kat.pollinghelper.structure.cell.function;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/7/5.
 */
public class ElseFunctionListItem extends FunctionListItem {

    private class ViewHolder {
        private TextView label;
    }

    public ElseFunctionListItem(String label) {
        super(FunctionType.FT_ELSE);
        this.label = label;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_else_function, null);
            viewHolder = new ViewHolder();
            viewHolder.label = (TextView)convertView.findViewById(R.id.tv_else_function_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.label.setText(label);
        return convertView;
    }

    private String label;
}
