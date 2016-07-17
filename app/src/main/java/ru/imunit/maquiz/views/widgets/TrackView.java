package ru.imunit.maquiz.views.widgets;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 09.06.16.
 */
public class TrackView extends FrameLayout {

    public TrackView(Context context) {
        super(context);
        init();
    }

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTrack(DBTrack track) {
        mTrack = track;
        mArtist.setText(track.getArtist());
        mTitle.setText(track.getName());
    }

    public DBTrack getTrack() {
        return mTrack;
    }

    public void animateTouchDown() {
        mHighlight.getLayoutParams().width = 1;
        mHighlight.setBackground(mDrawable);
        mHighlight.requestLayout();
        HighlightAnim anim = new HighlightAnim(mHighlight, this.getWidth());
        anim.setDuration(ANIM_TIME);
        mHighlight.startAnimation(anim);
    }

    public void animateTouchAway() {
        HighlightAnim anim = new HighlightAnim(mHighlight, this.getWidth());
        anim.setReverse(true);
        anim.setDuration(ANIM_TIME);
        mHighlight.startAnimation(anim);
    }

    public void animateTouchUp(boolean correct) {
        mHighlight.clearAnimation();
        mHighlight.getLayoutParams().width = this.getWidth();
        int color = correct ? 0x7700ff00 : 0x77ff0000;
        BlinkAnim anim = new BlinkAnim(mHighlight, color);
        anim.setDuration(BLINK_TIME);
        mHighlight.startAnimation(anim);
    }

    public void testResize() {
        mHighlight.getLayoutParams().width += 10;
        mHighlight.requestLayout();
    }

    private void init() {
        inflate(getContext(), R.layout.recycler_item_2, this);
        mIcon = (ImageView)findViewById(R.id.icon);
        mArtist = (TextView)findViewById(R.id.firstLine);
        mTitle = (TextView)findViewById(R.id.secondLine);
        mHighlight = (ImageView)findViewById(R.id.highlightBitmap);
        mDrawable = createGradient(getResources().getColor(R.color.colorForegroundHalf));
        // mHlDrawable = getResources().getDrawable(R.drawable.opt_highlight);
        // mIcon.setVisibility(View.GONE);
    }

    private DBTrack mTrack;
    private ImageView mIcon;
    private TextView mArtist;
    private TextView mTitle;

    private ImageView mHighlight;
    private PaintDrawable mDrawable;

    private int ANIM_TIME = 100;
    private int BLINK_TIME = 300;

    private class HighlightAnim extends Animation {

        public HighlightAnim(View view, int targetSize) {
            mView = view;
            mTargetSize = targetSize;
            mReverse = false;
        }

        public void setReverse(boolean state) {
            mReverse = state;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float coeff = mReverse ? (1f - interpolatedTime) : interpolatedTime;
            mView.getLayoutParams().width = (int) (mTargetSize * coeff);
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }

        private View mView;
        private int mTargetSize;
        private boolean mReverse;
    }

    private class BlinkAnim extends Animation {

        public BlinkAnim(View v, int color) {
            mView = v;
            mColor = color;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            // divide the [0; 1] interval to BLINKS equal intervals
            boolean even = (int)(interpolatedTime * BLINKS) % 2 == 0;
            // show view while even interval, hide while odd
            //int vis = even ? VISIBLE : INVISIBLE;
            //mView.setVisibility(vis);
            int w = even ? mWidth : 0;
            mView.getLayoutParams().width = w;
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            PaintDrawable grad = createGradient(mColor);
            mView.setBackground(grad);
            mWidth = mView.getLayoutParams().width;
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }

        private int mWidth;
        private int mColor;
        private View mView;
        private final int BLINKS = 5;
    }

    private static PaintDrawable createGradient(final int color) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int w, int h) {
                LinearGradient linearGradient = new LinearGradient(0, 0, w, 0,
                        new int[] { 0x00000000, color, color, 0x00000000 },
                        new float[] { 0, 0.15f, 0.85f, 1f },
                        Shader.TileMode.REPEAT);
                return linearGradient;
            }
        };
        PaintDrawable pd = new PaintDrawable();
        pd.setShape(new RectShape());
        pd.setShaderFactory(shaderFactory);
        return pd;
    }
}
