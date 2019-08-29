package com.example.demos.ViewStub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.example.demos.R;

public class ViewStubActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean flag;
    private ViewStub stub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sutb);
        stub = findViewById(R.id.stub);
        findViewById(R.id.tv).setOnClickListener(this);
        stub.inflate();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv:
                flag = !flag;
                if (flag) {
                    Log.d("xxx", "111111");
                    stub.setLayoutResource(R.layout.layout_stub_1);
                } else {
                    Log.d("xxx", "222222");
                    stub.setLayoutResource(R.layout.layout_stub_2);
                }
                break;
        }
    }
}
