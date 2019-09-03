package com.example.demos.forbidden3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.demos.forbidden2.ForbiddenBean;
import com.example.demos.restriction.funny.addworddemo.util.LogUtils;
import com.example.demos.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class ForbiddenLayout extends View {

    private static final String TAG = "ForbiddenLayout";

    private Context mContext;

    private Paint mPaint = new Paint();
    //整个RoomView的大小
    private int totalWidth;
    private int totalHeight;

    private float scaleRatio;
    private ArrayList<ArrayList<PointF>> roomPoints = new ArrayList<>();
    private List<ForbiddenBean> forbiddenList = new ArrayList<>();
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
    private double mTouchSlop = 0;
    private ArrayList<ArrayList<PointF>> baseMapData;
    private List<ArrayList<PointF>> rooms = new ArrayList<>();
    private int forbiddenAction;
    private ForbiddenBean pressedForbidden;

    public ForbiddenLayout(Context context) {
        this(context, null);
    }

    public ForbiddenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPaint();
        mGestureDetector = new GestureDetector(context, onDoubleTapListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }


    private void initPaint() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        LogUtils.d(TAG, "onSizeChanged : w = " + w + " , h = " + h + " , oldw = " + oldw + ", " +
                "oldh = " + oldh);
        totalWidth = w;
        totalHeight = h;
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

    public void drawBaseMap(ArrayList<ArrayList<PointF>> roomsEdgePointsArray, float scaleRatio) {
        baseMapData = roomsEdgePointsArray;
        this.scaleRatio = scaleRatio + 0.8f;
        if (roomPoints != null) {
            roomPoints.clear();
            for (int i = 2; i < baseMapData.size(); i++) {
                roomPoints.add(baseMapData.get(i));
            }
        }
        invalidate();
    }

    private void drawBaseMapRooms(Canvas canvas) {
        LogUtils.d(TAG, "drawBaseMapRooms : =====================");

        if (baseMapData == null || baseMapData.size() == 0) {
            Log.w(TAG, "There is no point for the base map,do not draw anything !");
            return;
        }
        if (rooms != null) {
            rooms.clear();
        }
        mPaint.setStrokeWidth(scaleRatio);
        for (int roomIndex = 0; roomIndex < baseMapData.size(); roomIndex++)
            if (roomIndex == 0) {
                mPaint.setColor(Color.parseColor("#a2a2a2"));
                ArrayList<PointF> edgePoints = baseMapData.get(roomIndex);
                for (PointF edgePoint : edgePoints) {
                    canvas.drawPoint(edgePoint.x, edgePoint.y, mPaint);
                }
            } else if (roomIndex == 1) {
                mPaint.setColor(Color.parseColor("#929292"));
                ArrayList<PointF> edgePoints = baseMapData.get(roomIndex);
                for (PointF edgePoint : edgePoints) {
                    canvas.drawPoint(edgePoint.x, edgePoint.y, mPaint);
                }
            } else {
                mPaint.setColor(Color.parseColor("#d2d2d2"));
                ArrayList<PointF> roomsPoints = baseMapData.get(roomIndex);
                rooms.add(roomsPoints);
                for (PointF roomPoint : roomsPoints) {
                    canvas.drawPoint(roomPoint.x, roomPoint.y, mPaint);
                }
            }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.d(TAG, "onDraw");
        canvas.save();
        canvas.concat(mScaleMatrix);
        drawBackground(canvas);
        drawBaseMapRooms(canvas);
        //画禁区
        for (ForbiddenBean bean : forbiddenList) {
            bean.onDraw(canvas);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                //先判断当前点击的位置,是否落在某个forbidden的删除按钮上
                pressedForbidden = getForbiddenDelete(x, y);
                if (pressedForbidden != null) {
                    if (pressedForbidden.isFocus()) {
                        //如果当前forbidden已是选中状态,那么删除这个forbidden
                        removeForbidden(pressedForbidden);
                    } else {
                        //如果当前forbidden非选中状态,那么把这个forbidden设置为选中
                        setForbiddenFocus(pressedForbidden);
                    }
                    return true;
                }

                //再判断是否点击了某个forbidden的缩放按钮上
                pressedForbidden = getForbiddenScale(x, y);
                if (pressedForbidden != null) {
                    if (pressedForbidden.isFocus()) {
                        //如果当前forbidden处于选中状态,那么让forbidden自身进行缩放
                        forbiddenAction = ForbiddenBean.FORBIDDEN_ACTION_SCALE;
                    } else {
                        //如果当前forbidden非选中状态,那么把这个forbidden设置为选中
                        setForbiddenFocus(pressedForbidden);
                    }
                } else {
                    //最后判断是否点击了某个forbidden上
                    pressedForbidden = getForbidden(x, y);
                    if (pressedForbidden != null) {
                        forbiddenAction = ForbiddenBean.FORBIDDEN_ACTION_MOVE;
                        setForbiddenFocus(pressedForbidden);
                    }
                }
                break;
            default:
                break;
        }

        if (pressedForbidden != null) {
            pressedForbidden.onTouchEvent(event,forbiddenAction);
            invalidate();
            return true;
        }


        boolean result = mScaleGestureDetector.onTouchEvent(event);
        if (!mScaleGestureDetector.isInProgress()) {
            result = mGestureDetector.onTouchEvent(event);
        }
        return result;
    }

    private void calculateXYBelongsToRoom(final float x, final float y) {
        found:
        for (int roomIndex = 0; roomIndex < roomPoints.size(); roomIndex++) {
            //遍历每个room
            for (PointF point : roomPoints.get(roomIndex)) {
                int xx = (int) (point.x * getScale() + getTranslateX());
                int yy = (int) (point.y * getScale() + getTranslateY());
                if (Math.abs(xx - x) <= scaleRatio * getScale() && Math.abs(yy - y) <= scaleRatio * getScale()) {
                    LogUtils.d(TAG, "selectedRoom 当前选中的点在 : " + roomIndex + " 房间号");
                    //说明传入的x,y属于当前这个房间里的点,取出房间号,根据房间号判断是否已经选择
//
                    break found;
                }
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            LogUtils.d(TAG, "onScaleGesture ============== ");
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
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LogUtils.d(TAG, "onDoubleTap");
            if (isAutoScale) {
                return true;
            }
            float x = e.getX();
            float y = e.getY();
            if (getScale() < MIN_SCALE) {
                //如果当前的Scale值小于最小的Scale,那么双击的时候, 就扩大到最小的Scale
                ForbiddenLayout.this.postDelayed(new AutoScaleRunnable(MIN_SCALE, x, y), 1);
                isAutoScale = true;
            } else {
                //否则,就要缩小到初始化的scale值
                ForbiddenLayout.this.postDelayed(new AutoScaleRunnable(initScale, x, y), 1);
                isAutoScale = true;
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (getScale() > initScale) {

                mScaleMatrix.postTranslate(-distanceX, -distanceY);
                checkBorder();
                invalidate();
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LogUtils.d(TAG, "onSingleTapUp ===== ");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtils.d(TAG, "onSingleTapConfirmed ==== ");
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
            mScaleMatrix.postScale(tempScale, tempScale, scaleCenterX, scaleCenterY);
            LogUtils.d(TAG, "继续缩放 ===================== " + getScale());
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
                LogUtils.d(TAG, "缩放至目标大小 ===================== " + getScale());
                invalidate();
                isAutoScale = false;
            }
        }
    }

    /**
     * 根据传入的点值,判断时候能被拖动
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
    public float getScale() {
        mScaleMatrix.getValues(matrixValues);
//        float tranY = matrixValues[Matrix.MTRANS_Y] * dy;
        return matrixValues[Matrix.MSCALE_X];
    }

    public float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    public float getTranslateY() {
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
            RectF forbiddenRectF = forbiddenBean.getForbiddenRectF();
            RectF tmp = new RectF();
            tmp.set(forbiddenRectF.left * getScale() + getTranslateX(),
                    forbiddenRectF.top * getScale() + getTranslateY(),
                    forbiddenRectF.right * getScale() + getTranslateX(),
                    forbiddenRectF.bottom * getScale() + getTranslateY());
            LogUtils.d(TAG, "getForbiddenDelete x " + x + " y " + y + " tmp : " + tmp);
            if (tmp.contains(x, y)) {
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
            RectF tmp = new RectF();
            tmp.set(delRectF.left * getScale() + getTranslateX(),
                    delRectF.top * getScale() + getTranslateY(),
                    delRectF.right * getScale() + getTranslateX(),
                    delRectF.bottom * getScale() + getTranslateY());
            LogUtils.d(TAG, "getForbiddenDelete x " + x + " y " + y + " tmp : " + tmp);
            if (tmp.contains(x, y)) {
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
    private ForbiddenBean getForbiddenScale(float x, float y) {
        for (int i = forbiddenList.size() - 1; i >= 0; i--) {
            ForbiddenBean forbiddenBean = forbiddenList.get(i);
            RectF scaleRectF = forbiddenBean.getScaleRectF();
            RectF tmp = new RectF();
            tmp.set(scaleRectF.left * getScale() + getTranslateX(),
                    scaleRectF.top * getScale() + getTranslateY(),
                    scaleRectF.right * getScale() + getTranslateX(),
                    scaleRectF.bottom * getScale() + getTranslateY());
            LogUtils.d(TAG, "getForbiddenDelete x " + x + " y " + y + " tmp : " + tmp);
            if (tmp.contains(x, y)) {
                return forbiddenBean;
            }
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
        if (forbiddenList.size() >= 1) {
            forbiddenList.get(forbiddenList.size() - 1).setFocus(true);
        }
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
