package com.example.demos.drag.DragLayout;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.demos.R;
import com.example.demos.utils.DensityUtil;


public class DragImageView extends android.support.v7.widget.AppCompatImageView implements View.OnLongClickListener, View.OnDragListener {

    private static final String TAG = "DragImageView";

    private Paint mPaint;
    private int width;
    private int height;
    private int centerX;
    private int centerY;
    private Path path;
    private PointF position;
    private String sequence;
    private CharSequence dragData;
    private int roomNum;
    private Drawable mDrawable;

    public DragImageView(Context context) {
        this(context, null);
    }

    public DragImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        path = new Path();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setOnLongClickListener(this);
        setOnDragListener(this);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getPosition() {
        return position;
    }

    private void setCleanSequence(String sequence) {
        this.sequence = sequence;
        setTag(sequence);
        postInvalidate();
        mDrawable = new RoomDrawable(sequence);
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
        setCleanSequence(String.valueOf(roomNum + 1));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        Log.d(TAG, "onSizeChanged : width = " + width + " , height = " + height);
        centerX = width / 2;
        centerY = height / 2;
        path.reset();
        path.addCircle(centerX, centerY, w / 2f, Path.Direction.CCW);
        if (mDrawable != null) {
            mDrawable.setBounds(0, 0, w, h);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = DensityUtil.dp2px(getContext(), 50);
        int sizeHeight = DensityUtil.dp2px(getContext(), 50);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        drawCircle(canvas);
//        drawText(canvas);
        mDrawable.draw(canvas);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));
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
        mPaint.reset();
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(getWidth() / 1.5f);
        float textWidth = mPaint.measureText(sequence);
        //文字的x坐标
        float x = (getWidth() - textWidth) / 2.0f;
        //文字的y坐标
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = (getHeight() + (Math.abs(fontMetrics.ascent) - fontMetrics.descent)) / 2.0f;
        canvas.drawText(sequence, x, y, mPaint);
    }


    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick ==== ");
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag
        Object tag = v.getTag();
        if (tag == null) {
            return true;
        }
        ClipData.Item item = new ClipData.Item(tag.toString());

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        ClipData dragData = new ClipData(
                tag.toString(),
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);

        // Instantiates the drag shadow builder.
        View.DragShadowBuilder myShadow = new MyDragShadowBuilder(this);

        // Starts the drag

        v.startDrag(dragData,  // the data to be dragged
                myShadow,  // the drag shadow builder
                null,      // no need to use local data
                0          // flags (not currently used, set to 0)
        );
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        ImageView view = (ImageView) v;

        // Defines a variable to store the action type for the incoming event
        final int action = event.getAction();
        // Handles each of the expected events
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.d("onDrag ", "DragEvent.ACTION_DRAG_STARTED");
                    // As an example of what your application might do,
                    // applies a blue color tint to the View to indicate that it can accept
                    // data.
                    view.setColorFilter(Color.parseColor("#5540E5F8"));
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    // returns true to indicate that the View can accept the dragged data.
                    return true;
                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("onDrag ", "DragEvent.ACTION_DRAG_ENTERED");
                // Applies a green tint to the View. Return true; the return value is ignored.

                view.setColorFilter(Color.parseColor("#55BCFD67"));

                // Invalidate the view to force a redraw in the new tint
                v.invalidate();

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.d("onDrag ", "DragEvent.ACTION_DRAG_LOCATION");

                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("onDrag ", "DragEvent.ACTION_DRAG_LOCATION");
                // Re-sets the color tint to blue. Returns true; the return value is ignored.
                view.setColorFilter(Color.parseColor("#5540E5F8"));

                // Invalidate the view to force a redraw in the new tint
                v.invalidate();

                return true;

            case DragEvent.ACTION_DROP:
                Log.d("onDrag ", "DragEvent.ACTION_DROP");
                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);

                // Gets the text data from the item.
                dragData = item.getText();


                if (this.getTag().equals(dragData)) {
                    Log.d(TAG, "onDrag , but is self on drag,return");
                    dragData = null;
                    return true;
                }

                // Displays a message containing the dragged data.
                Toast.makeText(getContext(), "Dragged data is " + dragData, Toast.LENGTH_LONG).show();
                // Turns off any color tints
                view.clearColorFilter();
                view.setImageResource(R.drawable.ic_launcher_background);

                // Invalidates the view to force a redraw
                v.invalidate();

                // Returns true. DragEvent.getResult() will return true.
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("onDrag ", "DragEvent.ACTION_DRAG_ENDED");
                // Turns off any color tinting
                view.clearColorFilter();

                // Invalidates the view to force a redraw

                // Does a getResult(), and displays what happened.
                if (event.getResult()) {
//                    Toast.makeText(getContext(), "The drop was handled.data = " + dragData,
//                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "true : dragData = " + dragData + " , curData = " + this.getTag());
                } else {
//                    Toast.makeText(getContext(), "The drop didn't work.data = " + dragData,
//                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "false : dragData = " + dragData + " , curData = " + this.getTag());
                }

                v.invalidate();
                // returns true; the value is ignored.
                dragData = null;
                return true;

            // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(DragImageView v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = v.mDrawable;
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = (int) (getView().getWidth() );

            // Sets the height of the shadow to half the height of the original View
            height = (int) (getView().getHeight() );

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }
}
