package com.example.kat.pollinghelper.ui.preference;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

/**
 * Created by KAT on 2016/8/10.
 */
public class FunctionSettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.file_function_setting));
        addPreferencesFromResource(R.xml.fragment_function_setting);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_ip)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_port)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_data_request_cycle)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_scan_cycle)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_scan_duration)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_data_view)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_scout_real_time)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        if (onPreferenceChangeListener == null)
            return;

        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        onPreferenceChangeListener.onPreferenceChange(preference,
                preference.getSharedPreferences().getString(preference.getKey(), ""));
    }

    public FunctionSettingFragment setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener l) {
        onPreferenceChangeListener = l;
        return this;
    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener;
}
