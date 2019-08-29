package com.example.demos.auto_text_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.demos.R;
import com.example.demos.restriction.funny.addworddemo.util.LogUtils;
import com.example.demos.utils.DensityUtil;

public class ForbiddenView extends View {
    private static final String TAG = "ForbiddenView";

    private static int NONE = 0; // 无
    private static int DRAG = 1; // 移动
    private static int ZOOM = 2; // 变换
    private static int DOUBLE_ZOOM = 3; //双指缩放
    private static int DELETE = 4; //删除

    private float baseValue = 0;
    //The original angle
    private float oldRotation;
    //旋转和缩放的中点
    private PointF midPoint;
    //拉伸按钮与view中心点的距离
    private float moveBitMapToMidPointLength;
    //保存刚开始按下的点
    private PointF startPoint = new PointF();
    private int addSize = 50;
    private int actionMode = 0;

    private float downX;
    private float downY;
    private Paint mPaint;
    private Paint pathPaint;
    private Paint bgRectFPaint;
    private Path mPath = new Path();
    private Bitmap bitDelete;
    private Bitmap bitMove;
    private RectF bgRectF = new RectF();
    private float bitHalf;
    private Matrix matrix = new Matrix();
    private String text = "禁区";

    private int mLeft, mTop, mRight, mBottom;
    private ViewGroup parent;

    public ForbiddenView(Context context, int left, int top, int right, int bottom, ViewGroup parent) {
        this(context, null, 0);
        this.parent = parent;
        this.mLeft = left;
        this.mRight = right;
        this.mTop = top;
        this.mBottom = bottom;
    }


