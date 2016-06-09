package ru.imunit.maquiz.models;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public class GameModel implements IGameModel {

    // playback won't start from a position later than this
    private static final float PLAYBACK_START_THRESHOLD = 0.75f;

    private IDataSource mDataSource;
    private List<ModelUpdateListener> mListeners;
    private List<DBTrack> mTracks;
    private HashMap<DBTrack, Integer> mGuess;
    private HashMap<DBTrack, Integer> mCorrectGuess;
    private List<Long> mGuessTime;
    private DBTrack mCorrectTrack;
    private int mCurrentRound;
    private int mRoundsCount;
    private long mTimerData;
    private long mRoundScore;
    private long mGameScore;
    private int mOptionsCount;

    public GameModel(IDataSource dataSrc, int options, int rounds) {
        mDataSource = dataSrc;
        mListeners = new ArrayList<>();
        mTracks = new ArrayList<>();
        mGuess = new HashMap<>();
        mCorrectGuess = new HashMap<>();
        mGuessTime = new ArrayList<>();
    }

    // Game logic

    private void initGame(int options, int rounds) {
        mTracks.clear();
        mGuess.clear();
        mCorrectGuess.clear();
        mGuessTime.clear();
        mOptionsCount = options;
        mRoundsCount = rounds;
        mCurrentRound = 0;
        mTimerData = 0;
        mRoundScore = 0;
        mGameScore = 0;
    }

    private Handler timerHandler = new Handler();
    private long mStartTime;
    private Runnable timerUpdate = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - mStartTime;
            mTimerData += millis;
            for (ModelUpdateListener listener : mListeners) {
                listener.onTimerUpdated(mTimerData);
            }
            timerHandler.postDelayed(this, 10);
        }
    };

    // Public interface

    public void nextRound() {
        mCurrentRound++;
        if (mCurrentRound > mRoundsCount) {
            // End game and show results
        }
        // obtain random tracks from data source
        mTracks = Arrays.asList(mDataSource.getRandomTracks(mOptionsCount));
        // set correct tracks randomly
        mCorrectTrack = mTracks.get(new Random().nextInt(mOptionsCount));
        mTimerData = 0;
        mRoundScore = 0;
        // notify all listeners
        for (ModelUpdateListener listener : mListeners) {
            listener.onRoundUpdated();
        }
    }

    public void startPlayback() {
        float pos = (new Random().nextFloat()) / PLAYBACK_START_THRESHOLD;
        for (ModelUpdateListener listener : mListeners) {
            listener.onPlaybackStarted(pos);
        }
        mStartTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerUpdate, 0);
    }

    public void makeGuess(DBTrack track) {
        boolean result = track.equals(mCorrectTrack);
        if (result) {
            timerHandler.removeCallbacks(timerUpdate);
            mGameScore += mRoundScore;
            for (ModelUpdateListener listener : mListeners) {
                listener.onScoreUpdated(mRoundScore);
            }
        }
        for (ModelUpdateListener listener : mListeners) {
            listener.onGuessVerified(true);
        }
    }

    @Override
    public int getCurrentRound() {
        return mCurrentRound;
    }

    @Override
    public int getRoundsCount() {
        return mRoundsCount;
    }

    @Override
    public long getTimerData() {
        return mTimerData;
    }

    @Override
    public long getGameScore() {
        return mGameScore;
    }

    @Override
    public long getRoundScore() {
        return mRoundScore;
    }

    @Override
    public List<DBTrack> getTracks() {
        return mTracks;
    }

    @Override
    public DBTrack getCorrectTrack() {
        return mCorrectTrack;
    }

    public interface ModelUpdateListener {
        void onRoundUpdated();
        void onScoreUpdated(long diff);
        void onTimerUpdated(long time);
        void onGuessVerified(boolean result);
        void onPlaybackStarted(float position);
    }
}
