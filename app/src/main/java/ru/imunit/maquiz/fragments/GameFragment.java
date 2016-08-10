package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.GameModel;
import ru.imunit.maquiz.models.IGameModel;
import ru.imunit.maquiz.views.widgets.InfoBar;
import ru.imunit.maquiz.views.widgets.TrackView;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragmentListener} interface
 * to handle interaction events.
 */
public class GameFragment extends Fragment implements
        GameModel.ModelUpdateListener,
        View.OnTouchListener {

    private boolean mMetronomePlaying;
    private boolean mUiLock = false;
    private IGameModel mModel;
    private GameFragmentListener mListener;
    private TextView mTextRound;
    private TextView mTextScore;
    private TextView mTextTime;
    private LinearLayout mTracksLayout;
    private MediaPlayer mMediaPlayer;
    private InfoBar mInfoBar;

    public GameFragment() {
        // Required empty public constructor
    }

    public void setModel(IGameModel model) {
        mModel = model;
    }

    public IGameModel getModel() {
        return mModel;
    }

    /**
     * Fragment lifecycle handlers
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("DEBUG", "onCreateView()");
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameFragmentListener) {
            mListener = (GameFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameFragmentListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextRound = (TextView)getView().findViewById(R.id.textTracks);
        mTextScore = (TextView)getView().findViewById(R.id.textScore);
        mTextTime = (TextView)getView().findViewById(R.id.textTime);
        mTracksLayout = (LinearLayout)getView().findViewById(R.id.layoutTracks);
        mInfoBar = (InfoBar)getView().findViewById(R.id.infoBar);
        mListener.onGameFragmentInitialized();
        if (mModel.isGameRunning()) {
            updateRoundUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMetronomePlaying = false;
        if (mInfoBar != null)
            mInfoBar.releaseAudioSession();
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        Log.i("DEBUG", "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mInfoBar != null)
            mInfoBar.releaseAudioSession();
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        Log.i("DEBUG", "onStop()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModel.isPlaybackStarted()) {
            onPlaybackStarted();
        }
        else if (mModel.isGameRunning()) {
            onRoundUpdated();
        }
        Log.i("DEBUG", "onResume()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mInfoBar != null)
            mInfoBar.releaseAudioSession();
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    /**
     *  Model events listener
     */

    public void onRoundUpdated() {
        updateRoundUi();
        if (mModel.isMetronomeEnabled()) {
            /* the inner condition guarantees that we don't start the metronome twice
               because of onResume being called on fragment start
             */
             if (!mMetronomePlaying) {
                mMetronomePlaying = true;
                mUiLock = true;
                mMediaPlayer = MediaPlayer.create(getContext(), R.raw.metronome_cut);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mMediaPlayer.release();
                        mInfoBar.releaseAudioSession();
                        mMetronomePlaying = false;
                        mUiLock = false;
                        mListener.onStartPlayback();
                    }
                });
                mInfoBar.setAudioSessionId(mMediaPlayer.getAudioSessionId());
                mMediaPlayer.start();
             }
        } else {
            mListener.onStartPlayback();
        }
    }

    @Override
    public void onScoreUpdated(long diff) {
        mTextScore.setText(String.valueOf(mModel.getGameScore()));
        // TODO: show somewhere else...
        mInfoBar.setInfoText(String.format(Locale.ENGLISH, "+ %d",
                mModel.getRoundScore()));
        mInfoBar.showTextInfo(1000);
//        Toast.makeText(getActivity(), String.format(Locale.ENGLISH, "+ %d",
//                mModel.getRoundScore()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerUpdated(int time) {
        mTextTime.setText(String.format(Locale.ENGLISH, "%.2f", (float)time / 1E3f));
    }

    @Override
    public void onGuessVerified(int result) {
        if (result == GameModel.GUESS_RESULT_CORRECT) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mInfoBar.releaseAudioSession();
            mMediaPlayer.release();

            // show correct guess animation and load next round after it has finished
            tempTrackView.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mListener.onNextRound();
                    // TODO: do something with this in future updates..
                    if (!mModel.isMetronomeEnabled())
                        mUiLock = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            mUiLock = true;
            tempTrackView.animateTouchUp(true);

        } else if (result == GameModel.GUESS_RESULT_WRONG_CONTINUE) {
            // show wrong guess animation and disable this item
            tempTrackView.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    // tempTrackView.setVisibility(View.GONE);
                    tempTrackView.disable();
                    tempTrackView.setEnabled(false);
                    mUiLock = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            mUiLock = true;
            tempTrackView.animateTouchUp(false);
        }
        else {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mInfoBar.releaseAudioSession();
            mMediaPlayer.release();
            // display failed notification and move to the next round
            tempTrackView.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mListener.onNextRound();
                    // see above to-do label
                    if (!mModel.isMetronomeEnabled())
                        mUiLock = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            mUiLock = true;
            tempTrackView.animateTouchUp(false);
        }
    }

    @Override
    public void onPlaybackStarted() {
        Uri trackUri = Uri.fromFile(new File(mModel.getCorrectTrack().getUri()));
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getContext(), trackUri);
        } catch (IOException e) {
            mListener.onMusicReadError();
            return;
        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int len = mMediaPlayer.getDuration();
                int start = (int)(len * mModel.getPlaybackStartPos()) + mModel.getPlaybackTime();
                if (start > len) start = start - len;

                mMediaPlayer.seekTo(start);
                Log.i("Playing media from:", String.format("%d / %d", start, len));
                mInfoBar.setAudioSessionId(mMediaPlayer.getAudioSessionId());
                mMediaPlayer.start();
                /* TODO: this call on each playback start is basically a time bomb..
                        consider refactoring
                 */
                mListener.onMediaReady();
            }
        });
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onGameFinished() {
        mListener.onGameFinished();
    }

    private void updateRoundUi() {
        mTextRound.setText(String.format(Locale.ENGLISH, "%d / %d",
                mModel.getCurrentRound(), mModel.getRoundsCount()));
        mTextTime.setText(String.format(Locale.ENGLISH, "%.2f",
                ((float)mModel.getPlaybackTime() / 1E3f)));
        mTextScore.setText(String.valueOf(mModel.getGameScore()));

        mTracksLayout.removeAllViews();
        List<DBTrack> tracks = mModel.getTracks();
        for (DBTrack track : tracks) {
            TrackView tv = new TrackView(getActivity());
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
            tv.setTrack(track);
            tv.setOnTouchListener(this);
            if (mModel.isTrackGuessed(track)) {
                tv.disable();
                tv.setEnabled(false);
            }
            mTracksLayout.addView(tv);
        }
    }

    // these are needed to handle dragging out of item bounds
    private Rect tvRect;
    private boolean tvMovOut;
    // store last clicked TV to trigger the animation when model returns the guess result
    private TrackView tempTrackView;
    // track view touch handler
    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        TrackView tv = (TrackView)v;
        if (mUiLock || tv == null)
            return false;

        if (evt.getAction() == MotionEvent.ACTION_DOWN) {
            tvRect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            tvMovOut = false;
            tv.animateTouchDown();
        }
        else if (evt.getAction() == MotionEvent.ACTION_UP) {
            if (!tvMovOut) {
                tempTrackView = tv;
                mListener.onMakeGuess(tv.getTrack());
                // tv.animateTouchUp(false);
            }
        }
        else if (evt.getAction() == MotionEvent.ACTION_MOVE) {
            if (!tvMovOut && !tvRect.contains(v.getLeft() + (int)evt.getX(), v.getTop() + (int)evt.getY())) {
                tvMovOut = true;
                tv.animateTouchAway();
            }
        }
//        else if (!tvMovOut && !tvRect.contains(v.getLeft() + (int)evt.getX(), v.getTop() + (int)evt.getY())) {
//            tvMovOut = true;
//            tv.animateTouchAway();
//            Log.i("Touch event", "touch out");
//        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this fragment
     */
    public interface GameFragmentListener {
        void onGameFragmentInitialized();
        void onNextRound();
        void onStartPlayback();
        void onMediaReady();
        void onMakeGuess(DBTrack track);
        void onGameFinished();
        void onMusicReadError();
    }
}
