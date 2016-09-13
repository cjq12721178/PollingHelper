package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.scout.ScoutCellSensorEntity;

import java.util.List;

/**
 * Created by KAT on 2016/6/8.
 */
public class SensorEntityAdapter extends BaseAdapter {

    public interface OnItemCheckClickedListener {
        void onClicked(CheckBox checkBox, int position);
    }

    private class ViewHolder {
        private TextView name;
        private CheckBox choice;
    }

    public SensorEntityAdapter(Context context, List<ScoutCellSensorEntity> sensorEntities) {
        inflater = LayoutInflater.from(context);
        this.sensorEntities = sensorEntities;
        inDeleteChoiceState = false;
    }

    @Override
    public int getCount() {
        return sensorEntities.size();
    }

    @Override
    public ScoutCellSensorEntity getItem(int position) {
        return sensorEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_sensor_config, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.tv_sensor_name);
            viewHolder.choice = (CheckBox) convertView.findViewById(R.id.chk_delete_choice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ScoutCellSensorEntity sensorEntity = getItem(position);
        viewHolder.name.setText(sensorEntity.getSensorConfig().getName());
        viewHolder.choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCheckClickedListener.onClicked((CheckBox)v, position);
            }
        });
        if (inDeleteChoiceState) {
            viewHolder.choice.setVisibility(View.VISIBLE);
            viewHolder.choice.setChecked(sensorEntity.isChecked());
        } else {
            viewHolder.choice.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public boolean isInDeleteChoiceState() {
        return inDeleteChoiceState;
    }

    public void setInDeleteChoiceState(boolean inDeleteChoiceState) {
        this.inDeleteChoiceState = inDeleteChoiceState;
    }

    public void setOnItemCheckClickedListener(OnItemCheckClickedListener onItemCheckClickedListener) {
        this.onItemCheckClickedListener = onItemCheckClickedListener;
    }

    private OnItemCheckClickedListener onItemCheckClickedListener;
    private boolean inDeleteChoiceState;
    private LayoutInflater inflater;
    private List<ScoutCellSensorEntity> sensorEntities;
}
