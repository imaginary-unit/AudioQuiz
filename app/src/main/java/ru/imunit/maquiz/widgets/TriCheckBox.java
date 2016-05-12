package ru.imunit.maquiz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import ru.imunit.maquiz.R;

/**
 * Created by smirnov on 23.10.2015.
 */
public class TriCheckBox extends View {

    public static final int STATE_UNCHECKED = 0;
    public static final int STATE_CHECKED = 1;
    public static final int STATE_UNDEFINED = -1;
    private int mState;
    private int mColor1;
    private int mColor2;
    private Paint mPaint;
    RectF mDrawRect;

    public TriCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriCheckBox);
        mState = a.getInteger(R.styleable.TriCheckBox_state, STATE_UNCHECKED);
        mColor1 = a.getColor(R.styleable.TriCheckBox_color1,
                getResources().getColor(R.color.colorForegroundHalf));
        mColor2 = a.getColor(R.styleable.TriCheckBox_color2,
                getResources().getColor(R.color.colorAccent));
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mDrawRect = new RectF();
    }

    public TriCheckBox(Context context) {
        super(context, null);
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int defaultWidth = 48;
        int defaultHeight = 48;

        int widthMode = MeasureSpec.getMode(widthSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        }
        else {
            width = defaultWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        }
        else {
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int a = Math.min(w, h);  // side of a square
        float r = 4f;   // corner radius

        if (mState == STATE_CHECKED) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mColor2);
            mDrawRect.set(w / 2 - a / 2, h / 2 - a / 2,
                    w / 2 + a / 2, h / 2 + a / 2);
            canvas.drawRoundRect(mDrawRect, r, r, mPaint);
        }
        else if (mState == STATE_UNCHECKED) {
            float sw = Math.min(w,h) / 7f;  // stroke width
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(sw);
            mPaint.setColor(mColor1);
            mDrawRect.set(w / 2 - a / 2 + sw / 2, h / 2 - a / 2 + sw / 2,
                    w / 2 + a / 2 - sw / 2, h / 2 + a / 2 - sw / 2);
            canvas.drawRoundRect(mDrawRect, r, r, mPaint);
        }
        else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mColor1);
            mDrawRect.set(w / 2 - a / 2, h / 2 - a / 2,
                    w / 2 + a / 2, h / 2 + a / 2);
            canvas.drawRoundRect(mDrawRect, r, r, mPaint);
        }
    }
}
