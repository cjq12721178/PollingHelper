package com.example.kat.pollinghelper.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by KAT on 2016/5/18.
 */
public class SlideListItem extends LinearLayout {
    private float lastMotionX;// 记住上次触摸屏的位置
    private int deltaX;
    private int backWidth;//滑动显示组件的宽度
    private float downX;
    private final int maxClickJudgDistance;//判断onItemClick的最大距离

    public SlideListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxClickJudgDistance = 5;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = dynamicWrapHeightContent(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        recordBackWidth(widthMeasureSpec, heightMeasureSpec);
    }

    private int dynamicWrapHeightContent(int widthMeasureSpec) {
        int heightMeasureSpec;
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {//采用最大的view的高度。
                height = h;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        return heightMeasureSpec;
    }

    private void recordBackWidth(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            if (i == 1) {
                backWidth = getChildAt(i).getMeasuredWidth();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int margeLeft = 0;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                int childWidth = view.getMeasuredWidth();
                // 将内部子孩子横排排列
                view.layout(margeLeft, 0, margeLeft + childWidth,
                        view.getMeasuredHeight());
                margeLeft += childWidth;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = true;
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                lastMotionX = x;
                downX = x;
            } break;
            case MotionEvent.ACTION_MOVE: {
                deltaX = (int) (lastMotionX - x);
                lastMotionX = x;
                int scrollX = getScrollX() + deltaX;
                if (scrollX > 0 && scrollX < backWidth) {
                    scrollBy(deltaX, 0);
                } else if (scrollX > backWidth) {
                    scrollTo(backWidth, 0);
                } else if (scrollX < 0) {
                    reset();
                }
            } break;
            case MotionEvent.ACTION_UP: {
                int scroll = getScrollX();
                if (Math.abs(x - downX) < maxClickJudgDistance) {
                    result = false;
                    break;
                }
                if (deltaX > 0) {
                    if (scroll > backWidth / 4) {
                        scrollTo(backWidth, 0);
                    } else {
                        reset();
                    }
                } else {
                    if (scroll > backWidth * 3 / 4) {
                        scrollTo(backWidth, 0);
                    } else {
                        reset();
                    }
                }
            } break;
            case MotionEvent.ACTION_CANCEL: {
                reset();
            } break;
            default: {
            } break;
        }
        return result;
    }

    public void reset() {
        scrollTo(0, 0);
    }
}
