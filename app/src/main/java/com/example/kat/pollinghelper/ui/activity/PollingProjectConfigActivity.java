package com.example.kat.pollinghelper.ui.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.dialog.EditDialog;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItem;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemClause;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemProjectEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PollingProjectConfigActivity extends ManagedActivity {

    private enum ProjectAdapterLayoutType {
        PALT_WITHOUT_ICON,
        PALT_WITH_ICON
    }

    private class ProjectConfigAdapter extends BaseAdapter {

        private class NoIconViewHolder {
            private TextView lable;
            private TextView content;
        }

        private class IconViewHolder {
            private TextView lable;
            private TextView content;
            private ImageView delete;
        }

        public ProjectConfigAdapter(Context context) {
            missionConfigInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemClauses.size();
        }

        @Override
        public PollingConfigListItemClause getItem(int position) {
            return itemClauses.get(position);
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
            return getItem(position).getLabel() != getString(R.string.ui_tv_project_config_label_time) ?
                    ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal() : ProjectAdapterLayoutType.PALT_WITH_ICON.ordinal();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PollingConfigListItemClause currentItemClause = getItem(position);
            if (getItemViewType(position) == ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal()) {
                final NoIconViewHolder noIconViewHolder;
                if (convertView == null) {
                    convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_double_textview, null);
                    noIconViewHolder = new NoIconViewHolder();
                    noIconViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                    noIconViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                    convertView.setTag(noIconViewHolder);
                } else {
                    noIconViewHolder = (NoIconViewHolder)convertView.getTag();
                }
                noIconViewHolder.lable.setText(currentItemClause.getLabel());
                noIconViewHolder.content.setText(currentItemClause.getContentString());
                if (currentItemClause.isModified()) {
                    noIconViewHolder.lable.getPaint().setFakeBoldText(true);
                    noIconViewHolder.content.getPaint().setFakeBoldText(true);
                }
            } else {
                final IconViewHolder iconViewHolder;
                if (convertView == null) {
                    convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_with_icon, null);
                    iconViewHolder = new IconViewHolder();
                    iconViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                    iconViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                    iconViewHolder.delete = (ImageView)convertView.findViewById(R.id.iv_polling_time_delete);
                    convertView.setTag(iconViewHolder);
                } else {
                    iconViewHolder = (IconViewHolder)convertView.getTag();
                }
                iconViewHolder.lable.setText(currentItemClause.getLabel() + String.valueOf(position - FixItemCount + 1));
                iconViewHolder.content.setText(currentItemClause.getContentString());
                iconViewHolder.delete.setImageResource(R.drawable.ic_time_delete);
                iconViewHolder.delete.setTag(position);
                iconViewHolder.delete.setOnClickListener(onTimeDeleteClickListener);
                if (currentItemClause.isModified()) {
                    iconViewHolder.lable.getPaint().setFakeBoldText(true);
                    iconViewHolder.content.getPaint().setFakeBoldText(true);
                }
            }
            return convertView;
        }

        private LayoutInflater missionConfigInflater;
    }

    private class ScheduledTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            currentItemClause.setContent(new SimpleTime(hourOfDay, minute));
            projectConfigAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_project_config);
        findViewById(R.id.btn_polling_config_time_add).setOnClickListener(onTimeAddClickListener);
    }

    @Override
    protected void onInitializeBusiness() {
        importProjectEntity();
        createEditDialog();
        initializeProjectConfigListView();
    }

    private void createEditDialog() {
        editDialog = new EditDialog();
        editDialog.setOnClickPositiveListener(onEditDialogPositiveClickListener);
    }

    private void initializeProjectConfigListView() {
        projectConfigAdapter = new ProjectConfigAdapter(this);
        ListView listView = (ListView)findViewById(R.id.lv_project_config);
        listView.setAdapter(projectConfigAdapter);
        listView.setOnItemClickListener(projectConfigClickListener);
        scheduledTimeSetListener = new ScheduledTimeSetListener();
    }

    private void importProjectEntity() {
        PollingConfigListItem listItem = (PollingConfigListItem)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_CURRENT_CLICKED);
        if (listItem.isEntity()) {
            projectEntity = (PollingConfigListItemProjectEntity) listItem;
        } else {
            projectEntity = new PollingConfigListItemProjectEntity(this, new PollingProjectConfig());
            projectEntity.setState(PollingConfigState.PCS_NEW);
        }

        //currentProjectEntities = operationInfo.getCurrentProjectEntities();
        currentProjectEntities = (List<PollingConfigListItemProjectEntity>)getArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY);

        itemClauses = new ArrayList<>();
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_project_config_label_name),
                projectEntity.getProjectConfig().getName()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_project_config_label_description),
                projectEntity.getProjectConfig().getDescription()));
        String pollingTime = getString(R.string.ui_tv_project_config_label_time);
        for (SimpleTime simpleTime :
                projectEntity.getProjectConfig().getScheduledTimes()) {
            itemClauses.add(new PollingConfigListItemClause(pollingTime, simpleTime));
        }
    }

    @Override
    public void onBackPressed() {
        if (projectEntity.getState() == PollingConfigState.PCS_UNKNOWN) {
            promptMessage(R.string.ui_prompt_project_config_unknown_error);
            setResult(RESULT_CANCELED);
        } else {
            if (!updateProjectEntity()) {
                return;
            }
            if (projectEntity.getState() == PollingConfigState.PCS_NEW) {
                //operationInfo.setNewListItemEntity(projectEntity);
                putArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW, projectEntity);
            }
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private boolean updateProjectEntity() {
        PollingConfigListItemClause projectNameClause = (PollingConfigListItemClause)getContent(R.string.ui_tv_project_config_label_name);
        if (projectNameClause.getContentString().isEmpty()) {
            promptMessage(R.string.ui_prompt_project_name_empty);
            return false;
        }
        if (isProjectNameRepetition(projectNameClause.getContentString())) {
            promptMessage(R.string.ui_prompt_project_name_repetition);
            return false;
        }

        List<PollingConfigListItemClause> scheduledTimes = (List<PollingConfigListItemClause>)getContent(R.string.ui_tv_project_config_label_time);
        if (scheduledTimes.isEmpty()) {
            promptMessage(R.string.ui_prompt_project_time_least_one);
            return false;
        }

        if (isScheduledTimeRepeated(scheduledTimes)) {
            promptMessage(R.string.ui_prompt_project_time_repetition);
            return false;
        }

        PollingConfigListItemClause projectDescriptionClause = (PollingConfigListItemClause)getContent(R.string.ui_tv_project_config_label_description);
        PollingProjectConfig projectConfig = projectEntity.getProjectConfig();
        projectConfig.setName((String)projectNameClause.getContent());
        if (projectEntity.getState() == PollingConfigState.PCS_NEW) {
            projectEntity.setName(projectConfig.getName());
        }
        projectConfig.setDescription((String)projectDescriptionClause.getContent());
        projectConfig.getScheduledTimes().clear();
        for (PollingConfigListItemClause scheduledTime :
                scheduledTimes) {
            projectConfig.getScheduledTimes().add((SimpleTime)scheduledTime.getContent());
        }

        if (projectEntity.getState() == PollingConfigState.PCS_INVARIANT) {
            if (isProjectConfigModified()) {
                projectEntity.setState(PollingConfigState.PCS_MODIFIED);
            }
        }

        return true;
    }

    private boolean isProjectConfigModified() {
        boolean result = false;
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.isModified()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isScheduledTimeRepeated(List<PollingConfigListItemClause> scheduledTimes) {
        for (int i = 0;i < scheduledTimes.size();++i) {
            for (int j = i + 1;j < scheduledTimes.size();++j) {
                if (scheduledTimes.get(i).getContent().equals(scheduledTimes.get(j).getContent())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isProjectNameRepetition(String newMissionName) {
        if (projectEntity.getState() != PollingConfigState.PCS_NEW) {
            if (projectEntity.getProjectConfig().getName().equals(newMissionName)) {
                return false;
            }
        }
        for (PollingConfigListItemProjectEntity projectEntity :
                currentProjectEntities) {
            if (projectEntity.getProjectConfig().getName().equals(newMissionName)) {
                return true;
            }
        }
        return false;
    }

    private Object getContent(int stringID) {
        Object result = null;
        final String label = getString(stringID);
        if (stringID == R.string.ui_tv_project_config_label_time) {
            List<PollingConfigListItemClause> clauses = new ArrayList<>();
            for (PollingConfigListItemClause itemClause :
                    itemClauses) {
                if (itemClause.getLabel().equals(label)) {
                    clauses.add(itemClause);
                }
            }
            result = clauses;
        } else {
            for (PollingConfigListItemClause itemClause :
                    itemClauses) {
                if (itemClause.getLabel().equals(label)) {
                    result = itemClause;
                    break;
                }
            }
        }
        return result;
    }

    private AdapterView.OnItemClickListener projectConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemClause = itemClauses.get(position);
            if (projectConfigAdapter.getItemViewType(position) == ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal()) {
                editDialog.show(getSupportFragmentManager(), currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                SimpleTime time = (SimpleTime) currentItemClause.getContent();
                TimePickerDialog timePickerDialog = new TimePickerDialog(PollingProjectConfigActivity.this, scheduledTimeSetListener, time.getHour(), time.getMinute(), false);
                timePickerDialog.setTitle(currentItemClause.getLabel());
                timePickerDialog.show();
            }
        }
    };

    private View.OnClickListener onTimeDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            itemClauses.remove((int)v.getTag());
            projectConfigAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener onTimeAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //注意此处不可直接在add中new PollingConfigListItemClause，因为这样无法使其状态变为modified
            PollingConfigListItemClause newScheduleTime = new PollingConfigListItemClause(getString(R.string.ui_tv_project_config_label_time), null);
            newScheduleTime.setContent(SimpleTime.from(new Date()));
            itemClauses.add(newScheduleTime);
            projectConfigAdapter.notifyDataSetChanged();
        }
    };

    private EditDialog.OnClickPositiveListener onEditDialogPositiveClickListener = new EditDialog.OnClickPositiveListener() {
        @Override
        public void onClick(String content) {
            currentItemClause.setContent(content);
            projectConfigAdapter.notifyDataSetChanged();
        }
    };

    private EditDialog editDialog;
    private final int FixItemCount = 2;
    private ScheduledTimeSetListener scheduledTimeSetListener;
    private List<PollingConfigListItemProjectEntity> currentProjectEntities;
    private PollingConfigListItemClause currentItemClause;
    private PollingConfigListItemProjectEntity projectEntity;
    private ProjectConfigAdapter projectConfigAdapter;
    private List<PollingConfigListItemClause> itemClauses;
}
