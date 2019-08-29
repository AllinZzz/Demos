package com.example.demos.drag.DragLayout;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class RoomDrawable extends Drawable {

    private static final String TAG = "RoomDrawable";
    private Paint mPaint;
    private Path path;
    private String sequence;

    public RoomDrawable(String sequence) {
        this.sequence = sequence;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Log.d(TAG, "draw === ");
        drawCircle(canvas);
        drawText(canvas);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        Log.d(TAG, "onBoundsChange : " + bounds);
        path.reset();
        path.addCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2f, Path.Direction.CCW);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.GREEN);
        canvas.clipPath(path);
        canvas.drawColor(Color.BLUE);
        canvas.drawPath(path, mPaint);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(sequence)) {
            Log.d(TAG, "drawText : but the sequence is null ,return");
            return;
        }
        Rect bounds = getBounds();
        mPaint.reset();
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(bounds.width() / 1.5f);
        float textWidth = mPaint.measureText(sequence);
        //文字的x坐标
        float x = (bounds.width() - textWidth) / 2.0f;
        //文字的y坐标
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = (bounds.height() + (Math.abs(fontMetrics.ascent) - fontMetrics.descent)) / 2.0f;
        canvas.drawText(sequence, x, y, mPaint);
    }
}
