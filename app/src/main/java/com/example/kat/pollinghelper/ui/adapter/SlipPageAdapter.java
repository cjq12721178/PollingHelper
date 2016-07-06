package com.example.kat.pollinghelper.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.kat.pollinghelper.ui.fragment.AnalogPanelSlipPage;
import com.example.kat.pollinghelper.ui.fragment.DigitalTableSlipPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAT on 2016/4/29.
 */
public class SlipPageAdapter extends FragmentPagerAdapter {
    ViewGroup container;
    List<Fragment> slipPages;

    public SlipPageAdapter(FragmentManager fm) {
        super(fm);
        slipPages = new ArrayList<>();
        slipPages.add(new AnalogPanelSlipPage());
        slipPages.add(new DigitalTableSlipPage());
    }

    @Override
    public Fragment getItem(int position) {
        return slipPages.get(position);
    }

    @Override
    public int getCount() {
        return slipPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return slipPages.get(position).toString();
    }
}
