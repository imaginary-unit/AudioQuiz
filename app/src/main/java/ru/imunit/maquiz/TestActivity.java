package ru.imunit.maquiz;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import ru.imunit.maquiz.views.widgets.TrackView;

public class TestActivity extends AppCompatActivity implements View.OnTouchListener {

    TrackView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv = (TrackView)findViewById(R.id.testTrackView);
        tv.setOnTouchListener(this);
        tv.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("Anim listener", "Animation completed");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    Rect rect;
    boolean mout;
    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        if (evt.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("TouchTest", "Touch down");
            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            mout = false;
            tv.animateTouchDown();
        }
        if (evt.getAction() == MotionEvent.ACTION_UP) {
            if (!mout) {
                Log.d("TouchTest", "Touch up");
                tv.animateTouchUp(false);
            }
        }
        if (evt.getAction() == MotionEvent.ACTION_MOVE) {
            if (!mout && !rect.contains(v.getLeft() + (int)evt.getX(), v.getTop() + (int)evt.getY())) {
                mout = true;
                Log.d("TouchTest", "Move out");
                tv.animateTouchAway();
                return true;
            }
        }
        return true;
    }
}
