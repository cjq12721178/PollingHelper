package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
import com.example.kat.pollinghelper.fuction.record.PollingProjectRecord;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.TreeViewAdapter;
import com.example.kat.pollinghelper.ui.structure.QueryInfo;

import java.util.Date;
import java.util.List;

public class PollingRecordActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_record);
    }

    @Override
    protected void onInitializeBusiness() {
        applyForHistoryRecords();
    }

    private void applyForHistoryRecords() {
        putArgument(ArgumentTag.AT_QUERY_INFO,
                new QueryInfo().setIntent(QueryInfo.WHOLE_RECORD_IN_SCHEDULED_TIME_RANGE)
                               .setBegScheduledTime(new Date(System.currentTimeMillis() - 7 * SimpleTime.DAY_MILLISECONDS)));
        notifyManager(OperaType.OT_QUERY_RECORD, queryFeedback);
    }

    private Runnable queryFeedback = new Runnable() {
        @Override
        public void run() {
            List<PollingProjectRecord> projectRecords = (List<PollingProjectRecord>)getArgument(ArgumentTag.AT_LIST_LATEST_PROJECT_RECORD);
            if (projectRecords != null) {
                if (treeViewAdapter == null) {
                    treeViewAdapter = new TreeViewAdapter(PollingRecordActivity.this, projectRecords);
                    treeViewAdapter.setIndicator(R.drawable.ic_arrow_up, R.drawable.ic_arrow_down);
                    treeViewAdapter.addHeader(1, R.layout.listitem_record_mission, 3, null);
                    treeViewAdapter.addHeader(2, R.layout.listitem_record_item, 4, null);
                    View projectRecordHeader = findViewById(R.id.include_record_header);
                    projectRecordHeader.setPadding(treeViewAdapter.getIndent(projectRecordHeader, 0), 0, 0, 0);
                    ListView recordListView = (ListView)findViewById(R.id.lv_record);
                    recordListView.setAdapter(treeViewAdapter);
                } else {
                    treeViewAdapter.setDataSource(projectRecords);
                }
                treeViewAdapter.notifyDataSetChanged();
            }
        }
    };

    private TreeViewAdapter treeViewAdapter;
}
