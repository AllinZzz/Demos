package com.example.demos.forbidden;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.demos.R;

public class ForbiddenActivity extends AppCompatActivity {

    private static final String TAG = "ForbiddenActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forbidden);
        final ForbiddenLayout view = findViewById(R.id.forbidden);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForbiddenView forbiddenView = new ForbiddenView(ForbiddenActivity.this, 100, 100,
                        400, 400, view);
                FrameLayout.LayoutParams lp =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = Math.round(view.getWidth() / 2f - 150);
                lp.topMargin = Math.round(view.getHeight() / 2f - 150);
                forbiddenView.setLayoutParams(lp);
                view.addView(forbiddenView);
            }
        });
    }
}
