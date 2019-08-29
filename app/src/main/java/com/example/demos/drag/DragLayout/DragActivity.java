package com.example.demos.drag.DragLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.example.demos.R;

import java.util.ArrayList;
import java.util.List;

public class DragActivity extends AppCompatActivity {

    DragLayout dragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        dragLayout = findViewById(R.id.drag_layout);
        final List<Integer> rooms = new ArrayList<>();
        rooms.add(0);
        rooms.add(1);
        rooms.add(2);
        dragLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dragLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                dragLayout.addRooms(rooms);
            }
        });
    }
}
