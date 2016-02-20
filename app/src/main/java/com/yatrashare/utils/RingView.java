package com.yatrashare.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.yatrashare.R;

/**
 * Created by KANDAGATLAS on 11-02-2016.
 */
public class RingView extends View {
    private Paint ringPaint;
    private int ringColor;
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public RingView(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see View(android.content.Context, android.util.AttributeSet, int)
     */
    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ringPaint = new Paint();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ringAttrs, 0, 0);
        try {
            ringColor = typedArray.getInteger(R.styleable.ringAttrs_ringColor, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;
        int radius = 0;

        if (viewWidthHalf > viewHeightHalf) {
            radius = viewWidthHalf - 10;
        } else {
            radius = viewHeightHalf - 10;
        }

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(4);
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(ringColor);

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, ringPaint);

    }
}
