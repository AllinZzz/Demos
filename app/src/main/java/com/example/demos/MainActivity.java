package com.example.demos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.demos.ViewStub.ViewStubActivity;
import com.example.demos.addview.AddViewActivity;
import com.example.demos.auto_text_view.AutoTextViewActivity;
import com.example.demos.card_view.CardViewActivity;
import com.example.demos.drag.DragLayout.DragActivity;
import com.example.demos.adapter.DemoAdapter;
import com.example.demos.forbidden.ForbiddenActivity;
import com.example.demos.forbidden2.ForbiddenActivity2;
import com.example.demos.forbidden3.ForbiddenLayout;
import com.example.demos.restriction.RestrictionActivity;
import com.example.demos.visible.VisibleActivity;
import com.example.demos.wifi.WiFiActivity;

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
                    case 1:
                        startActivity(new Intent(MainActivity.this, VisibleActivity.class));
                    case 2:
                        startActivity(new Intent(MainActivity.this, ViewStubActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, RestrictionActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, AddViewActivity.class));
                    case 5:
                        startActivity(new Intent(MainActivity.this,
                                com.example.demos.view_drag.DragActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this, AutoTextViewActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(MainActivity.this, CardViewActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(MainActivity.this,
                                ForbiddenActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(MainActivity.this, ForbiddenActivity2.class));
                        break;
                    case 10:
                        startActivity(new Intent(MainActivity.this, com.example.demos.forbidden3.ForbiddenActivity.class));
                        break;
                    case 11:
                        startActivity(new Intent(MainActivity.this, WiFiActivity.class));
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
                "DragLayout", "visible", "ViewStub", "禁区", "Add View", "Drag", "AutoTextView",
                "card View", "Forbidden", "Forbidden2", "Forbidden3", "WiFi", "More"
        };
        demoAdapter.setData(Arrays.asList(titles));
    }
}
