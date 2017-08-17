package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.record.ScoutItemRecord;
import com.example.kat.pollinghelper.bean.record.ScoutMissionRecord;
import com.example.kat.pollinghelper.bean.record.ScoutProjectRecord;
import com.example.kat.pollinghelper.bean.record.ScoutRecordState;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ArresterScoutRecordActivity extends ManagedActivity {

    private static final long DATA_UPDATE_INTERVAL = 1000;

    private ScoutProjectRecord projectRecord;
    //private Date originTime;
    private List<ArresterStateRecord> arresterStateRecords;
    private ArresterStateAdapter mArresterStateAdapter;
    private boolean isStopUpdate;
    private String lineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrester_scout_record);
    }

    @Override
    protected void onInitializeBusiness() {
        importScoutProjectRecord();
        changeTitle();
        judgeCurrentLine();
    }

    private void importScoutProjectRecord() {
        projectRecord = (ScoutProjectRecord)getArgument(ArgumentTag.AT_PROJECT_RECORD_CURRENT);
        if (projectRecord.getPollingState() != ScoutRecordState.PS_COMPLETED) {
            projectRecord.setPollingState(ScoutRecordState.PS_RUNNING);
        }
        //originTime = projectRecord.getFinishedTime();
    }

    private void changeTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.background_title_arrester));
        actionBar.setCustomView(R.layout.title_arrester_scout_record);
        TextView tvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_title);
        tvTitle.setText(projectRecord.getProjectConfig().getName());
    }

    private void judgeCurrentLine() {
        notifyManager(OperaType.OT_JUDGE_ARRESTER_GROUP, onArresterGroupJudged);
    }

    private Runnable onArresterGroupJudged = new Runnable() {
        @Override
        public void run() {
            arresterStateRecords = generateArresterStateRecords();
            if (arresterStateRecords != null) {
                initializeScoutView();
            }
        }
    };

    private List<ArresterStateRecord> generateArresterStateRecords() {
        try {
            List<ScoutMissionRecord> missionRecords = (List<ScoutMissionRecord>) getArgument(ArgumentTag.AT_LIST_MISSION_RECORD);
            if (missionRecords == null) {
                promptMessage(R.string.arrester_group_offline);
                finish();
                return null;
            }
            List<ArresterStateRecord> groupRecords = new ArrayList<>(missionRecords.size());
            for (ScoutMissionRecord missionRecord :
                    missionRecords) {
                if (lineName == null) {
                    lineName = missionRecord.getMissionConfig().getDescription();
                }
                if (missionRecord.getItemRecords().size() == 2) {
                    groupRecords.add(new HumitureRecord(missionRecord));
                } else {
                    groupRecords.add(new ArresterGroupRecord(missionRecord));
                }
            }
            return groupRecords;
        } catch (Exception e) {
            promptMessage(R.string.arrester_config_error);
            finish();
        }
        return null;
    }

    private void initializeScoutView() {
        TextView tvLineName = (TextView) findViewById(R.id.tv_arrester_state_group_name);
        tvLineName.setText(lineName);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_arrester_records);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.iv_mission_warn_interval), true));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mArresterStateAdapter = new ArresterStateAdapter(arresterStateRecords);
        recyclerView.setAdapter(mArresterStateAdapter);

        //定时刷新数据
        getHandler().postDelayed(onUpdateSensorData, DATA_UPDATE_INTERVAL);
    }

    private Runnable onUpdateSensorData = new Runnable() {
        @Override
        public void run() {
            notifyManager(OperaType.OT_UPDATE_MULTIPLE_MISSION_SENSOR_VALUE, onUpdateData);
            if (!isStopUpdate) {
                getHandler().postDelayed(onUpdateSensorData, DATA_UPDATE_INTERVAL);
            }
        }
    };

    private Runnable onUpdateData = new Runnable() {
        @Override
        public void run() {
            mArresterStateAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        isStopUpdate = true;
        super.onDestroy();
    }

    private interface ArresterStateRecord {
        String getName();
        int getType();
    }

    private static class ArresterGroupRecord implements ArresterStateRecord {

        private static final String ARRESTER_A = "A相避雷器";
        private static final String ARRESTER_B = "B相避雷器";
        private static final String ARRESTER_C = "C相避雷器";

        private final String name;
        private final ArresterRecord arresterARecord;
        private final ArresterRecord arresterBRecord;
        private final ArresterRecord arresterCRecord;

        public ArresterGroupRecord(ScoutMissionRecord missionRecord) {
            if (missionRecord == null) {
                throw new NullPointerException("mission record is null");
            }
            name = missionRecord.getMissionConfig().getName();
            arresterARecord = new ArresterRecord(ARRESTER_A, missionRecord);
            arresterBRecord = new ArresterRecord(ARRESTER_B, missionRecord);
            arresterCRecord = new ArresterRecord(ARRESTER_C, missionRecord);
        }

        @Override
        public String getName() {
            return name;
        }

        public ArresterRecord getArresterARecord() {
            return arresterARecord;
        }

        public ArresterRecord getArresterBRecord() {
            return arresterBRecord;
        }

        public ArresterRecord getArresterCRecord() {
            return arresterCRecord;
        }

        @Override
        public int getType() {
            return 1;
        }
    }

    private static class ArresterRecord {

        public static final String MEASUREMENT_LEAKAGE_CURRENT = "泄露电流值";
        public static final String MEASUREMENT_LIGHTENING_STROKE_COUNT = "累计雷击数";
        public static final String MEASUREMENT_TEMPERATURE = "阀芯温度值";

        private String name;
        private ScoutItemRecord leakageCurrent;
        private ScoutItemRecord lighteningStrokeCount;
        private ScoutItemRecord temperature;

        public ArresterRecord(String name, ScoutMissionRecord missionRecord) {
            this.name = name;
            for (ScoutItemRecord itemRecord :
                    missionRecord.getItemRecords()) {
                String measureName = itemRecord.getItemConfig().getMeasureName();
                if (measureName.contains(name)) {
                    if (measureName.contains(MEASUREMENT_LEAKAGE_CURRENT)) {
                        leakageCurrent = itemRecord;
                    } else if (measureName.contains(MEASUREMENT_LIGHTENING_STROKE_COUNT)) {
                        lighteningStrokeCount = itemRecord;
                    } else if (measureName.contains(MEASUREMENT_TEMPERATURE)) {
                        temperature = itemRecord;
                    }
                }
            }
            if (leakageCurrent == null ||
                    lighteningStrokeCount == null ||
                    temperature == null) {
                throw new NullPointerException("leakage current is null or lightening stroke count is null or temperature is null");
            }
        }

        public String getName() {
            return name;
        }

        public String getLeakageCurrentValue() {
            return leakageCurrent.getSignificantValueWithUnit();
        }

        public String getLighteningStrokeCountValue() {
            return lighteningStrokeCount.getSignificantValueWithUnit();
        }

        public String getTemperatureValue() {
            return temperature.getSignificantValueWithUnit();
        }
    }

    private static class HumitureRecord implements ArresterStateRecord {

        private static final String MEASUREMENT_TEMPERATURE = "当前温度";
        private static final String MEASUREMENT_HUMIDITY = "当前湿度";

        private String name;
        private ScoutItemRecord temperature;
        private ScoutItemRecord humidity;

        public HumitureRecord(ScoutMissionRecord missionRecord) {
            if (missionRecord == null) {
                throw new NullPointerException("mission record is null");
            }
            name = missionRecord.getMissionConfig().getName();
            for (ScoutItemRecord itemRecord :
                    missionRecord.getItemRecords()) {
                String measureName = itemRecord.getItemConfig().getMeasureName();
                if (measureName.contains(MEASUREMENT_TEMPERATURE)) {
                    temperature = itemRecord;
                } else if (measureName.contains(MEASUREMENT_HUMIDITY)) {
                    humidity = itemRecord;
                }
            }
            if (temperature == null || humidity == null) {
                throw new NullPointerException("leakage humidity is null or temperature is null");
            }
        }

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getTemperatureValue() {
            return temperature.getSignificantValueWithUnit();
        }

        public String getHumidityValue() {
            return humidity.getSignificantValueWithUnit();
        }
    }

    private static class ArresterStateAdapter extends RecyclerView.Adapter {

        private final List<ArresterStateRecord> arresterStateRecords;

        private ArresterStateAdapter(List<ArresterStateRecord> mArresterStateRecords) {
            this.arresterStateRecords = mArresterStateRecords;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == 1
                    ? new ArresterViewHolder(View.inflate(parent.getContext(),
                        R.layout.list_item_arrester_group_record,
                        null))
                    : new EnvironmentViewHolder(View.inflate(parent.getContext(),
                        R.layout.list_item_environment_record,
                        null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                ArresterGroupRecord record = (ArresterGroupRecord) arresterStateRecords.get(position);
                ArresterViewHolder viewHolder = (ArresterViewHolder) holder;
                viewHolder.tvArresterGroupName.setText(record.getName());
                viewHolder.tvLeakageCurrentA.setText(record.getArresterARecord().getLeakageCurrentValue());
                viewHolder.tvLeakageCurrentB.setText(record.getArresterBRecord().getLeakageCurrentValue());
                viewHolder.tvLeakageCurrentC.setText(record.getArresterCRecord().getLeakageCurrentValue());
                viewHolder.tvLighteningStrokeCountA.setText(record.getArresterARecord().getLighteningStrokeCountValue());
                viewHolder.tvLighteningStrokeCountB.setText(record.getArresterBRecord().getLighteningStrokeCountValue());
                viewHolder.tvLighteningStrokeCountC.setText(record.getArresterCRecord().getLighteningStrokeCountValue());
                viewHolder.tvTemperatureA.setText(record.getArresterARecord().getTemperatureValue());
                viewHolder.tvTemperatureB.setText(record.getArresterBRecord().getTemperatureValue());
                viewHolder.tvTemperatureC.setText(record.getArresterCRecord().getTemperatureValue());
            } else {
                HumitureRecord record = (HumitureRecord) arresterStateRecords.get(position);
                EnvironmentViewHolder viewHolder = (EnvironmentViewHolder) holder;
                viewHolder.tvEnvironmentName.setText(record.getName());
                viewHolder.tvTemperature.setText(record.getTemperatureValue());
                viewHolder.tvHumidity.setText(record.getHumidityValue());
            }

        }

        @Override
        public int getItemCount() {
            return arresterStateRecords != null ? arresterStateRecords.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return arresterStateRecords.get(position).getType();
        }

        public static class ArresterViewHolder extends RecyclerView.ViewHolder {

            private TextView tvArresterGroupName;
            private TextView tvLeakageCurrentA;
            private TextView tvLeakageCurrentB;
            private TextView tvLeakageCurrentC;
            private TextView tvLighteningStrokeCountA;
            private TextView tvLighteningStrokeCountB;
            private TextView tvLighteningStrokeCountC;
            private TextView tvTemperatureA;
            private TextView tvTemperatureB;
            private TextView tvTemperatureC;

            public ArresterViewHolder(View itemView) {
                super(itemView);
                tvArresterGroupName = (TextView) itemView.findViewById(R.id.tv_arrester_state_name);
                tvLeakageCurrentA = (TextView) itemView.findViewById(R.id.tv_leakage_current_a);
                tvLeakageCurrentB = (TextView) itemView.findViewById(R.id.tv_leakage_current_b);
                tvLeakageCurrentC = (TextView) itemView.findViewById(R.id.tv_leakage_current_c);
                tvLighteningStrokeCountA = (TextView) itemView.findViewById(R.id.tv_lightening_stroke_count_a);
                tvLighteningStrokeCountB = (TextView) itemView.findViewById(R.id.tv_lightening_stroke_count_b);
                tvLighteningStrokeCountC = (TextView) itemView.findViewById(R.id.tv_lightening_stroke_count_c);
                tvTemperatureA = (TextView) itemView.findViewById(R.id.tv_temperature_a);
                tvTemperatureB = (TextView) itemView.findViewById(R.id.tv_temperature_b);
                tvTemperatureC = (TextView) itemView.findViewById(R.id.tv_temperature_c);
            }
        }

        public static class EnvironmentViewHolder extends RecyclerView.ViewHolder {

            private TextView tvEnvironmentName;
            private TextView tvTemperature;
            private TextView tvHumidity;

            public EnvironmentViewHolder(View itemView) {
                super(itemView);
                tvEnvironmentName = (TextView) itemView.findViewById(R.id.tv_arrester_state_name);
                tvTemperature = (TextView) itemView.findViewById(R.id.tv_environment_temperature_value);
                tvHumidity = (TextView) itemView.findViewById(R.id.tv_environment_humidity_value);
            }
        }
    }
}
