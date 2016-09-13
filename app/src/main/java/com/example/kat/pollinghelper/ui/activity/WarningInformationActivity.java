package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.warn.MissionWarnInfo;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.adapter.ItemWarnAdapter;
import com.example.kat.pollinghelper.ui.adapter.MissionWarnAdapter;
import com.example.kat.pollinghelper.ui.util.SpaceItemDecoration;

import java.util.List;

public class WarningInformationActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_information);
        setEmptyWarnInfo();
        initItemWarnInfo();
    }

    private void setEmptyWarnInfo() {
        tvEmptyWarnInfo = (TextView)findViewById(R.id.tv_item_warn_empty);
    }

    @Override
    protected void onInitializeBusiness() {
        importWarnInfo();
        setWarnOccurredListener(onWarnOccurredListener);
        initMissionWarnView();
    }

    @Override
    protected void onDestroy() {
        setWarnOccurredListener(null);
        super.onDestroy();
    }

    private void setWarnOccurredListener(MissionWarnInfo.OnWarnOccurredListener l) {
        if (warnInfo != null) {
            for (MissionWarnInfo missionWarn :
                    warnInfo) {
                missionWarn.setOnWarnOccurredListener(l);
            }
        }
    }

    private void initItemWarnInfo() {
        ListView listView = (ListView)findViewById(R.id.lv_item_warn);
        itemWarnAdapter = new ItemWarnAdapter(this);
        listView.setAdapter(itemWarnAdapter);
    }

    private void initMissionWarnView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_mission_warn);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.iv_mission_warn_interval), false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        missionWarnAdapter = new MissionWarnAdapter(this, warnInfo);
        missionWarnAdapter.setOnItemClickListener(onMissionWarnClickListener);
        //默认选中第一个MissionWarnInfo
        missionWarnAdapter.setDefaultSelectedItem(0);
        changeMissionWarnInfo(0);
        updateItemWarnInfo();
        recyclerView.setAdapter(missionWarnAdapter);
    }

    private void importWarnInfo() {
        warnInfo = (List<MissionWarnInfo>)getArgument(ArgumentTag.AT_WARN_INFO);
    }

    private MissionWarnAdapter.OnItemClickListener onMissionWarnClickListener = new MissionWarnAdapter.OnItemClickListener() {
        @Override
        public void onClick(View v, int pos) {
            changeMissionWarnInfo(pos);
            updateItemWarnInfo();
        }
    };

    private void changeMissionWarnInfo(int pos) {
        if (warnInfo == null || pos < 0 || pos >= warnInfo.size())
            return;
        itemWarnAdapter.setMissionWarnInfo(warnInfo.get(pos));
    }

    private void updateItemWarnInfo() {
        MissionWarnInfo currentMissionWarnInfo = missionWarnAdapter.getCurrentSelectedMissionWarnInfo();
        tvEmptyWarnInfo.setVisibility(currentMissionWarnInfo == null || currentMissionWarnInfo.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        itemWarnAdapter.notifyDataSetChanged();
    }

    private MissionWarnInfo.OnWarnOccurredListener onWarnOccurredListener = new MissionWarnInfo.OnWarnOccurredListener() {
        @Override
        public void onWarnOccurred(MissionWarnInfo missionWarn) {
            if (missionWarn == missionWarnAdapter.getCurrentSelectedMissionWarnInfo()) {
                runOnUiThread(notifyWarnOccurred);
            }
        }
    };

    private Runnable notifyWarnOccurred = new Runnable() {
        @Override
        public void run() {
            updateItemWarnInfo();
        }
    };

    private List<MissionWarnInfo> warnInfo;
    private MissionWarnAdapter missionWarnAdapter;
    private ItemWarnAdapter itemWarnAdapter;
    private TextView tvEmptyWarnInfo;
}
