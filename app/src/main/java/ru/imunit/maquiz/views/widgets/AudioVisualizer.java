package ru.imunit.maquiz.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
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

    public void setAudioSessionId(int id) {
        // mSessionId = id;
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
        }
        mVisualizer = new Visualizer(id);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

            }
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                AudioVisualizer.this.update(bytes);
                // Log.i("FFT data samples:", String.valueOf(bytes.length));
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);

        mVisualizer.setEnabled(true);
    }

    // the number to divide the data samples count by to get the desired bars count
    public void setDivisions(int divisions) {
        mDivisions = divisions;
        mPaint.setStrokeWidth(BASE_BAR_W * mDivisions);
        mMargin = BASE_BAR_W * mDivisions / 2;
    }

    // set the bar's height as a fraction of the parent view height: [0, 1]
    public void setBarHeight(float height) {
        if (height < 0f)
            mHeight = 0f;
        else if (height > 1f)
            mHeight = 1f;
        else
            mHeight = height;
    }

    private void update(byte[] data) {
        mData = data;
        invalidate();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private float max = (float)Math.sqrt(Byte.MAX_VALUE*Byte.MAX_VALUE*2);
    private float dbMax = (float)(10 * Math.log10(max));
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null) {
            return;
        }
        // divide by 2 because there is Re and Im parts of the spectrum in a single array
        // divide by mDivisions to get the desired number of bars
        int n = (mData.length / 2) / mDivisions;
        if (mPoints == null || mPoints.length < mData.length) {
            mPoints = new float[4 * n];
        }

        int w = getWidth() - (int)mMargin*2;
        int h = getHeight();
        // each bar's heights is calculated as the average of mDivisions
        // consecutive data samples Db values
        for (int i=0; i < n; i++) {
            int ii = i*2*mDivisions;
            float dbSum = 0;
            for (int j=0; j < mDivisions; j++) {
                int re = mData[ii + j*2];
                int im = mData[ii + j*2+1];
                float mag = re*re + im*im;
                dbSum += (float)(10 * Math.log10(mag));
            }
            float dbValue = dbSum / mDivisions;
            float x = w * i / (n - 1) + mMargin;
            mPoints[i*4] = x;   // x1
            mPoints[i*4+2] = x; // x2
            mPoints[i*4+1] = h; // y1
            mPoints[i*4+3] = (1f - (dbValue / dbMax) * mHeight) * h; // y2
        }
        // canvas.drawPoints(mPoints, mPaint);
        canvas.drawLines(mPoints, mPaint);
    }

    private void init() {
        mDivisions = 2;
        mHeight = 0.5f;
        mMargin = BASE_BAR_W * mDivisions / 2;
        mData = null;
        mPaint = new Paint();
        // mSessionId = 0;
        mVisualizer = null;
        mPaint.setStrokeWidth(BASE_BAR_W * mDivisions);
        mPaint.setAntiAlias(false);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    private byte[] mData;
    private float[] mPoints;
    private int mDivisions;
    private float mMargin;
    private float mHeight;
    private Paint mPaint;
    // private int mSessionId;
    private Visualizer mVisualizer;
    private final float BASE_BAR_W = 8f;
}
