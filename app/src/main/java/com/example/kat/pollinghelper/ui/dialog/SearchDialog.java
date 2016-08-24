package com.example.kat.pollinghelper.ui.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/8/23.
 */
public class SearchDialog extends DialogFragment {

    public interface OnSearchListener {
        void onSearch(boolean isSearchConditionChanged, String[] searchContents);
    }

    private class OnClickSearchListener implements View.OnTouchListener {

        public OnClickSearchListener(EditText etSearchText) {
            editText = etSearchText;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // editText.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
            Drawable drawable = editText.getCompoundDrawables()[2];
            //如果右边没有图片，不再处理
            if (drawable == null)
                return false;
            //如果不是按下事件，不再处理
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > editText.getWidth()
                    - editText.getPaddingRight()
                    - drawable.getIntrinsicWidth()){
                String newSearchText = editText.getText().toString();
                if (onSearchListener != null) {
                    onSearchListener.onSearch(!newSearchText.equals(searchText), newSearchText.split(" "));
                }
                searchText = newSearchText;
                dismiss();
            }
            return false;
        }

        private final EditText editText;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_search, null);
        EditText editText = (EditText)view.findViewById(R.id.et_search_content);
        editText.setText(searchText);
        editText.setHint(summary);
        editText.setOnTouchListener(new OnClickSearchListener(editText));
        return view;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setOnSearchListener(OnSearchListener l) {
        onSearchListener = l;
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

    private static final String FRAGMENT_TAG = "search";
    private OnSearchListener onSearchListener;
    private String summary;
    private String searchText;
}
