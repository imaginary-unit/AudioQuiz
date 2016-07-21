package ru.imunit.maquiz.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import ru.imunit.maquiz.R;

/**
 * Created by theuser on 21.07.16.
 */

public class AudioVisualizer extends View {

    public AudioVisualizer(Context context) {
        super(context);
        init();
    }

    public AudioVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void update(byte[] data) {
        mData = data;
        invalidate();
    }

    float max = (float)Math.sqrt(Byte.MAX_VALUE*Byte.MAX_VALUE*2);
    float dbMax = (float)(10 * Math.log10(max));
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null) {
            return;
        }
        if (mPoints == null || mPoints.length < mData.length) {
            mPoints = new float[mData.length * 2];
        }

        int w = getWidth();
        int h = getHeight();
        for (int i=0; i < mData.length / 2; i++) {
            int re = mData[i*2];
            int im = mData[i*2+1];
            float mag = re*re + im*im;
            float dbValue = (float)(10 * Math.log10(mag));
            mPoints[i*4] = w * i / (mData.length / 2 - 1);      // x1
            mPoints[i*4+2] = w * i / (mData.length / 2 - 1);    // x2
            mPoints[i*4+1] = h; // y1
            mPoints[i*4+3] = (1f - dbValue / dbMax * 0.2f) * h; // y2
        }
        // canvas.drawPoints(mPoints, mPaint);
        canvas.drawLines(mPoints, mPaint);
    }

    private void init() {
        mData = null;
        mPaint = new Paint();
        mPaint.setStrokeWidth(6f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    private byte[] mData;
    private float[] mPoints;
    private Paint mPaint;
}
