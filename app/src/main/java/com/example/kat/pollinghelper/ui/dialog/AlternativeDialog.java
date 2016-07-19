package com.example.kat.pollinghelper.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/7/18.
 */
public class AlternativeDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_alternative, null);
        view.measure(0, 0);
        TextView message = (TextView)view.findViewById(R.id.tv_message);
        message.setText(getTag());
        message.setWidth(view.getMeasuredWidth());
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onClick(v);
                }
                dismiss();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onClick(v);
                }
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlternativeDialogTheme);
    }

    public AlternativeDialog setOnConfirmClickListener(View.OnClickListener l) {
        onConfirmClickListener = l;
        return this;
    }

    public AlternativeDialog setOnCancelClickListener(View.OnClickListener l) {
        onCancelClickListener = l;
        return this;
    }

    private View.OnClickListener onConfirmClickListener;
    private View.OnClickListener onCancelClickListener;
}
