package ru.imunit.maquiz.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.managers.SettingsManager;

/**
 * Created by theuser on 31.07.16.
 */

public class InfoBar extends FrameLayout {

    public InfoBar(Context context) {
        super(context);
        init();
    }

    public InfoBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAudioSessionId(int sessionId) {
        if (mVisualizerEnabled)
            mAVisualizer.setAudioSessionId(sessionId);
    }

    public void releaseAudioSession() {
        if (mVisualizerEnabled)
            mAVisualizer.release();
    }

    public void setInfoText(CharSequence text) {
        mTextInfo.setText(text);
    }

    public void setInfoText(int resId) {
        mTextInfo.setText(resId);
    }

    public void showTextInfo(int duration) {
        crossfade(mTextInfo, mAVisualizer, duration);
    }

    public void hideTextInfo() {
        crossfade(mAVisualizer, mTextInfo, 0);
    }

    private void init() {
        inflate(getContext(), R.layout.info_bar, this);
        mVisualizerEnabled = new SettingsManager(getContext()).getVisualizerState();
        mAVisualizer = (AudioVisualizer) findViewById(R.id.visualizer);
        if (mVisualizerEnabled) {
            mAVisualizer.setDivisions(2);
            mAVisualizer.setBarHeight(0.4f);
        }
        mTextInfo = (TextView)findViewById(R.id.textInfo);
        mTextInfo.setVisibility(View.GONE);
    }

    private void crossfade(final View inView, final View outView, final int reverseDelay) {

        inView.setAlpha(0f);
        inView.setVisibility(View.VISIBLE);
        inView.animate()
                .alpha(1f)
                .setDuration(FADE_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (reverseDelay > 0) {  // run reverse animation
                            RunnableCF r = new RunnableCF(outView, inView);
                            mHandler.postDelayed(r, reverseDelay + FADE_DURATION);
                        }
                    }
                });

        outView.setAlpha(1f);
        outView.animate()
                .alpha(0f)
                .setDuration(FADE_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // TODO: can't remove this listener after it has been set..
                        // outView.setVisibility(View.GONE);
                    }
                });
    }

    private class RunnableCF implements Runnable {
        private View inView;
        private View outView;

        public RunnableCF(View inView, View outView) {
            this.inView = inView;
            this.outView = outView;
        }

        @Override
        public void run() {
            crossfade(this.inView, this.outView, 0);
        }
    }
    private Handler mHandler = new Handler();
    private AudioVisualizer mAVisualizer;
    private TextView mTextInfo;
    private boolean mVisualizerEnabled;
    private final int FADE_DURATION = 250;
}
