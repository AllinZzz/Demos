package com.example.demos;

import android.content.Intent;
import android.graphics.Canvas;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.demos.DragLayout.DragActivity;
import com.example.demos.adapter.DemoAdapter;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    private DemoAdapter demoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();

    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        demoAdapter = new DemoAdapter();
        demoAdapter.setOnItemClickListener(new DemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick : " + position);
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, DragActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "onItemLongClick : " + position);
            }
        });
        recyclerView.setAdapter(demoAdapter);

        setData();
    }

    private void setData() {
        String[] titles = new String[]{
                "DragLayout", "More"
        };
        demoAdapter.setData(Arrays.asList(titles));
    }
}
