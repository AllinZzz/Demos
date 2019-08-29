package com.example.demos.auto_text_view;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.demos.restriction.funny.addworddemo.util.LogUtils;

import java.util.List;

public class ForbiddenLayout extends FrameLayout {
    private static final String TAG = "ForbiddenLayout";

    public ForbiddenLayout(@NonNull Context context) {
        this(context, null);
    }

    public ForbiddenLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForbiddenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.d(TAG, "onLayout : ");

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ForbiddenView child = (ForbiddenView) getChildAt(i);
            LogUtils.d(TAG,
                    "onLayout: child " + child.getmLeft() + " , " + child.getmTop() + " , " + child.getmRight() + " , " + child.getmBottom());
            child.layout(child.getmLeft(), child.getmTop(), child.getmRight(), child.getmBottom());
        }
    }

}
