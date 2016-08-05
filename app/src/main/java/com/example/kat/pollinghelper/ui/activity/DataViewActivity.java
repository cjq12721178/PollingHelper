package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.example.kat.pollinghelper.ui.fragment.LineChartSlipPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataViewActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        initRefresher();
        initDataViewMode();
    }

    private void initRefresher() {
        refresher = new Handler();
    }

    private void initDataViewMode() {
        initTabStrip();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.vp_slip_page_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        slipPageAdapter = new SlipPageAdapter(fragmentManager, getFragments(fragmentManager));
        viewPager.setAdapter(slipPageAdapter);
        viewPager.addOnPageChangeListener(new SlipPageChangeProcessor());
    }

    @NonNull
    private List<DataViewFragment> getFragments(FragmentManager fragmentManager) {
        List<DataViewFragment> slipPages = new ArrayList<>();
        try {
            slipPages.add(getFragment(fragmentManager, AnalogPanelSlipPage.class,
                    0, R.string.fragment_title_analog_dial));
            slipPages.add(getFragment(fragmentManager, DigitalTableSlipPage.class,
                    1, R.string.fragment_title_digital_table));
            slipPages.add(getFragment(fragmentManager, LineChartSlipPage.class,
                    2, R.string.fragment_title_line_chart));
        } catch (Exception ignored) {
        }
        return slipPages;
    }

    private <E extends DataViewFragment> DataViewFragment getFragment(FragmentManager fragmentManager,
                                                                      Class<E> c,
                                                                      int position,
                                                                      int labelResId)
            throws IllegalAccessException, InstantiationException {
        String name = makeFragmentName(viewPager.getId(), position);
        E fragment = (E)fragmentManager.findFragmentByTag(name);
        return (fragment != null ? fragment : c.newInstance()).setLabel(getString(labelResId));
    }

    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
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
        Log.d("PollingHelper", "activity onDestroy");
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
            bleScanTimeInterval = getResources().getInteger(R.integer.time_interval_scan_ble_data_view);
            refreshTimes = 0;
            isNextTimeUpdate = true;
            refresher.post(onUpdateDataView);
        }
    };

    private Runnable onUpdateDataView = new Runnable() {
        @Override
        public void run() {
            onUpdateData();
            onUpdateView();
            scanBleSensor();
            predetermineNextUpdate();
        }
    };

    private void scanBleSensor() {
        if (++refreshTimes * refreshTimeInterval >= bleScanTimeInterval) {
            refreshTimes = 0;
            notifyManager(OperaType.OT_SCAN_BLE_SENSOR);
        }
    }

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
    private int bleScanTimeInterval;
    private int refreshTimes;
    private boolean isNextTimeUpdate;
    private int refreshTimeInterval;
    private SlipPageAdapter slipPageAdapter;
    private ViewPager viewPager;
}