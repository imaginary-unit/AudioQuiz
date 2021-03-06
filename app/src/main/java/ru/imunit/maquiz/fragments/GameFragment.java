package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

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

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


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
    private GridLayout mTracksLayout;
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

        mTracksLayout = (GridLayout)getView().findViewById(R.id.layoutTracks);
        mTextRound = (TextView)getView().findViewById(R.id.textTracks);
        mTextScore = (TextView)getView().findViewById(R.id.textScore);
        mTextTime = (TextView)getView().findViewById(R.id.textTime);
        mInfoBar = (InfoBar)getView().findViewById(R.id.infoBar);
        mInfoBar.init();
        mListener.onGameFragmentInitialized();
        if (mModel.isGameRunning()) {
            updateRoundUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMetronomePlaying = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
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
                        mMediaPlayer = null;
                        mMetronomePlaying = false;
                        mUiLock = false;
                        mListener.onStartPlayback();
                    }
                });
                mInfoBar.showDots();
                mMediaPlayer.start();
             }
        } else {
            mListener.onStartPlayback();
        }
    }

    @Override
    public void onScoreUpdated(long diff) {
        mTextScore.setText(String.valueOf(mModel.getGameScore()));
        mInfoBar.setInfoText(String.format(Locale.ENGLISH, "+ %d",
                mModel.getRoundScore()));
        mInfoBar.showInfoText();
    }

    @Override
    public void onTimerUpdated(int time) {
        mTextTime.setText(String.format(Locale.ENGLISH, "%.1f", (float)time / 1E3f));
    }

    @Override
    public void onGuessVerified(int result) {
        if (result == GameModel.GUESS_RESULT_CORRECT) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

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
            mMediaPlayer.release();
            mMediaPlayer = null;
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
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
                mMediaPlayer.setLooping(true);
                mInfoBar.showSpeakers();
                mMediaPlayer.start();
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
        onTimerUpdated(mModel.getPlaybackTime());
        mTextScore.setText(String.valueOf(mModel.getGameScore()));

        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            mTracksLayout.removeAllViews();
            List<DBTrack> tracks = mModel.getTracks();
            int len = tracks.size();
            for (int i=0; i < len; i++) {
                TrackView tv = new TrackView(getActivity());
                GridLayout.Spec rowSpec = GridLayout.spec(i);
                GridLayout.Spec columnSpec = GridLayout.spec(0);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowSpec, columnSpec);
                tv.setLayoutParams(lp);
                //tv.setLayoutParams(new GridLayout.LayoutParams(
                //        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
                tv.setTrack(tracks.get(i));
                tv.setOnTouchListener(this);
                if (mModel.isTrackGuessed(tracks.get(i))) {
                    tv.disable();
                    tv.setEnabled(false);
                }
                mTracksLayout.addView(tv);
            }
        } else {
            mTracksLayout.removeAllViews();
            List<DBTrack> tracks = mModel.getTracks();
            int len = tracks.size();
            for (int i=0; i < len; i++) {
                TrackView tv = new TrackView(getActivity());
                GridLayout.Spec rowSpec = GridLayout.spec(i / 2);
                GridLayout.Spec columnSpec = GridLayout.spec(i % 2, 1.0f);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowSpec, columnSpec);
                lp.width = 0;
                tv.setLayoutParams(lp);
                tv.setTrack(tracks.get(i));
                tv.setOnTouchListener(this);
                if (mModel.isTrackGuessed(tracks.get(i))) {
                    tv.disable();
                    tv.setEnabled(false);
                }
                mTracksLayout.addView(tv);
            }
        }
    }

    // these are needed to handle dragging out of item bounds
    private Rect tvRect;
    private boolean tvMovOut;
    // store last clicked TV to trigger the animation when model returns the guess result
    private TrackView tempTrackView;
    // ic_file_music view touch handler
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