    private ForbiddenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initBitmap();
    }

    private void initBitmap() {
        if (bitDelete == null) {
            bitDelete = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.btn_sticker_cancel_n);
        }
        if (bitMove == null) {
            bitMove = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.btn_sticker_turn_n);
        }
        bitHalf = bitDelete.getWidth() / 2f;
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1f);
        mPaint.setTextSize(DensityUtil.sp2px(getContext(), 14));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.RED);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 4));
        pathPaint.setStrokeWidth(2f);

        bgRectFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgRectFPaint.setStrokeWidth(1f);
        bgRectFPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgRectFPaint.setColor(Color.parseColor("#d06a6a"));
        bgRectFPaint.setAlpha(102);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.d(TAG, "onMeasure");

        int width = mRight - mLeft;
        int height = mBottom - mTop;
        setMeasuredDimension(width, height);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        mLeft = l;
        mTop = t;
        mRight = r;
        mBottom = b;
        bgRectF.set(0, 0, getWidth(), getHeight());
        mPath.reset();
        mPath.addRect(bgRectF, Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.d(TAG, "onDraw ");
        canvas.drawRect(bgRectF, bgRectFPaint);
        canvas.drawPath(mPath, pathPaint);
        canvas.drawBitmap(bitDelete, bgRectF.left - bitHalf, bgRectF.top - bitHalf, mPaint);
        canvas.drawBitmap(bitMove, bgRectF.right - bitHalf, bgRectF.bottom - bitHalf, mPaint);
        //文字x轴坐标
        float textWidth = mPaint.measureText(text);
        float x = getWidth() / 2f - textWidth / 2;
        //文字y轴坐标
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = getHeight() / 2f + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        LogUtils.d(TAG, "textSize : " + mPaint.getTextSize());
        canvas.drawText(text, x, y, mPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parent.bringChildToFront(this);
                baseValue = 0;
                downX = event.getX();
                downY = event.getY();
                startPoint.set(downX, downY);
                actionMode = DRAG;

                //构建一个拉伸按钮的矩形区域
                Rect moveRect = new Rect(getWidth() - addSize, getHeight() - addSize, getWidth() + addSize
                        , getHeight() + addSize);
                //构建一个删除按钮的矩形区域
                Rect delRect = new Rect(-addSize, -addSize, addSize, addSize);

                if (moveRect.contains((int) event.getX(), (int) event.getY())) {
                    LogUtils.d(TAG, "点中了拉伸按钮");
                    midPoint = midPoint(new PointF(0, 0), new PointF(getWidth(), getHeight()));
                    moveBitMapToMidPointLength = spacing(midPoint, new PointF(getWidth(), getHeight()));
                    actionMode = ZOOM;
                } else if (delRect.contains((int) event.getX(), (int) event.getY())) {
                    LogUtils.d(TAG, "点中了删除按钮");
                    parent.removeView(this);
                    actionMode = DELETE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (actionMode == DRAG) {
                    LogUtils.d(TAG, "move : drag");
                    final float xDistance = event.getX() - downX;
                    final float yDistance = event.getY() - downY;
                    if (xDistance != 0 && yDistance != 0) {
                        int l = (int) (getLeft() + xDistance);
                        int r = (int) (getRight() + xDistance);
                        int t = (int) (getTop() + yDistance);
                        int b = (int) (getBottom() + yDistance);
                        if (l < 0) {
                            l = 0;
                            r = getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = getHeight();
                        }
                        if (r > parent.getRight()) {
                            r = parent.getRight();
                            l = r - getWidth();
                        }
                        if (b > parent.getBottom()) {
                            b = parent.getBottom();
                            t = b - getHeight();
                        }
                        this.layout(l, t, r, b);
                    }
                } else if (actionMode == ZOOM) {
                    LogUtils.d(TAG, "move : zoom");
                    PointF movePoint = new PointF(event.getX(), event.getY());
                    float moveLength = spacing(startPoint, movePoint);
                    LogUtils.d(TAG, "move length : " + moveLength);
                    if (moveLength > 10f) {

                        midPoint = midPoint(new PointF(0, 0), new PointF(getWidth(), getHeight()));
//                        moveBitMapToMidPointLength = spacing(midPoint, new PointF(getWidth(), getHeight()));
//                        //滑动的点,距离view中心点的距离
//                        float moveToMidLength = spacing(midPoint, movePoint);
//                        LogUtils.d(TAG, "move toMidLength : " + moveToMidLength + " , " +
//                                "BitmapToMidPointLength : " + moveBitMapToMidPointLength);
//
//                        //移动的点距离中心点的距离,处理未滑动前,拉伸按钮图标距离中心点的距离之间的比值
//                        float scale = moveToMidLength / moveBitMapToMidPointLength;
//                        LogUtils.d(TAG, "move : scale : " + scale);

                        //当前视图右下角距离中心点的x和y方向的长度
                        float x = getWidth() - midPoint.x;
                        float y = getHeight() - midPoint.y;
                        //x方向滑动的距离
                        float xDistance = movePoint.x - midPoint.x;
                        float yDistance = movePoint.y - midPoint.y;
                        //x方向的缩放比
                        float xScale = xDistance / x;
                        //y方向的缩放比
                        float yScale = yDistance / y;


                        //增加了的width
                        float scaledWidth = getWidth() * (xScale - 1);
                        float scaledHeight = getHeight() * (yScale - 1);
                        int l = (int) (getLeft() - scaledWidth / 2);
                        int r = (int) (getRight() + scaledWidth / 2);
                        int t = (int) (getTop() - scaledHeight / 2);
                        int b = (int) (getBottom() + scaledHeight / 2);
                        if (l < 0) {
                            l = 0;
//                            r = getWidth();
                        }
                        if (t < 0) {
                            t = 0;
//                            b = getHeight();
                        }
                        if (r > parent.getRight()) {
                            r = parent.getRight();
//                            l = r - getWidth();
                        }
                        if (b > parent.getBottom()) {
                            b = parent.getBottom();
//                            t = b - getHeight();
                        }
                        layout(l, t, r, b);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d(TAG, "action_up : left : " + getLeft() + " , top : " + getTop());
                FrameLayout.LayoutParams lp =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = getLeft();
                lp.topMargin = getTop();
                setLayoutParams(lp);

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;

    }

    /**
     * 根据当前Canvas的matrix,获取与放大后的画布的大小相同的一个矩形
     * 用于检测边界
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = this.matrix;
        RectF rectF = new RectF();
        rectF.set(0, 0, getWidth(), getHeight());
        matrix.mapRect(rectF);
        return rectF;
    }


    private float rotationforTwo(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 得到两个点的距离
    private float spacing(PointF p1, PointF p2) {
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    // 得到两个点的中点
    private PointF midPoint(PointF p1, PointF p2) {
        PointF point = new PointF();
        float x = p1.x + p2.x;
        float y = p1.y + p2.y;
        point.set(x / 2, y / 2);
        return point;
    }

    // 旋转
    private float rotation(PointF p1, PointF p2) {
        double delta_x = (p1.x - p2.x);
        double delta_y = (p1.y - p2.y);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public int getmLeft() {
        return mLeft;
    }

    public void setmLeft(int mLeft) {
        this.mLeft = mLeft;
    }

    public int getmTop() {
        return mTop;
    }

    public void setmTop(int mTop) {
        this.mTop = mTop;
    }

    public int getmRight() {
        return mRight;
    }

    public void setmRight(int mRight) {
        this.mRight = mRight;
    }

    public int getmBottom() {
        return mBottom;
    }

    public void setmBottom(int mBottom) {
        this.mBottom = mBottom;
    }
}
