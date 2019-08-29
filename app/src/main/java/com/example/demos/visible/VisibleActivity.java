package com.example.demos.visible;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.demos.R;

public class VisibleActivity extends AppCompatActivity {

    private Button btn_ll;
    private RelativeLayout rl;
    private LinearLayout ll;
    private boolean flag_rl;
    private boolean flag_ll;
    private Button btn_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visible);

        btn_ll = findViewById(R.id.btn_ll_visible);
        btn_rl = findViewById(R.id.btn_rl_visible);
        rl = findViewById(R.id.rl);
        ll = findViewById(R.id.ll);

        btn_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_rl = !flag_rl;
                if (flag_rl) {
                    rl.setVisibility(View.VISIBLE);
                } else {
                    rl.setVisibility(View.GONE);
                }
            }
        });

        btn_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_ll = !flag_ll;
                ll.setVisibility(flag_ll ? View.VISIBLE : View.GONE);
            }
        });

    }
}
