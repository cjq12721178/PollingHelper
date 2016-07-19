package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellClause;
import com.example.kat.pollinghelper.utility.Converter;

import java.util.List;

/**
 * Created by KAT on 2016/7/18.
 */
public class MissionConfigAdapter extends BaseAdapter {

    private enum MissionAdapterLayoutType {
        MALT_DOUBLE_TEXTVIEW,
        MALT_TEXTVIEW_IMAGEVIEW
    }

    private class TextViewHolder {
        private TextView lable;
        private TextView content;
    }

    private class ImageViewHolder {
        private TextView lable;
        private ImageView content;
    }

    public MissionConfigAdapter(Context context, List<ScoutCellClause> clauses) {
        this.context = context;
        missionConfigInflater = LayoutInflater.from(context);
        this.clauses = clauses;
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
        return MissionAdapterLayoutType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getLabel() != context.getString(R.string.ui_tv_mission_config_label_device_image) ?
                MissionAdapterLayoutType.MALT_DOUBLE_TEXTVIEW.ordinal() : MissionAdapterLayoutType.MALT_TEXTVIEW_IMAGEVIEW.ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScoutCellClause currentItemClause = getItem(position);
        if (getItemViewType(position) == MissionAdapterLayoutType.MALT_DOUBLE_TEXTVIEW.ordinal()) {
            final TextViewHolder textViewHolder;
            if (convertView == null) {
                convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_double_textview, null);
                textViewHolder = new TextViewHolder();
                textViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                textViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                convertView.setTag(textViewHolder);
            } else {
                textViewHolder = (TextViewHolder)convertView.getTag();
            }
            textViewHolder.lable.setText(currentItemClause.getLabel());
            textViewHolder.content.setText(currentItemClause.getContentString());
            if (currentItemClause.isModified()) {
                textViewHolder.lable.getPaint().setFakeBoldText(true);
                textViewHolder.content.getPaint().setFakeBoldText(true);
            }
        } else {
            final ImageViewHolder imageViewHolder;
            if (convertView == null) {
                convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_textview_imageview, null);
                imageViewHolder = new ImageViewHolder();
                imageViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_mission_config_label);
                imageViewHolder.content = (ImageView)convertView.findViewById(R.id.iv_mission_config_device);
                convertView.setTag(imageViewHolder);
            } else {
                imageViewHolder = (ImageViewHolder)convertView.getTag();
            }
            imageViewHolder.lable.setText(currentItemClause.getLabel());
            if (currentItemClause.getContent() != null) {
                imageViewHolder.content.setImageBitmap(Converter.getBitmapFromByteArray((byte[])currentItemClause.getContent()));
            } else {
                imageViewHolder.content.setImageResource(R.drawable.ic_device_empty);
            }
            if (currentItemClause.isModified()) {
                imageViewHolder.lable.getPaint().setFakeBoldText(true);
            }
        }
        return convertView;
    }

    private List<ScoutCellClause> clauses;
    private LayoutInflater missionConfigInflater;
    private Context context;
}
