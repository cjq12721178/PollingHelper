package com.example.kat.pollinghelper.ui.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.structure.config.SimpleTime;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.adapter.ProjectConfigAdapter;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCell;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellClause;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellProjectEntity;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellState;
import com.example.kat.pollinghelper.structure.cell.scout.ScoutEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoutProjectConfigActivity extends ScoutConfigBaseActivity {

    private class ScheduledTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            getCurrentClause().setContent(new SimpleTime(hourOfDay, minute));
            getBaseAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_project_config);
        findViewById(R.id.btn_polling_config_time_add).setOnClickListener(onTimeAddClickListener);
    }

    @Override
    protected BaseAdapter initListView() {
        ProjectConfigAdapter projectConfigAdapter = new ProjectConfigAdapter(this, getClauses());
        ListView listView = (ListView)findViewById(R.id.lv_project_config);
        listView.setAdapter(projectConfigAdapter);
        listView.setOnItemClickListener(projectConfigClickListener);
        scheduledTimeSetListener = new ScheduledTimeSetListener();
        return projectConfigAdapter;
    }

    @Override
    protected ScoutEntity importEntity() {
        ScoutCell listItem = (ScoutCell)getArgument(ArgumentTag.AT_SCOUT_CELL_CURRENT_CLICKED);
        ScoutCellProjectEntity projectEntity;
        if (listItem.isEntity()) {
            projectEntity = (ScoutCellProjectEntity) listItem;
            setFirstEdit(false);
        } else {
            projectEntity = new ScoutCellProjectEntity(this, new ScoutProjectConfig());
            projectEntity.setState(ScoutCellState.PCS_NEW);
            setFirstEdit(true);
        }
        return projectEntity;
    }

    @Override
    protected List<ScoutCellClause> createClauses() {
        currentProjectEntities = (List<ScoutCellProjectEntity>)getArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY);
        List<ScoutCellClause> itemClauses = new ArrayList<>();
        ScoutProjectConfig projectConfig = getEntity().getProjectConfig();
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_tv_project_config_label_name),
                projectConfig.getName()));
        itemClauses.add(new ScoutCellClause(getString(R.string.ui_tv_project_config_label_description),
                projectConfig.getDescription()));
        String pollingTime = getString(R.string.ui_tv_project_config_label_time);
        for (SimpleTime simpleTime :
                projectConfig.getScheduledTimes()) {
            itemClauses.add(new ScoutCellClause(pollingTime, simpleTime));
        }
        return itemClauses;
    }

    @Override
    protected ScoutCellProjectEntity getEntity() {
        return (ScoutCellProjectEntity)super.getEntity();
    }

    @Override
    protected boolean updateEntity() {
        ScoutCellClause projectNameClause = getContent(R.string.ui_tv_project_config_label_name);
        if (projectNameClause.getContentString().isEmpty()) {
            processConfigError(R.string.ui_prompt_project_name_empty);
            return false;
        }
        if (isProjectNameRepetition(projectNameClause.getContentString())) {
            processConfigError(R.string.ui_prompt_project_name_repetition);
            return false;
        }

        List<ScoutCellClause> scheduledTimes = getScheduledTimes();
        if (scheduledTimes.isEmpty()) {
            processConfigError(R.string.ui_prompt_project_time_least_one);
            return false;
        }

        if (isScheduledTimeRepeated(scheduledTimes)) {
            processConfigError(R.string.ui_prompt_project_time_repetition);
            return false;
        }

        ScoutCellClause projectDescriptionClause = (ScoutCellClause)getContent(R.string.ui_tv_project_config_label_description);
        ScoutCellProjectEntity projectEntity = getEntity();
        ScoutProjectConfig projectConfig = projectEntity.getProjectConfig();
        projectConfig.setName((String)projectNameClause.getContent());
        if (projectEntity.getState() == ScoutCellState.PCS_NEW) {
            projectEntity.setName(projectConfig.getName());
        }
        projectConfig.setDescription((String)projectDescriptionClause.getContent());
        projectConfig.getScheduledTimes().clear();
        for (ScoutCellClause scheduledTime :
                scheduledTimes) {
            projectConfig.getScheduledTimes().add((SimpleTime)scheduledTime.getContent());
        }

        return true;
    }

    private boolean isScheduledTimeRepeated(List<ScoutCellClause> scheduledTimes) {
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
        ScoutCellProjectEntity currentProjectEntity = getEntity();
        for (ScoutCellProjectEntity projectEntity :
                currentProjectEntities) {
            if (currentProjectEntity != projectEntity &&
                    projectEntity.getProjectConfig().getName().equals(newMissionName)) {
                return true;
            }
        }
        return false;
    }

    private List<ScoutCellClause> getScheduledTimes() {
        final String label = getString(R.string.ui_tv_project_config_label_time);
        List<ScoutCellClause> clauses = new ArrayList<>();
        for (ScoutCellClause itemClause :
                getClauses()) {
            if (itemClause.getLabel().equals(label)) {
                clauses.add(itemClause);
            }
        }
        return clauses;
    }

    private AdapterView.OnItemClickListener projectConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScoutCellClause currentItemClause = getCurrentClause(position);
            if (getBaseAdapter().getItemViewType(position) == ProjectConfigAdapter.ProjectAdapterLayoutType.PALT_WITHOUT_ICON.ordinal()) {
                showEditDialog(currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                SimpleTime time = (SimpleTime) currentItemClause.getContent();
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScoutProjectConfigActivity.this, scheduledTimeSetListener, time.getHour(), time.getMinute(), false);
                timePickerDialog.setTitle(currentItemClause.getLabel());
                timePickerDialog.show();
            }
        }
    };

    private View.OnClickListener onTimeAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //注意此处不可直接在add中new ScoutCellClause，因为这样无法使其状态变为modified
            ScoutCellClause newScheduleTime = new ScoutCellClause(getString(R.string.ui_tv_project_config_label_time), null);
            newScheduleTime.setContent(SimpleTime.from(new Date()));
            getClauses().add(newScheduleTime);
            getBaseAdapter().notifyDataSetChanged();
        }
    };

    private ScheduledTimeSetListener scheduledTimeSetListener;
    private List<ScoutCellProjectEntity> currentProjectEntities;
}
