package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.scout.ScoutCellClause;

import java.util.List;

/**
 * Created by KAT on 2016/7/18.
 */
public class ProjectConfigAdapter extends BaseAdapter {

    public enum ProjectAdapterLayoutType {
        PALT_WITHOUT_ICON,
        PALT_WITH_ICON
    }

    private class NoIconViewHolder {
        private TextView label;
        private TextView content;
    }

    private class IconViewHolder {
        private TextView label;
        private TextView content;
        private ImageView delete;
    }

    public ProjectConfigAdapter(Context context, List<ScoutCellClause> clauses) {
        this.context = context;
        this.clauses = clauses;
        missionConfigInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return clauses.size();
    }

    @Override
    public ScoutCellClause getItem(int position) {
        return clauses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return ProjectAdapterLayoutType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getLabel() != context.getString(R.string.ui_tv_project_config_label_time) ?
                ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal() : ProjectAdapterLayoutType.PALT_WITH_ICON.ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScoutCellClause currentItemClause = getItem(position);
        if (getItemViewType(position) == ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal()) {
            final NoIconViewHolder noIconViewHolder;
            if (convertView == null) {
                convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_double_textview, null);
                noIconViewHolder = new NoIconViewHolder();
                noIconViewHolder.label = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                noIconViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                convertView.setTag(noIconViewHolder);
            } else {
                noIconViewHolder = (NoIconViewHolder)convertView.getTag();
            }
            noIconViewHolder.label.setText(currentItemClause.getLabel());
            noIconViewHolder.content.setText(currentItemClause.getContentString());
            if (currentItemClause.isModified()) {
                noIconViewHolder.label.getPaint().setFakeBoldText(true);
                noIconViewHolder.content.getPaint().setFakeBoldText(true);
            }
        } else {
            final IconViewHolder iconViewHolder;
            if (convertView == null) {
                convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_with_icon, null);
                iconViewHolder = new IconViewHolder();
                iconViewHolder.label = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                iconViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                iconViewHolder.delete = (ImageView)convertView.findViewById(R.id.iv_polling_time_delete);
                convertView.setTag(iconViewHolder);
            } else {
                iconViewHolder = (IconViewHolder)convertView.getTag();
            }
            iconViewHolder.label.setText(currentItemClause.getLabel() + String.valueOf(position - FixItemCount + 1));
            iconViewHolder.content.setText(currentItemClause.getContentString());
            iconViewHolder.delete.setImageResource(R.drawable.ic_time_delete);
            iconViewHolder.delete.setTag(position);
            iconViewHolder.delete.setOnClickListener(onTimeDeleteClickListener);
            if (currentItemClause.isModified()) {
                iconViewHolder.label.getPaint().setFakeBoldText(true);
                iconViewHolder.content.getPaint().setFakeBoldText(true);
            }
        }
        return convertView;
    }

    private View.OnClickListener onTimeDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clauses.remove((int)v.getTag());
            notifyDataSetChanged();
        }
    };

    private final int FixItemCount = 2;
    private LayoutInflater missionConfigInflater;
    private List<ScoutCellClause> clauses;
    private Context context;
}