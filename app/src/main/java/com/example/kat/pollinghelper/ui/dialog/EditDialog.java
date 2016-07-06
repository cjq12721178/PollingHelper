package com.example.kat.pollinghelper.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/6/7.
 */
public class EditDialog extends DialogFragment {

    public interface OnClickPositiveListener {
        void onClick(String content);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_with_edittext, null);
        etContent = (EditText) view.findViewById(R.id.et_polling_config_dialog_content);
        etContent.setText(content);
        builder.setTitle(getTag());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ui_prompt_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClickPositiveListener != null) {
                    onClickPositiveListener.onClick(etContent.getText().toString());
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        window.setAttributes(params);
    }

    public void show(FragmentManager manager, String tag, String content) {
        this.content = content;
        super.show(manager, tag);
    }

    public void setOnClickPositiveListener(OnClickPositiveListener onClickPositiveListener) {
        this.onClickPositiveListener = onClickPositiveListener;
    }

    private OnClickPositiveListener onClickPositiveListener;
    private EditText etContent;
    private String content;
}
