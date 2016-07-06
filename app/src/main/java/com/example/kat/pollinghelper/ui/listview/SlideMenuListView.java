package com.example.kat.pollinghelper.ui.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by KAT on 2016/5/18.
 */
public class SlideMenuListView extends ListView {

    public interface OnItemClickListener {
        void onItemClick(SlideMenuListView view, MotionEvent event);
    }

    public SlideMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        minSlideDistance = 10.0f;
        isSlideJudgLock = false;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 如果一个ViewGroup的onInterceptTouchEvent()方法返回true，说明Touch事件被截获，
     * 子View不再接收到Touch事件，而是转向本ViewGroup的
     * onTouchEvent()方法处理。从Down开始，之后的Move，Up都会直接在onTouchEvent()方法中处理。
     * 先前还在处理touch event的child view将会接收到一个 ACTION_CANCEL。
     * 如果onInterceptTouchEvent()返回false，则事件会交给child view处理。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = true;
        if (!isIntercept(ev)) {
            result = false;
        } else {
            result = super.onInterceptTouchEvent(ev);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean dte = super.dispatchTouchEvent(event);
        if (MotionEvent.ACTION_UP == event.getAction() && !dte) {//onItemClick
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, event);
            }
        }
        return dte;
    }

    /**
     * 检测是ListView滑动还是item滑动 isSlideJudgLock 一旦判读是item滑动，则在up之前都是返回false
     */
    private boolean isIntercept(MotionEvent ev) {
        boolean result = true;
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastMotionX = x;
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSlideJudgLock) {
                    float deltaX = Math.abs(lastMotionX - x);
                    float deltaY = Math.abs(lastMotionY - y);
                    lastMotionX = x;
                    lastMotionY = y;
                    if (deltaX > deltaY && deltaX > minSlideDistance) {
                        isSlideJudgLock = true;
                        result = false;
                    }
                } else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isSlideJudgLock = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isSlideJudgLock = false;
                break;
        }
        return result;
    }

    private OnItemClickListener onItemClickListener;
    private final float minSlideDistance;
    private float lastMotionX;// 记住上次X触摸屏的位置
    private float lastMotionY;// 记住上次Y触摸屏的位置
    private boolean isSlideJudgLock;
}
