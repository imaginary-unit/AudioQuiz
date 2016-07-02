package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ru.imunit.maquiz.views.widgets.TrackView;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragmentListener} interface
 * to handle interaction events.
 */
public class GameFragment extends Fragment
            implements GameModel.ModelUpdateListener {

    private IGameModel mModel;
    private GameFragmentListener mListener;
    private TextView mTextRound;
    private TextView mTextScore;
    private TextView mTextTime;
    private LinearLayout mTracksLayout;
    private MediaPlayer mMediaPlayer;

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
        mTextRound = (TextView)getView().findViewById(R.id.textTracks);
        mTextScore = (TextView)getView().findViewById(R.id.textScore);
        mTextTime = (TextView)getView().findViewById(R.id.textTime);
        mTracksLayout = (LinearLayout)getView().findViewById(R.id.layoutTracks);
        mListener.onGameFragmentInitialized();
        if (mModel.isGameRunning()) {
            onRoundUpdated();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModel.isGameRunning() && mModel.getPlaybackTime() > 0) {
            onPlaybackStarted();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    /**
     *  Model events listener
     */

    public void onRoundUpdated() {
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
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof TrackView) {
                        TrackView t = (TrackView)v;
                        mListener.onMakeGuess(t.getTrack());
                    }
                }
            });
            mTracksLayout.addView(tv);
        }

        // TODO: play metronome before playback
        // mListener.onStartPlayback();
    }

    @Override
    public void onScoreUpdated(long diff) {
        mTextScore.setText(String.valueOf(mModel.getGameScore()));
        // TODO: show somewhere else...
        Toast.makeText(getActivity(), String.format(Locale.ENGLISH, "+ %d",
                mModel.getRoundScore()), Toast.LENGTH_SHORT).show();
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
            mMediaPlayer.release();
            // display correct guess notification, congrats and so on (may be animated..
            // ..so consider extraction to another method
            mListener.onNextRound();
        } else if (result == GameModel.GUESS_RESULT_WRONG_CONTINUE) {
            // display wrong guess notification, animation, whatever..
        }
        else {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            // display failed notification and move to the next round
            mListener.onNextRound();
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
            // TODO: handle exception
        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int len = mMediaPlayer.getDuration();
                int start = (int)(len * mModel.getPlaybackStartPos()) + mModel.getPlaybackTime();
                if (start > len) start = start - len;

                mMediaPlayer.seekTo(start);
                Log.i("Playing media from:", String.format("%d / %d", start, len));
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface GameFragmentListener {
        void onGameFragmentInitialized();
        void onNextRound();
        void onStartPlayback();
        void onMediaReady();
        void onMakeGuess(DBTrack track);
        void onGameFinished();
    }
}
