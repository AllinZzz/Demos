package com.example.demos.forbidden3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.demos.R;
import com.example.demos.forbidden2.ForbiddenBean;

public class ForbiddenActivity extends AppCompatActivity {

    private ForbiddenLayout forbiddenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forbidden3);
        forbiddenLayout = findViewById(R.id.forbiddenLayout);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForbiddenBean forbiddenBean = new ForbiddenBean(forbiddenLayout);
                forbiddenLayout.addForbidden(forbiddenBean, 100, 100);
            }
        });
    }
}
