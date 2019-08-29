package com.example.demos.forbidden;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import com.example.demos.restriction.funny.addworddemo.util.LogUtils;
import com.example.demos.utils.DensityUtil;

public class ForbiddenLayout extends FrameLayout implements IForbiddenLayout {

    private static final String TAG = "ForbiddenLayout";

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
    /**
     * 用于双指检测
     */
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Matrix mScaleMatrix = new Matrix();
    /**
     * 用来存放矩阵的9个值
     */
    private float[] matrixValues = new float[9];

    /**
     * 双击时,是否自动缩放
     */
    private boolean isAutoScale;
    private int mVerticalPadding = 0;

    private int mHorizontalPadding = 0;
    private int mLastPointCount;
    private float mLastY;
    private float mLastX;
    private boolean isCanDrag;
    private double mTouchSlop = 5;
    private Paint mPaint = new Paint();
    private boolean touch;
    private float lastScale = 1f;
    private float lastTranslateX = 0f;
    private float lastTranslateY = 0f;


    public ForbiddenLayout(@NonNull Context context) {
        this(context, null);
    }

    public ForbiddenLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForbiddenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(context);
    }

    private void init(Context context) {
        initPaint();
        //初始化GestureDetector
        mGestureDetector = new GestureDetector(context, onDoubleTapListener);
        //初始化ScaleGestureDetector
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    private void initPaint() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);
    }


    private boolean isScaling = false;
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "onScaleGesture ============== ");
            //获取当前的缩放比例
            isScaling = true;
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
                mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2f,
                        getHeight() / 2f);
                LogUtils.d("Scale center : " + detector.getFocusX() + " , " + detector.getFocusY()
                );
//                mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
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

    private void zoomChild() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ForbiddenView) {
                ForbiddenView forbiddenView = (ForbiddenView) childAt;
                forbiddenView.zoom(getScale(), getTranslateX(), getTranslateY());
            }
        }
    }

    private GestureDetector.SimpleOnGestureListener onDoubleTapListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap");

            float x = e.getX();
            float y = e.getY();
            if (getScale() < MIN_SCALE) {
                //如果当前的Scale值小于最小的Scale,那么双击的时候, 就扩大到最小的Scale
                ForbiddenLayout.this.postDelayed(new AutoScaleRunnable(MIN_SCALE, getWidth() / 2f,
                                getHeight() / 2f)
                        , 100);
                isAutoScale = true;
            } else {
                //否则,就要缩小到初始化的scale值
                ForbiddenLayout.this.postDelayed(new AutoScaleRunnable(initScale, getWidth() / 2f,
                                getHeight() / 2f)
                        , 100);
                isAutoScale = true;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Log.d(TAG, "onLongPress ==== ");
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDoubleTapListener onDown ============== ");
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onDoubleTapListener onSingleTapUp ============== ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return true;
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
            isScaling = true;
            mScaleMatrix.postScale(tempScale, tempScale, scaleCenterX, scaleCenterY);
            checkBorder();
            invalidate();
            float currentScale = getScale();
            //如果当前缩放值在合法范围,继续缩放
            if (tempScale > 1f && currentScale < mTargetScale
                    || tempScale < 1f && currentScale > mTargetScale) {
                ForbiddenLayout.this.postDelayed(this, 1);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        int childCount = getChildCount();
//        float scale = getScale();
//        float translateX = getTranslateX();
//        float translateY = getTranslateY();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = getChildAt(i);
//            int l = Math.round(childAt.getLeft() * scale + translateX);
//            int t = Math.round(childAt.getTop() * scale + translateY);
//            int r = Math.round(childAt.getRight() * scale + translateX);
//            int b = Math.round(childAt.getBottom() * scale + translateY);
//            childAt.layout(l, t, r, b);
////            FrameLayout.LayoutParams lp = (LayoutParams) getLayoutParams();
////            lp.leftMargin = l;
////            lp.topMargin = t;
////            childAt.setLayoutParams(lp);
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(mScaleMatrix);
//        zoomChild();
        drawBackground(canvas);
        drawCoor(canvas);
        canvas.restore();
    }

    private void drawCoor(Canvas canvas) {
        mPaint.setColor(Color.RED);
        canvas.drawLine(0, getHeight() / 2f, getWidth(), getHeight() / 2f, mPaint);
        canvas.drawLine(getWidth() / 2f, 0, getWidth() / 2f, getHeight(), mPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d(TAG, "onTouchEvent");
//        if (mGestureDetector.onTouchEvent(event))
//            return true;
//
//        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        //拿到触摸点的个数
        int pointerCount = event.getPointerCount();
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
            mLastPointCount = pointerCount;
        }
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
                LogUtils.d(TAG, "Action_Move");
                float dx = x - mLastX;
                float dy = y - mLastY;
                LogUtils.d(TAG, "move : dx : " + dx);
                LogUtils.d(TAG, "move : dy : " + dy);
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                LogUtils.d(TAG, "isCanDrag : " + isCanDrag);
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
                    isScaling = false;
                }
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    private void translateChild(float dx, float dy) {
        LogUtils.d(TAG,"translateChild : dx : " + dx + " , dy : " + dy);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ForbiddenView) {
                LogUtils.d(TAG, "translateChild : " + childAt.getLeft() + " , " + childAt.getRight());
                ForbiddenView forbiddenView = (ForbiddenView) childAt;
                forbiddenView.translate((
                        (dx + (childAt.getLeft() + childAt.getRight()) / 2f * getScale() - childAt.getWidth() / 2f) - childAt.getLeft()),
                        ((dy + (childAt.getTop() + childAt.getBottom()) / 2f * getScale() - childAt.getHeight() / 2f) - childAt.getTop())
                        );
            }
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
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        float width = getWidth();
        float height = getHeight();
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.01是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rect.width() >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }
            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }
        if (rect.height() >= height - 2 * mVerticalPadding) {
            if (rect.top > mVerticalPadding) {
                deltaY = -rect.top + mVerticalPadding;
            }
            if (rect.bottom < height - mVerticalPadding) {
                deltaY = height - mVerticalPadding - rect.bottom;
            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
        zoomChild();
        translateChild(getTranslateX(), getTranslateY());
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


}
