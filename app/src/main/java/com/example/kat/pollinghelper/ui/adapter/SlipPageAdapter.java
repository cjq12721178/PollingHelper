package com.example.kat.pollinghelper.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kat.pollinghelper.data.SensorValue;
import com.example.kat.pollinghelper.ui.fragment.DataViewFragment;

import java.util.List;

/**
 * Created by KAT on 2016/4/29.
 */
public class SlipPageAdapter extends FragmentPagerAdapter {

    public SlipPageAdapter(FragmentManager fm, List<DataViewFragment> slipPages) {
        super(fm);
        this.slipPages = slipPages;
    }

    @Override
    public DataViewFragment getItem(int position) {
        return slipPages.get(position);
    }

    @Override
    public int getCount() {
        return slipPages != null ? slipPages.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).getLabel();
    }

    public void setSensorList(List<SensorValue> sensorList) {
        if (slipPages == null || sensorList == null)
            return;

        for (DataViewFragment fragment :
                slipPages) {
            fragment.bindDataSource(sensorList);
        }
    }

    List<DataViewFragment> slipPages;
}
