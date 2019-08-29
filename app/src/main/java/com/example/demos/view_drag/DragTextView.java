package com.example.demos.view_drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

import com.example.demos.R;
import com.example.demos.utils.DensityUtil;

public class DragTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "DragTextView";
    private Paint paint;
    private Matrix matrix = new Matrix();
    private String text;
    private Path path;
    private Paint pathPaint;
    private Bitmap delBitmap, dragBitmap;
    private float half;

    public DragTextView(Context context) {
        this(context, null);
    }

    public DragTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initPaint();
        setTextSize(DensityUtil.sp2px(getContext(), 10));
        setPadding(50, 50, 50, 50);
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.RED);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
        pathPaint.setStrokeWidth(8);
        if (delBitmap == null) {
            delBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sticker_cancel_n);
            half = delBitmap.getWidth() / 2f;
        }
        if (dragBitmap == null) {
            dragBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.btn_sticker_word_turn_n);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG,
                "onSizeChanged  " + getLeft() + " , " + getTop() + " , " + getRight() + " , " + getBottom() + " , " + w + " , " + h);
        if (path == null) {
            path = new Path();
            path.addRect(0, 0, w, h, Path.Direction.CCW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(matrix);
        canvas.drawColor(Color.parseColor("#55ff0000"));
        canvas.drawPath(path, pathPaint);
        canvas.drawBitmap(delBitmap, 0-half, 0-half, paint);
        canvas.drawBitmap(dragBitmap, getWidth()-half,getHeight()-half, paint);

    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
