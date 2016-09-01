package ru.imunit.maquiz.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.imunit.maquiz.R;


public class InfoBar extends FrameLayout {

    public enum State {
        empty,
        dots,
        speakers,
        txt
    }

    private Handler mHandler = new Handler();
    private TextView mTextInfo;
    private ImageView mSpeakerLeft;
    private ImageView mSpeakerRight;
    private ImageView mDots;
    private State mState;
    private final int FADE_DURATION = 250;

    public InfoBar(Context context) {
        super(context);
    }

    public InfoBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public void setInfoText(CharSequence text) {
        mTextInfo.setText(text);
    }

    public void setInfoText(int resId) {
        mTextInfo.setText(resId);
    }

    public void init() {
        inflate(getContext(), R.layout.info_bar, this);
        mState = State.empty;
        mSpeakerLeft = (ImageView)findViewById(R.id.speakerLeft);
        mSpeakerLeft.setVisibility(View.GONE);
        mSpeakerRight = (ImageView)findViewById(R.id.speakerRight);
        mSpeakerRight.setVisibility(View.GONE);
        mDots = (ImageView)findViewById(R.id.dots);
        mDots.setVisibility(View.GONE);
        mTextInfo = (TextView)findViewById(R.id.textInfo);
        mTextInfo.setVisibility(View.GONE);
    }

    public void showDots() {
        setState(State.dots);
    }

    public void showSpeakers() {
        setState(State.speakers);
    }

    public void showInfoText() {
        setState(State.txt);
    }

    public void hideAll() {
        setState(State.empty);
    }

    private void setState(State newState) {
        if (mState != newState) {
            switch (mState) {
                case empty:
                    break;
                case dots:
                    fadeOut(mDots);
                    break;
                case speakers:
                    fadeOut(mSpeakerLeft);
                    fadeOut(mSpeakerRight);
                    break;
                case txt:
                    fadeOut(mTextInfo);
                    break;
            }
            switch (newState) {
                case empty:
                    break;
                case dots:
                    fadeIn(mDots);
                    Drawable dots = mDots.getDrawable();
                    if (dots instanceof Animatable)
                        ((Animatable)dots).start();
                    break;
                case speakers:
                    fadeIn(mSpeakerLeft);
                    fadeIn(mSpeakerRight);
                    Drawable leftDrawable = mSpeakerLeft.getDrawable();
                    if (leftDrawable instanceof Animatable)
                        ((Animatable)leftDrawable).start();
                    Drawable rightDrawable = mSpeakerRight.getDrawable();
                    if (rightDrawable instanceof Animatable)
                        ((Animatable)rightDrawable).start();
                    break;
                case txt:
                    fadeIn(mTextInfo);
                    break;
            }
            mState = newState;
        }
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
                        // TODO: how to remove this listener after it has been set?
                        // outView.setVisibility(View.GONE);
                    }
                });
    }

    private void fadeOut(final View view) {
        view.setAlpha(1f);
        view.animate()
                .alpha(0f)
                .setDuration(FADE_DURATION);
    }

    private void fadeIn(final View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(FADE_DURATION);
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

}
