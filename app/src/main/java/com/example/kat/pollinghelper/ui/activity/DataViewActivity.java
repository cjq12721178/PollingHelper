package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.ui.adapter.SlipPageAdapter;
import com.example.kat.pollinghelper.ui.fragment.AnalogPanelSlipPage;
import com.example.kat.pollinghelper.ui.fragment.DataViewFragment;
import com.example.kat.pollinghelper.ui.fragment.DigitalTableSlipPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataViewActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        refresher = new Handler();
        initializeDataViewMode();
    }

    private void initializeDataViewMode() {
        initTabStrip();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.vp_slip_page_container);
        slipPageAdapter = new SlipPageAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(slipPageAdapter);
        viewPager.addOnPageChangeListener(new SlipPageChangeProcessor());
    }

    @NonNull
    private List<DataViewFragment> getFragments() {
        List<DataViewFragment> slipPages = new ArrayList<>();
        slipPages.add(new AnalogPanelSlipPage()
                .setLabel(getString(R.string.fragment_title_analog_dial)));
        slipPages.add(new DigitalTableSlipPage()
                .setLabel(getString(R.string.fragment_title_digital_table)));
        return slipPages;
    }

    private void initTabStrip() {
        PagerTabStrip tabStrip = (PagerTabStrip) this.findViewById(R.id.ts_data_view);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(this.getResources().getColor(R.color.transparent_green));
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.transparent_red));
    }

    @Override
    protected void onInitializeBusiness() {
        putArgument(ArgumentTag.AT_DATA_LISTENER, onDataListener);
        notifyManager(OperaType.OT_REQUEST_SENSOR_COLLECTION, startRefreshDataView);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_data_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO 切换数据类型
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        isNextTimeUpdate = false;
        putArgument(ArgumentTag.AT_DATA_LISTENER, null);
        notifyManager(OperaType.OT_REQUEST_SENSOR_COLLECTION);
        super.onDestroy();
    }

    private class SlipPageChangeProcessor implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private Runnable startRefreshDataView = new Runnable() {
        @Override
        public void run() {
            slipPageAdapter.setSensorList(sensorList);
            refreshTimeInterval = getResources().getInteger(R.integer.time_interval_update_data_view);
            isNextTimeUpdate = true;
            refresher.post(onUpdateDataView);
        }
    };

    private Runnable onUpdateDataView = new Runnable() {
        @Override
        public void run() {
            onUpdateData();
            onUpdateView();
            predetermineNextUpdate();
        }
    };

    private void predetermineNextUpdate() {
        if (isNextTimeUpdate) {
            refresher.postDelayed(onUpdateDataView, refreshTimeInterval);
        }
    }

    private void onUpdateView() {
        slipPageAdapter.getItem(viewPager.getCurrentItem()).updateDataView();
    }

    private void onUpdateData() {
        synchronized (sensorBuffer) {
            if (!sensorBuffer.isEmpty()) {
                sensorList.addAll(sensorBuffer);
                sensorBuffer.clear();
            }
        }
    }

    private DataStorage.OnDataListener onDataListener = new DataStorage.OnDataListener() {
        @Override
        public void onInit(Collection<SensorValue> sensorCollection) {
            sensorList = new ArrayList<>(sensorCollection);
            sensorBuffer = new ArrayList<>();
        }

        @Override
        public void onUpdate(SensorValue newSensor) {
            synchronized (sensorBuffer) {
                sensorBuffer.add(newSensor);
            }
        }
    };

    private List<SensorValue> sensorBuffer;
    private List<SensorValue> sensorList;
    private Handler refresher;
    private boolean isNextTimeUpdate;
    private int refreshTimeInterval;
    private SlipPageAdapter slipPageAdapter;
    private ViewPager viewPager;
}