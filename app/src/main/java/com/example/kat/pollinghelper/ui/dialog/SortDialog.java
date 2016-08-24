package com.example.kat.pollinghelper.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/8/16.
 */
public class SortDialog extends DialogFragment {

    public interface OnSortFactorChangedListener {
        void onChanged(boolean isAscending, int checkedRadioButtonId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sort, null);
        rgpSort = (RadioGroup)view.findViewById(R.id.rg_sort_type);
        rgpSort.check(checkedId);
        view.findViewById(R.id.btn_ascending).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortClick(true);
            }
        });
        view.findViewById(R.id.btn_descending).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortClick(false);
            }
        });
        return view;
    }

    private void onSortClick(boolean isAscending) {
        checkedId = rgpSort.getCheckedRadioButtonId();
        if (onSortFactorChangedListener != null) {
            onSortFactorChangedListener.onChanged(isAscending, checkedId);
        }
        dismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return this.show(transaction);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        this.show(manager);
    }

    public void show(FragmentManager manager) {
        super.show(manager, FRAGMENT_TAG);
    }

    public int show(FragmentTransaction transaction) {
        return super.show(transaction, FRAGMENT_TAG);
    }

    private static final String FRAGMENT_TAG = "sort";

    public SortDialog setOnSortFactorChangedListener(OnSortFactorChangedListener l) {
        onSortFactorChangedListener = l;
        return this;
    }

    private OnSortFactorChangedListener onSortFactorChangedListener;
    RadioGroup rgpSort;
    private int checkedId = R.id.rdo_sort_entry_time;
}
