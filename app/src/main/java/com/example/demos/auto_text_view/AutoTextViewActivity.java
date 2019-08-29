package com.example.demos.auto_text_view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.demos.R;


public class AutoTextViewActivity extends Activity {
    private static final String TAG = "AutoTextViewActivity";
    private ForbiddenLayout content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_text_view);
        content = findViewById(R.id.fl_content);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int left = content.getWidth() / 2 - 100;
                int right = content.getWidth() / 2 + 100;
                int top = content.getHeight() / 2 - 100;
                int bottom = content.getHeight() / 2 + 100;
                ForbiddenView autoTextView = new ForbiddenView(AutoTextViewActivity.this,
                        left, top, right, bottom, content);
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                autoTextView.setLayoutParams(layoutParams);
                content.addView(autoTextView);
            }
        });


    }
}
