package com.example.demos.forbidden2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.example.demos.restriction.funny.addworddemo.util.LogUtils;
import com.example.demos.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class ForbiddenLayout2 extends FrameLayout {

    private static final String TAG = "ForbiddenLayout2";

    private static final float OVER_SCALE_FACTOR = 1.0f;

    private Paint mPaint;

    float offsetX;
    float offsetY;
    float smallScale;
    float bigScale;
    boolean big;
    float currentScale;
    ObjectAnimator scaleAnimator;
    OverScroller scroller;

    private List<ForbiddenBean> forbiddenList = new ArrayList<>();

    GestureDetectorCompat detector;
    HenGestureListener henGestureListener = new HenGestureListener();
    HenFlingRunner henFlingRunner = new HenFlingRunner();
    ScaleGestureDetector scaleDetector;
    HenScaleListener scaleListener = new HenScaleListener();
    private Paint pathPaint;
    private Paint bgRectFPaint;
    private float realOffsetX;
    private float realOffsetY;

    public ForbiddenLayout2(@NonNull Context context) {
        this(context, null);
    }

    public ForbiddenLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForbiddenLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initPaint();
        detector = new GestureDetectorCompat(context, henGestureListener);
        scroller = new OverScroller(context);
        scaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFilterBitmap(true);


        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.RED);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 4));
        pathPaint.setStrokeWidth(2f);

        bgRectFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgRectFPaint.setStrokeWidth(1f);
        bgRectFPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgRectFPaint.setColor(Color.parseColor("#D8AFAF"));
        bgRectFPaint.setAlpha(102);
    }

    public float getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(float currentScale) {
        this.currentScale = currentScale;
        invalidate();
    }

    private ObjectAnimator getScaleAnimator() {
        if (scaleAnimator == null) {
            scaleAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0);
        }
        if (big) {
            scaleAnimator.setFloatValues(currentScale, bigScale);
        } else {
            scaleAnimator.setFloatValues(currentScale, smallScale);
        }
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!big) {
                    offsetX = 0;
                    offsetY = 0;
                }
            }
        });
        return scaleAnimator;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        smallScale = 1.0f;
        bigScale = 2 * OVER_SCALE_FACTOR;
        currentScale = smallScale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scaleFraction = (currentScale - smallScale) / (bigScale - smallScale);
        realOffsetX = offsetX * scaleFraction;
        realOffsetY = offsetY * scaleFraction;
        canvas.translate(realOffsetX, realOffsetY);
        canvas.scale(currentScale, currentScale, getWidth() / 2f, getHeight() / 2f);
        drawBackground(canvas);
        mPaint.setColor(Color.RED);
        canvas.drawLine(0, getHeight() / 2f, getWidth(), getHeight() / 2f, mPaint);
        canvas.drawLine(getWidth() / 2f, 0, getWidth() / 2f, getHeight(), mPaint);
        //画禁区
        for (ForbiddenBean bean : forbiddenList) {
            bean.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d(TAG, "onTouchEvent === ");

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                //先判断当前点击的位置,是否落在某个forbidden的删除按钮上
                ForbiddenBean forbiddenDelete = getForbiddenDelete(x, y);
                if (forbiddenDelete != null) {
                    if (forbiddenDelete.isFocus()) {
                        //如果当前forbidden已是选中状态,那么删除这个forbidden
                        removeForbidden(forbiddenDelete);
                    } else {
                        //如果当前forbidden非选中状态,那么把这个forbidden设置为选中
                        setForbiddenFocus(forbiddenDelete);
                    }
                }
                break;

        }

        boolean result = scaleDetector.onTouchEvent(event);
        if (!scaleDetector.isInProgress()) {
            result = detector.onTouchEvent(event);
        }
        return result;
    }

    private void drawBackground(Canvas canvas) {
        LogUtils.d(TAG, "drawBackground ==============");
        int space = DensityUtil.dp2px(getContext(), 10);   //长宽间隔
        int vertLine = 0;
        int hortLine = 0;
        mPaint.setStrokeWidth(3.0f);
        mPaint.setColor(Color.parseColor("#DDDDDD"));

        canvas.drawColor(Color.parseColor("#F0F0F0"));
        for (int i = 0; i <= (getWidth() > getHeight() ? getWidth() / space : getHeight() / space); i++) {
            canvas.drawLine(0, vertLine, getWidth(), vertLine, mPaint);  //画横线
            canvas.drawLine(hortLine, 0, hortLine, getHeight(), mPaint); //画竖线
            vertLine += space;
            hortLine += space;
        }
    }


    private class HenGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
            if (currentScale > smallScale) {
                offsetX -= distanceX;
                offsetY -= distanceY;
                fixOffsets();
                invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (currentScale > smallScale) {
                scroller.fling((int) offsetX, (int) offsetY, (int) velocityX, (int) velocityY,
                        -(int) ((getWidth() * bigScale - getWidth()) / 2),
                        (int) ((getWidth() * bigScale - getWidth()) / 2),
                        -(int) ((getHeight() * bigScale - getHeight()) / 2),
                        (int) ((getHeight() * bigScale - getHeight()) / 2));

                postOnAnimation(henFlingRunner);
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            big = !big;
            if (big) {
                offsetX = (e.getX() - getWidth() / 2f) * (1 - bigScale / smallScale);
                offsetY = (e.getY() - getHeight() / 2f) * (1 - bigScale / smallScale);
                fixOffsets();
                getScaleAnimator().start();
            } else {
                getScaleAnimator().start();
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    private class HenFlingRunner implements Runnable {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                offsetX = scroller.getCurrX();
                offsetY = scroller.getCurrY();
                invalidate();
                postOnAnimation(this);
            }
        }
    }

    private class HenScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        float initialScale;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentScale = initialScale * detector.getScaleFactor();
            big = currentScale > smallScale;
            if (currentScale <= smallScale) {
                currentScale = smallScale;

            }
            if (currentScale >= bigScale) {
                currentScale = bigScale;
            }
            invalidate();
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            initialScale = currentScale;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (currentScale == smallScale) {
                offsetX = 0;
                offsetY = 0;
            }
        }
    }

    private void fixOffsets() {
        offsetX = Math.min(offsetX, (getWidth() * bigScale - getWidth()) / 2);
        offsetX = Math.max(offsetX, -(getWidth() * bigScale - getWidth()) / 2);
        offsetY = Math.min(offsetY, (getHeight() * bigScale - getHeight()) / 2);
        offsetY = Math.max(offsetY, -(getHeight() * bigScale - getHeight()) / 2);
    }

    public void addForbidden(ForbiddenBean forbiddenBean, float width, float height) {

        for (ForbiddenBean bean : forbiddenList) {
            bean.setFocus(false);
        }
        forbiddenBean.setData(width, height, getWidth() / 2f, getHeight() / 2f);
        forbiddenList.add(forbiddenBean);
        invalidate();
    }

    /**
     * 根据传入的x,y值,判断是否是落在某个forbidden上,如果是,则返回该forbidden,否则返回null;
     *
     * @param x 点击事件的getX()
     * @param y 点击事件的getY()
     */
    private ForbiddenBean getForbidden(float x, float y) {
        for (int i = forbiddenList.size() - 1; i >= 0; i--) {
            ForbiddenBean forbiddenBean = forbiddenList.get(i);
            float[] point = new float[]{x, y};
            RectF forbiddenRectF = forbiddenBean.getForbiddenRectF();
            if (forbiddenRectF.contains(point[0], point[1])) {
                return forbiddenBean;
            }
        }
        return null;
    }

    /**
     * 根据传入的x,y值,判断点击是否落在某个forbidden的del按钮上
     *
     * @param x
     * @param y
     * @return
     */
    private ForbiddenBean getForbiddenDelete(float x, float y) {
        for (int i = forbiddenList.size() - 1; i >= 0; i--) {
            ForbiddenBean forbiddenBean = forbiddenList.get(i);
            RectF delRectF = forbiddenBean.getDelRectF();
            LogUtils.d(TAG,"getForbiddenDelete : delRectF : " + delRectF + " , forbid");
        }
        return null;
    }

    /**
     * 删除forbidden
     *
     * @param forbiddenDelete
     */
    private void removeForbidden(ForbiddenBean forbiddenDelete) {
        Bitmap bmpDelete = forbiddenDelete.getBmpDelete();
        Bitmap bmpScale = forbiddenDelete.getBmpScale();
        if (bmpScale != null && !bmpScale.isRecycled()) {
            bmpScale.recycle();
        }
        if (bmpDelete != null && !bmpDelete.isRecycled()) {
            bmpDelete.recycle();
        }
        forbiddenList.remove(forbiddenDelete);
        invalidate();
    }

    /**
     * 把指定的forbidden设置为focus
     *
     * @param forbiddenDelete
     */
    private void setForbiddenFocus(ForbiddenBean forbiddenDelete) {
        for (ForbiddenBean bean : forbiddenList) {
            if (bean == forbiddenDelete) {
                bean.setFocus(true);
            } else {
                bean.setFocus(false);
            }
        }
        invalidate();

    }


}
