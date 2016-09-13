package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.bean.warn.MissionWarnInfo;
import com.example.kat.pollinghelper.utility.Converter;

import java.util.List;

/**
 * Created by KAT on 2016/9/12.
 */
public class MissionWarnAdapter extends RecyclerView.Adapter<MissionWarnAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(View v, int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView deviceImage;
        public TextView missionName;
        public OnClickListenerImp listenerImp;

        public ViewHolder(View itemView, OnClickListenerImp listenerImp) {
            super(itemView);
            deviceImage = (ImageView)itemView.findViewById(R.id.iv_mission_device);
            missionName = (TextView)itemView.findViewById(R.id.tv_mission_name);
            this.listenerImp = listenerImp;
            itemView.setOnClickListener(listenerImp);
        }
    }

    private class OnClickListenerImp implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            currentSelectedIndex = position;
            notifyDataSetChanged();
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, position);
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private int position;
    }

    public MissionWarnAdapter(Context context,
                              List<MissionWarnInfo> missionWarnInfoList) {
        VIEW_BACKGROUND_COLOR_NORMAL = context.getResources().getColor(R.color.transparent_total);
        VIEW_BACKGROUND_COLOR_SELECTED = context.getResources().getColor(R.color.transparent_blue);
        this.missionWarnInfoList = missionWarnInfoList;
        currentSelectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(),
                R.layout.recycle_item_mission_warn, null),
                new OnClickListenerImp());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScoutMissionConfig missionConfig = missionWarnInfoList.get(position).getMissionConfig();
        holder.deviceImage.setImageBitmap(Converter.byteArray2Bitmap(missionConfig.getDeviceImageData()));
        holder.deviceImage.setBackgroundColor(position == currentSelectedIndex ? VIEW_BACKGROUND_COLOR_SELECTED : VIEW_BACKGROUND_COLOR_NORMAL);
        holder.missionName.setText(missionConfig.getName());
        holder.listenerImp.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return missionWarnInfoList != null ? missionWarnInfoList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        onItemClickListener = l;
    }

    public MissionWarnInfo getCurrentSelectedMissionWarnInfo() {
        return (missionWarnInfoList != null &&
                currentSelectedIndex >= 0 &&
                currentSelectedIndex < missionWarnInfoList.size()) ?
                missionWarnInfoList.get(currentSelectedIndex) :
                null;
    }

    public void setDefaultSelectedItem(int position) {
        if (missionWarnInfoList == null ||
                position < 0 ||
                position >= missionWarnInfoList.size()) {
            currentSelectedIndex = -1;
        } else {
            currentSelectedIndex = position;
        }
    }

    private final List<MissionWarnInfo> missionWarnInfoList;
    private final int VIEW_BACKGROUND_COLOR_NORMAL;
    private final int VIEW_BACKGROUND_COLOR_SELECTED;
    private OnItemClickListener onItemClickListener;
    private int currentSelectedIndex;
}
