package com.example.kat.pollinghelper.ui.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kat.pollinghelper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/8/18.
 */
public class FilterDialog extends DialogFragment {

    private class ConditionGroup {

        public class OnConditionChangedListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                if (v.getId() == whole.getId()) {
                    v.setBackground(selected);
                    for (View item :
                            items) {
                        item.setBackground(normal);
                    }
                } else {
                    whole.setBackground(normal);
                    v.setBackground(v.getBackground() == selected ? normal : selected);
                }
            }
        }

        public ConditionGroup(Drawable selected, Drawable normal) {
            this.selected = selected;
            this.normal = normal;
        }

        //conditionValue最高位为1表示选中whole，0为未选中
        //conditionValue从最低位开始，每一位1表示选中，0表示未选中，其次序从低位到高位，对应items从前到后
        public boolean init(int conditionValue, View whole, View... items) {
            if (whole == null || items == null || items.length < 2 || items.length > 31)
                return false;
            OnConditionChangedListener l = new OnConditionChangedListener();
            whole.setOnClickListener(l);
            if (conditionValue < 0) {
                conditionValue = DEFAULT_CONDITION_VALUE;
            }
            whole.setBackground(conditionValue == DEFAULT_CONDITION_VALUE ? selected : normal);
            this.whole = whole;
            this.items = new ArrayList<>(items.length);
            int pos = 1;
            for (View item :
                    items) {
                item.setOnClickListener(l);
                item.setBackground((conditionValue & pos) != 0 ? selected : normal);
                this.items.add(item);
                pos <<= 1;
            }
            return true;
        }

        public int getValue() {
            if (items == null || whole == null || whole.getBackground() == selected) {
                return DEFAULT_CONDITION_VALUE;
            } else {
                int value = 0;
                int pos = 1;
                for (View item :
                        items) {
                    if (item.getBackground() == selected) {
                        value |= pos;
                    }
                    pos <<= 1;
                }
                return value;
            }
        }

        private final Drawable selected;
        private final Drawable normal;
        private View whole;
        private List<View> items;
    }

    public interface OnFilterChangedListener {
        void onChanged(boolean isRealChanged, int fromFilterConditionValue, int patternFilterConditionValue);
    }

    public FilterDialog() {
        fromConditionValue = DEFAULT_CONDITION_VALUE;
        patternConditionValue = DEFAULT_CONDITION_VALUE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, null);
        Drawable selectedDrawable = getResources().getDrawable(R.color.background_filter_selected);
        Drawable normalDrawable = getResources().getDrawable(R.color.background_filter_normal);
        final ConditionGroup fromCondition = new ConditionGroup(selectedDrawable, normalDrawable);
        fromCondition.init(fromConditionValue,
                view.findViewById(R.id.btn_filter_data_source_all),
                view.findViewById(R.id.btn_filter_data_source_wifi),
                view.findViewById(R.id.btn_filter_data_source_ble));
        final ConditionGroup patternCondition = new ConditionGroup(selectedDrawable, normalDrawable);
        patternCondition.init(patternConditionValue,
                view.findViewById(R.id.btn_filter_data_type_all),
                view.findViewById(R.id.btn_filter_data_type_analog),
                view.findViewById(R.id.btn_filter_data_type_status),
                view.findViewById(R.id.btn_filter_data_type_count));
        view.findViewById(R.id.btn_filter_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRealChanged = fromConditionValue != fromCondition.getValue() ||
                        patternConditionValue != patternCondition.getValue();
                fromConditionValue = fromCondition.getValue();
                patternConditionValue = patternCondition.getValue();
                if (onFilterChangedListener != null) {
                    onFilterChangedListener.onChanged(isRealChanged, fromConditionValue, patternConditionValue);
                }
                dismiss();
            }
        });
        view.findViewById(R.id.btn_filter_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
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

    public FilterDialog setOnFilterChangedListener(OnFilterChangedListener l) {
        onFilterChangedListener = l;
        return this;
    }

    private static final String FRAGMENT_TAG = "filter";
    private static final int DEFAULT_CONDITION_VALUE = 0x80000000;
    private OnFilterChangedListener onFilterChangedListener;
    private int fromConditionValue;
    private int patternConditionValue;
}
