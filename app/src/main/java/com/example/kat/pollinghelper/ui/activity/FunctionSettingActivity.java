package com.example.kat.pollinghelper.ui.activity;

import android.os.Bundle;
import android.preference.Preference;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.preference.FunctionSettingFragment;
import com.example.kat.pollinghelper.utility.Converter;

public class FunctionSettingActivity extends ManagedActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new FunctionSettingFragment().setOnPreferenceChangeListener(this))
                                               .commit();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getTitleRes()) {
            case R.string.ui_ip_title: {
                if (newValue == null) {
                    promptMessage(R.string.ui_prompt_ip_format_error);
                    return false;
                }
            } break;
            case R.string.ui_port_title: {
                if (newValue == null) {
                    promptMessage(R.string.ui_prompt_port_format_error);
                    return false;
                }
            } break;
            case R.string.ui_ep_data_request_title: {
                int setValue = Integer.parseInt((String) newValue);
                if (setValue < getResources().getInteger(R.integer.time_interval_request_data)) {
                    promptMessage(R.string.ui_prompt_data_request_cycle_down_limit);
                    return false;
                }
            } break;
            case R.string.ui_ep_title_scan_cycle: {
                int setValue = Integer.parseInt((String) newValue);
                if (Converter.minute2Millisecond(setValue) < 0) {
                    promptMessage(R.string.ui_prompt_scan_ble_cycle_up_limit);
                    return false;
                }
            } break;
            case R.string.ui_ep_title_scan_duration: {
                int setValue = Integer.parseInt((String) newValue);
                if (setValue < getResources().getInteger(R.integer.time_duration_scan_ble_down_limit)) {
                    promptMessage(R.string.ui_prompt_scan_ble_duration_down_limit);
                    return false;
                }
            } break;
            case R.string.ui_ep_title_data_view:
            case R.string.ui_ep_title_scout_real_time: {
                int setValue = Integer.parseInt((String) newValue);
                if (setValue <= 0) {
                    promptMessage(R.string.ui_prompt_data_refresh_cycle);
                    return false;
                }
            } break;
        }
        preference.setSummary(newValue.toString());
        return true;
    }
}
