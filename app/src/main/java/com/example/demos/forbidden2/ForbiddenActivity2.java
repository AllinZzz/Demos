package com.example.demos.forbidden2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.demos.R;

public class ForbiddenActivity2 extends AppCompatActivity {

    private static final String TAG = "ForbiddenActivity2";
    private ForbiddenLayout2 forbiddenLayout2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forbidden2);
        forbiddenLayout2 = findViewById(R.id.fl_root);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
