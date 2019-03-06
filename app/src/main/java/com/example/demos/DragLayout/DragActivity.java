package com.example.demos.DragLayout;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demos.R;

public class DragActivity extends AppCompatActivity {

    DragLayout dragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        dragLayout = findViewById(R.id.drag_layout);

        TextView textView = new TextView(this);
        textView.setTextSize(50);
        textView.setBackgroundColor(Color.parseColor("#55ff0000"));
        textView.setText("aaaaaaa");
        dragLayout.addView(textView);
    }
}
