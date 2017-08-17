package com.example.kat.pollinghelper.ui.activity;

import com.example.kat.pollinghelper.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kat.pollinghelper.bean.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.processor.service.NotificationService;
import com.example.kat.pollinghelper.ui.adapter.FunctionListAdapter;
import com.example.kat.pollinghelper.bean.main_interface.ElseFunctionListItem;
import com.example.kat.pollinghelper.bean.main_interface.FunctionListItem;
import com.example.kat.pollinghelper.bean.main_interface.FunctionType;
import com.example.kat.pollinghelper.bean.ScoutInfo;
import com.example.kat.pollinghelper.bean.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.bean.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.service.ManagerService;
import com.example.kat.pollinghelper.bean.QueryInfo;
import com.example.kat.pollinghelper.bean.main_interface.RealTimeScoutItem;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ManagedActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPollingBusiness();
        initializePrompter();
        registerReceiver();
        initService();
        initFunctionListView();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.ba_renew_project_record));
        registerReceiver(renewProjectRecordReceiver, filter);
    }

    private void initPollingBusiness() {
        scoutInfo = new ScoutInfo();
    }

    private void initializePrompter() {
        BeautyToast.init(getApplicationContext());
    }

    @Override
    protected void onInitializeBusiness() {
        checkPollingDataBase();
        importProjectAndSensorConfigs();
    }

    private void importProjectAndSensorConfigs() {
        putArgument(ArgumentTag.AT_QUERY_INFO, new QueryInfo().setIntent(QueryInfo.LATEST_RECORD_FOR_PER_PROJECT));
        notifyManager(updateFunctionListView,
                OperaType.OT_IMPORT_PROJECT_AND_SENSOR_CONFIGS,
                OperaType.OT_QUERY_RECORD,
                OperaType.OT_INSTALL_WARN_LISTENER);
    }

    private void checkPollingDataBase() {
        notifyManager(OperaType.OT_CREATE_POLLING_DATABASE);
    }

    private void initService() {
        startService(new Intent(this, NotificationService.class));
        startService(new Intent(this, ManagerService.class));
    }

    private void initFunctionListView() {
        RealTimeScoutItem.initContentPrefix(this);
        functionListItems = new ArrayList<>();
        functionListItems.add(new ElseFunctionListItem(getString(R.string.activity_title_data_view)));
        functionListItems.add(new ElseFunctionListItem(getString(R.string.activity_title_warning_information)));
        functionListItems.add(new ElseFunctionListItem(getString(R.string.activity_title_polling_record)));
        functionListAdapter = new FunctionListAdapter(this, functionListItems);
        ListView lvFunction = (ListView) findViewById(R.id.lvFunction);
        lvFunction.setAdapter(functionListAdapter);
        lvFunction.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case R.id.mi_polling_config: {
                startPollingConfig();
            } break;
            case R.id.mi_sensor_config: {
                startSensorConfig();
            } break;
            case R.id.mi_else_config: {
                startFunction(FunctionSettingActivity.class);
            } break;
            default: {
                result = super.onOptionsItemSelected(item);
            } break;
        }
        return result;
    }

    private void startSensorConfig() {
        putArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG, scoutInfo.getSensorConfigs());
        startActivityForResult(new Intent(this, SensorEntityActivity.class), REQUEST_CODE_SENSOR_CONFIG);
    }

    private void startPollingConfig() {
        if (scoutInfo.getSensorConfigs().isEmpty()) {
            Toast.makeText(this, getString(R.string.ui_prompt_empty_sensor_config), Toast.LENGTH_SHORT).show();
        } else {
            putArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG, scoutInfo.getSensorConfigs());
            putArgument(ArgumentTag.AT_LIST_PROJECT_CONFIG, scoutInfo.getProjectConfigs());
            startActivityForResult(new Intent(this, ScoutConfigActivity.class), REQUEST_CODE_POLLING_CONFIG);
        }
    }

    private void startFunction(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(renewProjectRecordReceiver);
        stopService(new Intent(this, ManagerService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - exitTime > 2000) {
            promptMessage(R.string.ui_prompt_exit_app);
            exitTime = currentTime;
        } else {
            super.onBackPressed();
        }
    }

    private void updateNotificationTime() {
        Intent intent = new Intent(getString(R.string.ba_update_project_time));
        intent.putExtra(getString(R.string.tag_project_schedule_times), scoutInfo.getProjectScheduleTimeInfo());
        sendBroadcast(intent);
    }

    private void updateMainListView() {
        clearCurrentProjectRecords();
        for (ScoutProjectRecord projectRecord :
                scoutInfo.getProjectRecords()) {
            functionListItems.add(new RealTimeScoutItem(projectRecord));
        }
        functionListAdapter.notifyDataSetChanged();
    }

    private void clearCurrentProjectRecords() {
        for (int i = 0;i < functionListItems.size();) {
            FunctionListItem functionListItem = functionListItems.get(i);
            if (functionListItem.getType() == FunctionType.FT_ELSE) {
                ++i;
            } else {
                functionListItems.remove(i);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                startFunction(DataViewActivity.class);
            } break;
            case 1: {
                startFunction(WarningInformationActivity.class);
            } break;
            case 2: {
                startFunction(ScoutRecordActivity.class);
            } break;
            default: {
                startPolling(position - getResources().getInteger(R.integer.main_activity_fix_item_count));
            } break;
        }
    }

    private void startPolling(int projectIndex) {
        if (projectIndex >= 0 && projectIndex < scoutInfo.getProjectRecords().size()) {
            ScoutProjectRecord record = scoutInfo.getProjectRecords().get(projectIndex);
            putArgument(ArgumentTag.AT_PROJECT_RECORD_CURRENT, record);
            if ("智能避雷器巡检".equals(record.getProjectConfig().getDescription())) {
                startActivityForResult(new Intent(this, ArresterScoutRecordActivity.class), projectIndex);
            } else {
                startActivityForResult(new Intent(this, ScoutProjectRecordActivity.class), projectIndex);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= 0 && requestCode < scoutInfo.getProjectRecords().size()) {
            functionListAdapter.notifyDataSetChanged();
        }else if (requestCode == REQUEST_CODE_POLLING_CONFIG && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(ArgumentTag.RESTORE_PROJECT_AND_SENSOR_CONFIG, false)) {
                showLoadingDialog(R.string.ui_import_project_and_sensor_configs);
                importProjectAndSensorConfigs();
            } else if (data.getBooleanExtra(ArgumentTag.PROJECT_CONFIG_CHANGED, false)) {
                updateFunctionListView.run();
                notifyManager(OperaType.OT_INSTALL_WARN_LISTENER);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SENSOR_CONFIG) {
            if (data.getBooleanExtra(ArgumentTag.RESTORE_PROJECT_AND_SENSOR_CONFIG, false)) {
                showLoadingDialog(R.string.ui_import_project_and_sensor_configs);
                importProjectAndSensorConfigs();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Runnable updateFunctionListView = new Runnable() {
        @Override
        public void run() {
            closeLoadingDialog();
            List<ScoutProjectConfig> projectConfigs = (List<ScoutProjectConfig>)getArgument(ArgumentTag.AT_LIST_PROJECT_CONFIG);
            List<ScoutSensorConfig> sensorConfigs = (List<ScoutSensorConfig>)getArgument(ArgumentTag.AT_LIST_SENSOR_CONFIG);
            List<ScoutProjectRecord> projectRecords = (List<ScoutProjectRecord>)getArgument(ArgumentTag.AT_LIST_LATEST_PROJECT_RECORD);
            if (scoutInfo.generateProjectRecords(projectConfigs, sensorConfigs, projectRecords)) {
                updateNotificationTime();
                updateMainListView();
            } else {
                promptMessage(R.string.ui_prompt_import_project_and_sensor_configs_failed);
            }
        }
    };

    private BroadcastReceiver renewProjectRecordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String projectName = intent.getStringExtra(getString(R.string.tag_project_name));
                for (int i = 0;i < functionListItems.size();++i) {
                    FunctionListItem functionListItem = functionListItems.get(i);
                    if (functionListItem.getType() == FunctionType.FT_REAL_TIME_POLLING) {
                        RealTimeScoutItem realTimeScoutItem = (RealTimeScoutItem)functionListItem;
                        if (realTimeScoutItem.getProjectRecord().getProjectConfig().getName().equals(projectName)) {
                            realTimeScoutItem.setProjectRecord(scoutInfo.generateNewProjectRecord(realTimeScoutItem.getProjectRecord().getProjectConfig()));
                            functionListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    };

    private final int REQUEST_CODE_POLLING_CONFIG = 1000;
    private final int REQUEST_CODE_SENSOR_CONFIG = 1001;
    private List<FunctionListItem> functionListItems;
    private FunctionListAdapter functionListAdapter;
    private ScoutInfo scoutInfo;
    private long exitTime;
}
