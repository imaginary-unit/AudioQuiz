package ru.imunit.maquiz;

import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import java.io.File;
import java.io.IOException;

import ru.imunit.maquiz.views.widgets.AudioVisualizer;
import ru.imunit.maquiz.views.widgets.InfoBar;
import ru.imunit.maquiz.views.widgets.TrackView;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

public class TestActivity extends AppCompatActivity implements View.OnTouchListener {

    TrackView tv;
    MediaPlayer mp;
    // Visualizer vis;
    // AudioVisualizer av;
    InfoBar ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testTrackViewer();
        try {
            testInfoBar();
        } catch (IOException e) {
            Log.i("Visualizer test error", e.getMessage());
        }
    }

    private void testInfoBar() throws IOException {

        IDataSource dataSource = DataSourceFactory.getDataSource(this);
        dataSource.openReadable();
        DBTrack[] tracks = dataSource.getRandomTracks(1);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDataSource(this, Uri.fromFile(new File(tracks[0].getUri())));
        mp.prepare();

        ib = (InfoBar)findViewById(R.id.testInfoBar);
        ib.setAudioSessionId(mp.getAudioSessionId());
        ib.setInfoText("Significantly long testing text!!");

        mp.start();
    }

    private void testVisualizer() throws IOException {

        IDataSource dataSource = DataSourceFactory.getDataSource(this);
        dataSource.openReadable();
        DBTrack[] tracks = dataSource.getRandomTracks(1);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDataSource(this, Uri.fromFile(new File(tracks[0].getUri())));
        mp.prepare();

//        av = (AudioVisualizer)findViewById(R.id.testVisualizer);
//        av.setDivisions(2);
//        av.setBarHeight(0.4f);
//        av.setAudioSessionId(mp.getAudioSessionId());

//        vis = new Visualizer(mp.getAudioSessionId());
//        vis.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
//        vis.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//            @Override
//            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
//
//            }
//            @Override
//            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
//                av.update(bytes);
//                // Log.i("FFT data samples:", String.valueOf(bytes.length));
//            }
//        }, Visualizer.getMaxCaptureRate() / 2, false, true);
//        vis.setEnabled(true);

        mp.start();
    }

    private void testTrackViewer() {
        tv = (TrackView)findViewById(R.id.testTrackView);
        tv.setOnTouchListener(this);
        tv.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Log.i("Anim listener", "Animation completed");
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
            //Log.d("TouchTest", "Touch down");
            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            mout = false;
            tv.animateTouchDown();
            if (ib != null) {
                ib.showTextInfo(0);
            }
        }
        if (evt.getAction() == MotionEvent.ACTION_UP) {
            if (!mout) {
                //Log.d("TouchTest", "Touch up");
                tv.animateTouchUp(false);
                if (ib != null) {
                    ib.hideTextInfo();
                }
            }
        }
        if (evt.getAction() == MotionEvent.ACTION_MOVE) {
            if (!mout && !rect.contains(v.getLeft() + (int)evt.getX(), v.getTop() + (int)evt.getY())) {
                mout = true;
                //Log.d("TouchTest", "Move out");
                tv.animateTouchAway();
                return true;
            }
        }
        return true;
    }
}
