package com.example.demos.drag.DragLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.example.demos.R;
import com.example.demos.restriction.funny.addworddemo.util.LogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DragLayout extends ViewGroup {

    private static final String TAG = "DragLayout";

    private static final int LONG_PRESS_TIME = 800;

    private Paint paint;
    /**
     * 用来存放矩阵的9个值
     */
    private float[] matrixValues = new float[9];
    /**
     * 最大的缩放比率(相较于原始尺寸)
     */
    public static float MAX_SCALE = 2.0f;
    /**
     * 最小缩放比率(相较于原始尺寸)
     */
    public static float MIN_SCALE = 2.0f;
    /**
     * 初始化时的缩放比率
     */
    private float initScale = 1.0f;

    private Matrix mScaleMatrix = new Matrix();
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;


    private boolean isAutoScale;
    private boolean isAboutSchema;
    private float mLastPointCount;
    private boolean isCanDrag;
    private float mLastX;
    private float mLastY;
    private int mHorizontalPadding;
    private int mVerticalPadding;
    private int mTouchSlop;
    private boolean isWorking;
    private int width;
    private int height;


    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        initTypeArray(context, attrs);
        mGestureDetector = new GestureDetector(context, onDoubleTapListener);
        //初始化ScaleGestureDetector
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);

    }

    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //获取当前的缩放比例
            float currentScale = getScale();
            float scaleFactor = detector.getScaleFactor();
            //控制缩放范围,设置当前状态下缩放的最大和最小值
            if ((currentScale < MAX_SCALE && scaleFactor > 1.0f)
                    //当前缩放比例小于最大比例,并且现在的缩放因子大于1,表示正在放大
                    || (currentScale > initScale && scaleFactor < 1.0f)) {
                //当前缩放比例大于初始化的缩放比例, 并且现在的缩放因子小于1,表示正在缩小
                //设置当前状态下的缩放因子
                if (scaleFactor * currentScale < initScale) {
                    scaleFactor = initScale / currentScale;
                }
                if (scaleFactor * currentScale > MAX_SCALE) {
                    scaleFactor = MAX_SCALE / currentScale;
                }
                mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                checkBorder();
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    };
    private GestureDetector.SimpleOnGestureListener onDoubleTapListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap");
            if (isAutoScale) {
                return true;
            }
            float x = e.getX();
            float y = e.getY();
            if (getScale() < MIN_SCALE) {
                //如果当前的Scale值小于最小的Scale,那么双击的时候, 就扩大到最小的Scale
                DragLayout.this.postDelayed(new AutoScaleRunnable(MIN_SCALE, x, y), 1);
                isAutoScale = true;
            } else {
                //否则,就要缩小到初始化的scale值
                DragLayout.this.postDelayed(new AutoScaleRunnable(initScale, x, y), 1);
                isAutoScale = true;
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isAboutSchema) {
//                calculateXYBelongsToRoom(e.getX(), e.getY());
                return true;
            } else {
                return false;
            }
        }
    };


    private class AutoScaleRunnable implements Runnable {

        private static final float BIGGER = 1.07f;

        private static final float SMALLER = 0.93f;

        private float mTargetScale;

        private float tempScale;
        /**
         * 缩放的中心
         */
        private float scaleCenterX;
        private float scaleCenterY;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         * @param x
         * @param y
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.scaleCenterX = x;
            this.scaleCenterY = y;

            if (getScale() < mTargetScale) {
                //如果当前缩放比率小于传入的目标缩放值,那么就要放大
                tempScale = BIGGER;
            } else {
                tempScale = SMALLER;
            }
        }

        @Override
        public void run() {
            //进行缩放
            mScaleMatrix.postScale(tempScale, tempScale, scaleCenterX, scaleCenterY);
            checkBorder();
            invalidate();
            float currentScale = getScale();
            //如果当前缩放值在合法范围,继续缩放
            if (tempScale > 1f && currentScale < mTargetScale
                    || tempScale < 1f && currentScale > mTargetScale) {
                DragLayout.this.postDelayed(this, 1);
            } else {
                //否则,不在合法范围 , 那么就直接设置成目标缩放比率
                float deltaScale = mTargetScale / currentScale;  //根据当前的scale值,计算距离目标scale值的差值,只需要缩放该差值就够了
                mScaleMatrix.postScale(deltaScale, deltaScale, scaleCenterX, scaleCenterY);
                checkBorder();
                invalidate();
                isAutoScale = false;
            }
        }
    }


    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragLayout);
        if (typedArray != null) {
            // TODO: 2019/3/6 do some init job here
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        //拿到触摸点的个数
        int pointerCount = event.getPointerCount();
        float x = 0, y = 0;
        //计算多个触摸点的x和y的均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        /**
         * 每次触摸点发生变化的时候,重置mLastX,mLastY
         */
        if (mLastPointCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointCount = pointerCount;
        if (pointerCount > 1) {
            mScaleGestureDetector.onTouchEvent(event);
            Log.d(TAG, "onTouchEvent mScaleGestureDetector");
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            Log.d(TAG, "onTouchEvent mGestureDetector");
            return true;
        }


        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    RectF rect = getMatrixRectF();
                    if (rect.width() <= getWidth() - 2 * mHorizontalPadding) {
                        //如果宽度小于屏幕宽度,那么禁止左右移动
                        dx = 0;
                    }
                    if (rect.height() <= getHeight() - 2 * mVerticalPadding) {
                        //如果高度小于屏幕高度,那么禁止上下移动
                        dy = 0;
                    }
                    mScaleMatrix.postTranslate(dx, dy);
                    checkBorder();
                    invalidate();
                }
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_UP");
                mLastPointCount = 0;
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.save();
        canvas.concat(mScaleMatrix);
        drawBackground(canvas);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d(TAG, "dispatchDraw");
        super.dispatchDraw(canvas);
    }


    private void drawBackground(Canvas canvas) {
        Log.d(TAG, "drawBackground ==============");
        float space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        int verLine = 0;
        int hortLine = 0;
        Paint bgPaint = getPaint();
        bgPaint.setStrokeWidth(2.0f);
        bgPaint.setColor(Color.parseColor("#DDDDDD"));
        canvas.drawColor(Color.parseColor("#F0F0F0"));
        for (int i = 0; i <= (getWidth() > getHeight() ? getWidth() / space : getHeight() / space); i++) {
            canvas.drawLine(0, verLine, getWidth(), verLine, bgPaint);  //画横线
            canvas.drawLine(hortLine, 0, hortLine, getHeight(), bgPaint); //画竖线
            verLine += space;
            hortLine += space;
        }
    }

    private Paint getPaint() {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return paint;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout " + changed + " " + l + " " + t + " " + r + " " + b);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LogUtils.d(TAG, "onLayout : measuredWidth : " + child.getMeasuredWidth());
            int childWidth = (int) (child.getMeasuredWidth() * getScale());
            int childHeight = (int) (child.getMeasuredHeight() * getScale());
            PointF position = ((DragImageView) child).getPosition();
            int x = (int) ((int) (position.x) * getScale() + getTranslateX());
            int y = (int) ((int) (position.y) * getScale() + getTranslateY());
            child.layout(((x - childWidth / 2)), ((y - childHeight / 2)), ((x + childWidth / 2)), ((y + childHeight / 2)));
        }
    }

    public void addRooms(List<Integer> rooms) {
        Log.d(TAG, "addRooms");
        removeAllViews();
        for (int i = 0; i < rooms.size(); i++) {
            Integer room = rooms.get(i);
            DragImageView dragImageView = new DragImageView(getContext());
            dragImageView.setPosition(new PointF((float) width / 2f + i * 100,
                    (float) height / 2f + i * 100));
            dragImageView.setRoomNum(room);
            addView(dragImageView);

        }
    }


    /**
     * 根据传入的点值,判断是否能被拖动
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    /**
     * 获取当前的缩放比率
     *
     * @return
     */
    private float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 获取当前在横向距离上的位移
     *
     * @return
     */
    private float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    /**
     * 获取当前在纵向距离上的位移
     *
     * @return
     */
    private float getTranslateY() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 检测边界
     */
    private void checkBorder() {
        LogUtils.d(TAG, "checkBorder === ");
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        float width = getWidth();
        float height = getHeight();
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.01是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }
            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }
        if (rect.height() + 0.01 >= height - 2 * mVerticalPadding) {
            if (rect.top > mVerticalPadding) {
                deltaY = -rect.top + mVerticalPadding;
            }
            if (rect.bottom < height - mVerticalPadding) {
                deltaY = height - mVerticalPadding - rect.bottom;
            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
        View childAt = getChildAt(0);
        if (childAt != null) {
            childAt.requestLayout();
        }
    }

    /**
     * 根据当前Canvas的matrix,获取与放大后的画布的大小相同的一个矩形
     * 用于检测边界
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        rectF.set(0, 0, getWidth(), getHeight());
        matrix.mapRect(rectF);
        return rectF;
    }

    public void setWorking(boolean isWorking) {
        this.isWorking = isWorking;
        invalidate();
    }
}
