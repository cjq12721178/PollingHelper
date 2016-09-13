package com.example.kat.pollinghelper.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.bean.view.SensorValueProcessor;
import com.example.kat.pollinghelper.ui.adapter.SlipPageAdapter;
import com.example.kat.pollinghelper.ui.dialog.FilterDialog;
import com.example.kat.pollinghelper.ui.dialog.SearchDialog;
import com.example.kat.pollinghelper.ui.dialog.SortDialog;
import com.example.kat.pollinghelper.ui.fragment.AnalogPanelSlipPage;
import com.example.kat.pollinghelper.ui.fragment.DataViewFragment;
import com.example.kat.pollinghelper.ui.fragment.DigitalTableSlipPage;
import com.example.kat.pollinghelper.ui.fragment.LineChartSlipPage;
import com.example.kat.pollinghelper.utility.Converter;

import java.util.ArrayList;
import java.util.List;

public class DataViewActivity extends ManagedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        initFunctionPanel();
        initRefresher();
        initDataViewMode();
    }

    private void initFunctionPanel() {
        //初始化排序功能
        sortDialog = new SortDialog();
        sortDialog.setOnSortFactorChangedListener(onSortFactorChangedListener);
        findViewById(R.id.tv_sort).setOnClickListener(onSortClickListener);

        //初始化筛选功能
        filterDialog = new FilterDialog();
        filterDialog.setOnFilterChangedListener(onFilterChangedListener);
        findViewById(R.id.tv_filter).setOnClickListener(onFilterClickListener);

        //初始化搜索功能
        searchDialog = new SearchDialog();
        searchDialog.setSummary(getString(R.string.et_hint_search_data_view));
        searchDialog.setOnSearchListener(onSearchListener);
        findViewById(R.id.tv_search).setOnClickListener(onSearchClickListener);
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
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        E dataViewFragment = fragment != null && fragment.getClass() == c ? (E)fragment : null;
        return (dataViewFragment != null ? dataViewFragment : c.newInstance()).setLabel(getString(labelResId));
    }

    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    private void initTabStrip() {
        PagerTabStrip tabStrip = (PagerTabStrip) this.findViewById(R.id.ts_data_view);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(getResources().getColor(R.color.transparent_green));
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.transparent_red));
    }

    @Override
    protected void onInitializeBusiness() {
        sensorValueProcessor = new SensorValueProcessor();
        sensorValueProcessor.setSensorComparator(true, SensorValueProcessor.SortEntry.SE_TIME);
        putArgument(ArgumentTag.AT_DATA_LISTENER, sensorValueProcessor.getOnDataListener());
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
            slipPageAdapter.setSensorList(sensorValueProcessor.getFinalSensors());
            SharedPreferences configs = getSharedPreferences(getString(R.string.file_function_setting), MODE_PRIVATE);
            refreshTimeInterval = Converter.second2Millisecond(Converter.string2Int(configs.getString(getString(R.string.key_data_view), null),
                    getResources().getInteger(R.integer.time_interval_update_data_view)));
            bleScanTimeInterval = getResources().getInteger(R.integer.time_interval_scan_ble_data_view);
            refreshTimes = 0;
            isNextTimeUpdate = true;
            refresher.post(onUpdateDataView);
        }
    };

    private Runnable onUpdateDataView = new Runnable() {
        @Override
        public void run() {
            onUpdateView(onUpdateData());
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

    private void onUpdateView(boolean isOnlyNotifyCurrentItem) {
        int pos = viewPager.getCurrentItem();
        if (isOnlyNotifyCurrentItem) {
            slipPageAdapter.getItem(pos).updateDataView();
        } else {
            for (int i = Math.max(pos - 1, 0),
                 end = Math.min(pos + 1, slipPageAdapter.getCount() - 1);
                 i < end;++i) {
                slipPageAdapter.getItem(i).updateDataView();
            }
        }
    }

    private boolean onUpdateData() {
        return sensorValueProcessor.updateSensor();
    }

    private SortDialog.OnSortFactorChangedListener onSortFactorChangedListener = new SortDialog.OnSortFactorChangedListener() {
        @Override
        public void onChanged(boolean isAscending, int checkedRadioButtonId) {
            sensorValueProcessor.setSensorComparator(isAscending, getSortEntryById(checkedRadioButtonId));
            sensorValueProcessor.sort();
            onUpdateView(false);
        }
    };

    private SensorValueProcessor.SortEntry getSortEntryById(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.rdo_sort_entry_address:return SensorValueProcessor.SortEntry.SE_ADDRESS;
            case R.id.rdo_sort_entry_type:return SensorValueProcessor.SortEntry.SE_TYPE;
            case R.id.rdo_sort_entry_unit:return SensorValueProcessor.SortEntry.SE_UNIT;
            case R.id.rdo_sort_entry_time:
            default:return SensorValueProcessor.SortEntry.SE_TIME;
        }
    }

    private View.OnClickListener onSortClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sortDialog.show(getSupportFragmentManager());
        }
    };

    private View.OnClickListener onFilterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filterDialog.show(getSupportFragmentManager());
        }
    };

    private FilterDialog.OnFilterChangedListener onFilterChangedListener = new FilterDialog.OnFilterChangedListener() {
        @Override
        public void onChanged(boolean isRealChanged, int fromFilterConditionValue, int patternFilterConditionValue) {
            if (isRealChanged) {
                sensorValueProcessor.setFilterCondition(fromFilterConditionValue, patternFilterConditionValue);
                onUpdateView(false);
            }
        }
    };

    private SearchDialog.OnSearchListener onSearchListener = new SearchDialog.OnSearchListener() {
        @Override
        public void onSearch(boolean isSearchConditionChanged, String[] searchContents) {
            if (isSearchConditionChanged) {
                sensorValueProcessor.setSearchContents(searchContents);
                onUpdateView(false);
            }
        }
    };

    private View.OnClickListener onSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchDialog.show(getSupportFragmentManager());
        }
    };

    private SensorValueProcessor sensorValueProcessor;
    private SearchDialog searchDialog;
    private FilterDialog filterDialog;
    private SortDialog sortDialog;
    private Handler refresher;
    private int bleScanTimeInterval;
    private int refreshTimes;
    private boolean isNextTimeUpdate;
    private int refreshTimeInterval;
    private SlipPageAdapter slipPageAdapter;
    private ViewPager viewPager;
}