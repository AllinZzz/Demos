package com.example.demos.addview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.demos.R;

public class AddViewActivity extends Activity {

    FrameLayout frame;
    int times = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view);
        frame = findViewById(R.id.fl);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                times++;
                TextView tv = new TextView(AddViewActivity.this);
                tv.setText(String.valueOf(times));
                tv.setLeft(30 * times);
                tv.setTop(10*times);
                tv.setRight(50*times);
                tv.setBottom(20*times);
                tv.setTextSize(20);
                frame.addView(tv);
            }
        });

    }
}
