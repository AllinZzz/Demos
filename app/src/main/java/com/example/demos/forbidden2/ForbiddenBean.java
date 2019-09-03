package com.example.demos.forbidden2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.example.demos.R;
import com.example.demos.forbidden3.ForbiddenLayout;
import com.example.demos.restriction.funny.addworddemo.util.LogUtils;

public class ForbiddenBean {

    private static final String TAG = "ForbiddenBean";
    private static final int DEL_SCALE_PADDING = 10;
    public static final int FORBIDDEN_ACTION_SCALE = 0x001;
    public static final int FORBIDDEN_ACTION_MOVE = 0x002;
    private final ForbiddenLayout parent;

    private float width;  //禁区的宽
    private float height;  //禁区的高
    private float centerX;  //禁区的中心点x值
    private float centerY;  //禁区的中心点y值
    private boolean focus;  //当前是否被选中
    private Path path = new Path();  //禁区的虚线路径
    private RectF forbiddenRectF = new RectF();  //禁区的矩形
    private Paint mPatin;  //画位图的画笔
    private Paint pathPaint;  //虚线路径画笔
    private Paint bgRectFPaint;  //底色画笔
    private Bitmap bmpDelete;  //删除按钮位图
    private Bitmap bmpScale;   //缩放按钮位图
    private RectF delRectF = new RectF();  //删除按钮的矩形
    private RectF scaleRectF = new RectF();  //缩放按钮的矩形
    private Canvas canvas;
    private float downX;
    private float downY;


    public ForbiddenBean(ForbiddenLayout parent) {
        this.parent = parent;
        initPaint();
        initBitmap(parent.getContext());
        focus = true;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public RectF getForbiddenRectF() {
        return forbiddenRectF;
    }

    public void setForbiddenRectF(RectF forbiddenRectF) {
        this.forbiddenRectF = forbiddenRectF;
    }

    public Paint getmPatin() {
        return mPatin;
    }

    public void setmPatin(Paint mPatin) {
        this.mPatin = mPatin;
    }

    public Paint getPathPaint() {
        return pathPaint;
    }

    public void setPathPaint(Paint pathPaint) {
        this.pathPaint = pathPaint;
    }

    public Paint getBgRectFPaint() {
        return bgRectFPaint;
    }

    public void setBgRectFPaint(Paint bgRectFPaint) {
        this.bgRectFPaint = bgRectFPaint;
    }

    public Bitmap getBmpDelete() {
        return bmpDelete;
    }

    public void setBmpDelete(Bitmap bmpDelete) {
        this.bmpDelete = bmpDelete;
    }

    public Bitmap getBmpScale() {
        return bmpScale;
    }

    public void setBmpScale(Bitmap bmpScale) {
        this.bmpScale = bmpScale;
    }

    public RectF getDelRectF() {
        return delRectF;
    }

    public void setDelRectF(RectF delRectF) {
        this.delRectF = delRectF;
    }

    public RectF getScaleRectF() {
        return scaleRectF;
    }

    public void setScaleRectF(RectF scaleRectF) {
        this.scaleRectF = scaleRectF;
    }

    public void setData(float width, float height, float centerX, float centerY) {
        this.width = width;
        this.height = height;
        this.centerX = centerX;
        this.centerY = centerY;
        setRectF(width, height, centerX, centerY);
    }

    private void setRectF(float width, float height, float centerX, float centerY) {
        forbiddenRectF.set(centerX - width / 2f, centerY - height / 2f, centerX + width / 2f,
                centerY + height / 2f);
        delRectF.set(centerX - width / 2f - bmpDelete.getWidth() / 2f - DEL_SCALE_PADDING,
                centerY - height / 2f - bmpDelete.getHeight() / 2f - DEL_SCALE_PADDING,
                centerX - width / 2f + bmpDelete.getWidth() / 2f + DEL_SCALE_PADDING,
                centerY - height / 2f + bmpDelete.getHeight() / 2f + DEL_SCALE_PADDING);
        scaleRectF.set(centerX + width / 2f - bmpScale.getWidth() / 2f - DEL_SCALE_PADDING,
                centerY + height / 2f - bmpScale.getHeight() / 2f - DEL_SCALE_PADDING,
                centerX + width / 2f + bmpScale.getWidth() / 2f + DEL_SCALE_PADDING,
                centerY + height / 2f + bmpScale.getHeight() / 2f + DEL_SCALE_PADDING);
        path.reset();
        path.addRect(forbiddenRectF, Path.Direction.CCW);
    }

    private void initBitmap(Context context) {
        bmpDelete = getAvatar(context.getResources(), R.drawable.img_forbidden_del, 30);
        bmpScale = getAvatar(context.getResources(), R.drawable.img_forbidden_scale, 30);
    }

    private void initPaint() {

        mPatin = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPatin.setStyle(Paint.Style.STROKE);

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

    public void onDraw(Canvas canvas) {
        LogUtils.d(TAG, "onDraw == ");
        this.canvas = canvas;
        canvas.drawRect(forbiddenRectF, bgRectFPaint);
        canvas.drawPath(path, pathPaint);
        if (focus) {
            canvas.drawBitmap(bmpDelete, centerX - width / 2f - bmpDelete.getWidth() / 2f,
                    centerY - height / 2f - bmpDelete.getHeight() / 2f, mPatin);
            canvas.drawBitmap(bmpScale, centerX + width / 2f - bmpScale.getWidth() / 2f,
                    centerY + height / 2f - bmpScale.getHeight() / 2f, mPatin);
        }
    }


    public void onTouchEvent(MotionEvent event, int forbiddenAction) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if (forbiddenAction == FORBIDDEN_ACTION_MOVE) {
                    //移动forbidden,改变forbidden的centerX,centerY,然后刷新
                    float distanceX = (moveX - downX) / parent.getScale();
                    float distanceY = (moveY - downY) / parent.getScale();
                    centerX += distanceX;
                    centerY += distanceY;
                    LogUtils.d(TAG, "ForbiddenBean onTouch : distanceX : " + distanceX + " , distanceY " +
                            ": " + distanceY + " , centerX : " + centerX + " , centerY : " + centerY);
                    setData(width, height, centerX, centerY);
                } else {
                    //缩放forbidden,保留左上角坐标不变,进行缩放
                }
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

    }


    @Override
    public String toString() {
        return "ForbiddenBean{" +
                "width=" + width +
                ", height=" + height +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                ", focus=" + focus +
                ", path=" + path +
                ", forbiddenRectF=" + forbiddenRectF +
                ", mPatin=" + mPatin +
                ", pathPaint=" + pathPaint +
                ", bgRectFPaint=" + bgRectFPaint +
                ", bmpDelete=" + bmpDelete +
                ", bmpScale=" + bmpScale +
                ", delRectF=" + delRectF +
                ", scaleRectF=" + scaleRectF +
                ", canvas=" + canvas +
                '}';
    }

    public Bitmap getAvatar(Resources res, int drawableId, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, drawableId, options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(res, drawableId, options);
    }
}
