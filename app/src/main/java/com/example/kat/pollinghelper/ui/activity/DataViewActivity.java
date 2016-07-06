package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.adapter.SlipPageAdapter;

public class DataViewActivity extends ManagedActivity {
    SlipPageAdapter slipPageAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        initializeDataViewMode();
    }

    private void initializeDataViewMode() {
        slipPageAdapter = new SlipPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.vp_slip_page_container);
        viewPager.setAdapter(slipPageAdapter);
        viewPager.addOnPageChangeListener(new SlipPageChangeProcessor());
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

    private class SlipPageChangeProcessor implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setTitle(slipPageAdapter.getPageTitle(position));
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}