package com.example.kat.pollinghelper.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/6/12.
 */
public class LoadingDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_loading, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_loading);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_loading);
        imageView.startAnimation(animation);
        TextView textView = (TextView)view.findViewById(R.id.tv_loading);
        textView.setText(getTag());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.LoadingDialogTheme);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        isShow = true;
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        if (isShow) {
            super.dismiss();
            isShow = false;
        }
    }

    private boolean isShow;
}
